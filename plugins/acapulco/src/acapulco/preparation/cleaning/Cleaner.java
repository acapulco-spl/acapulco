package acapulco.preparation.cleaning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import acapulco.utils.FileUtils;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.base.impl.Feature;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class Cleaner {

	public static void clean(String inputFMPath, String outputFMPath) {
		
		File fmFile = new File(inputFMPath);
		IFeatureModel fm = FeatureModelManager.load(fmFile.toPath());
		printMetrics(fm);

		FeatureModelAnalyzer operator = new FeatureModelAnalyzer(fm);

		List<IFeature> dead = operator.getDeadFeatures(new NullMonitor<LiteralSet>());
		// System.out.println("Dead features: " + dead);

		for (IFeature deadF : dead) {
			//System.out.println("DeadF: " + deadF);
			fm.deleteFeature(deadF);
		}

		List<IFeature> falseOpt = operator.getFalseOptionalFeatures(new NullMonitor<List<LiteralSet>>());
		// System.out.println("False optional features: " + falseOpt);

		for (IFeature falseOptF : falseOpt) {
			//System.out.println("falseOptF: " + falseOptF);
			IFeatureStructure parent = falseOptF.getStructure().getParent();
			if (parent.isOr() || parent.isAlternative()) {
				// remove it from the group, add it as mandatory
				IFeature newGroup = new Feature(falseOptF.getFeatureModel(), parent.getFeature().getName() + "_group");
				newGroup.getStructure().setAbstract(true);
				newGroup.getStructure().setMandatory(true);

				if (parent.isOr()) {
					newGroup.getStructure().setOr();
				} else if (parent.isAlternative()) {
					newGroup.getStructure().setAlternative();
				}

				for (IFeatureStructure child : parent.getChildren()) {
					if (child != falseOptF.getStructure()) {
						newGroup.getStructure().addChild(child);
					}
				}

				for (IFeatureStructure child : newGroup.getStructure().getChildren()) {
					parent.removeChild(child);
				}

				parent.addChild(newGroup.getStructure());
				parent.changeToAnd();
				falseOptF.getStructure().setMandatory(true);

			} else {
				falseOptF.getStructure().setMandatory(true);
			}
		}

		List<IFeature> falseOpt3 = operator.getFalseOptionalFeatures(new NullMonitor<List<LiteralSet>>());
		System.out.println("False optional features: " + falseOpt3);

		List<IFeature> dead2 = operator.getDeadFeatures(new NullMonitor<LiteralSet>());
		System.out.println("Dead features: " + dead2);

		// Remove constraints with features that were removed
		List<IConstraint> noLongerValidConstraints = new ArrayList<IConstraint>();
		for (IConstraint c : fm.getConstraints()) {
			for (IFeature f : c.getContainedFeatures()) {
				if (fm.getFeature(f.getName()) == null) {
					noLongerValidConstraints.add(c);
					break;
				}
			}
		}

		for (IConstraint constraint : noLongerValidConstraints) {
			fm.removeConstraint(constraint);
		}

		// Save clean fm
		File fmOutputFileTemp = new File(inputFMPath + ".clean.xml");
		// System.out.println(fm);
		saveFM(fmOutputFileTemp, fm);

		// second load because I had errors using the same fm
		IFeatureModel fm2 = load(FileUtils.getStringOfFile(fmOutputFileTemp));
		FeatureModelAnalyzer operator2 = new FeatureModelAnalyzer(fm2);
		
		List<IConstraint> redundantConstraints = operator2.getRedundantConstraints(new NullMonitor<List<LiteralSet>>());
		System.out.println("Redundant constraints: " + redundantConstraints);

		for (IConstraint constraint : redundantConstraints) {
			fm2.removeConstraint(constraint);
		}

		FeatureModelAnalyzer operator3 = new FeatureModelAnalyzer(fm2);
		List<IConstraint> redundantConstraints2 = operator3
				.getRedundantConstraints(new NullMonitor<List<LiteralSet>>());
		System.out.println("Redundant constraints: " + redundantConstraints2);
		
		// System.out.println(fm2);
		printMetrics(fm2);
		// fmOutputFile = new File(fmPath + ".clean.sxfm.xml");
		File fmOutputFile2 = new File(outputFMPath);
		saveSXFM(fmOutputFile2, fm2);
		// delete the temporal file
		fmOutputFileTemp.delete();
	}
	
	public static void main(String[] args) {
		// Load fm
		String fmPath = args[0];
		clean(fmPath, fmPath + ".clean.sxfm.xml");
	}

	public static void printMetrics(IFeatureModel fm) {
		System.out.println("Features " + fm.getFeatures().size());
		int groups = 0;
		for (IFeature f : fm.getFeatures()) {
			if (f.getStructure().isOr() || f.getStructure().isAlternative()) {
				groups++;
			}
		}
		System.out.println("Groups " + groups);
		FeatureModelAnalyzer operator = new FeatureModelAnalyzer(fm);
		System.out.println("Core " + operator.getCoreFeatures(new NullMonitor<LiteralSet>()).size());
		System.out.println("CTCs " + fm.getConstraints().size());
	}

	/**
	 * Load fm as string
	 * 
	 * @param file
	 */
	public static IFeatureModel load(String content) {
		IFeatureModel featureModel = new FeatureModel(DefaultFeatureModelFactory.ID);
		FMFormatManager.getInstance().addExtension(new XmlFeatureModelFormat());
		IPersistentFormat<IFeatureModel> format = FMFormatManager.getInstance().getFormatByContent(content, "a.xml");
		FileHandler.loadFromString(content, featureModel, format);
		return featureModel;
	}


	public static void saveFM(File newFile, IFeatureModel fm) {
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		String fmString = format.write(fm);
		FileUtils.writeStringToFile(newFile, fmString);
	}

	public static void saveSXFM(File newFile, IFeatureModel fm) {
		SXFMFormat format = new SXFMFormat();
		String fmString = format.write(fm);
		FileUtils.writeStringToFile(newFile, fmString);
	}


}

package acapulco.preparation.cleaning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.prop4j.Literal;
import org.prop4j.Node;
import org.prop4j.Not;
import org.prop4j.Or;

import acapulco.utils.FileUtils;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class ComplexConstraintRemover {

	public static void main(String[] args) {
		String inputFMPath = args[0];
		String outputFMPath = args[1];
		removeComplexConstraints(inputFMPath, outputFMPath);
	}
	
	/**
	 * Remove complex constraints
	 * @param inputFMPath
	 * @param outputFMPath
	 * @return the list of removed constraints
	 */
	public static List<IConstraint> removeComplexConstraints(String inputFMPath, String outputFMPath) {
		File fmFile = new File(inputFMPath);
		// Load fm
		IFeatureModel fm = FeatureModelManager.load(fmFile.toPath());
		printMetrics(fm);

		List<IConstraint> simple = new ArrayList<>();
		List<IConstraint> complex = new ArrayList<>();
		for (IConstraint c : fm.getConstraints()) {
			if (isSimple(c)) {
				simple.add(c);
			} else {
				complex.add(c);
			}
		}

		System.out.println("Removed " + (fm.getConstraints().size() - simple.size()) + " complex constraints.");
		// replace the previous constraints by just keeping the simple ones
		fm.setConstraints(simple);
		printMetrics(fm);

		// Save clean fm
		File fmOutputFile = new File(outputFMPath);
		saveSXFM(fmOutputFile, fm);
		
		return complex;
	}
	

	public static boolean isSimple(IConstraint c) {
		if (c.getNode() instanceof Or) {
			Or orNode = (Or) c.getNode();
			Node[] children = orNode.getChildren();
			if (children.length == 2) {
				int nots = 0;
				int literals = 0;

				for (Node child : children) {
					if (child instanceof Not) {
						nots++;
						if ((child.getChildren()[0] instanceof Literal)) {
							literals++;
						}
					} else if (child instanceof Literal) {
						literals++;
					}
				}
				
				if (literals < 2 || nots < 1) {
					return false;
				} else return true;
			}
		}
		return false;
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

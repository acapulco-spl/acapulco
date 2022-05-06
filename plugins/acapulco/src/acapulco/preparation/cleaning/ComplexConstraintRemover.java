package acapulco.preparation.cleaning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import acapulco.featureide.utils.FeatureIDEUtils;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class ComplexConstraintRemover {

	/**
	 * Remove complex constraints
	 * 
	 * @param inputFMPath
	 * @param outputFMPath
	 * @return the list of removed constraints
	 */
	public static List<IConstraint> removeComplexConstraints(IFeatureModel fm, String outputFMPath) {
		FeatureIDEUtils.printMetrics(fm);

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
		FeatureIDEUtils.printMetrics(fm);

		// Save clean fm
		File fmOutputFile = new File(outputFMPath);
		FeatureIDEUtils.saveSXFM(fmOutputFile, fm);

		return complex;
	}

	public static boolean isSimple(IConstraint c) {
		if (FeatureIDEUtils.isImplies(c)) {
			return true;
		}
		if (FeatureIDEUtils.isExcludes(c)) {
			return true;
		}
		return false;
	}

}

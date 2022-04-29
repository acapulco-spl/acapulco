package acapulco.preparation.cleaning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.prop4j.Implies;
import org.prop4j.Literal;
import org.prop4j.Node;
import org.prop4j.Not;
import org.prop4j.Or;

import acapulco.featureide.utils.FeatureIDEUtils;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class ComplexConstraintRemover {

	/**
	 * Remove complex constraints
	 * 
	 * @param inputFMPath
	 * @param outputFMPath
	 * @return the list of removed constraints
	 */
	public static List<IConstraint> removeComplexConstraints(IFeatureModel fm, String outputFMPath) {
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
		FeatureIDEUtils.saveSXFM(fmOutputFile, fm);

		return complex;
	}

	public static boolean isSimple(IConstraint c) {
		if (isImplies(c)) {
			return true;
		}
		if (isExcludes(c)) {
			return true;
		}
		return false;
	}

	/**
	 * Is implies
	 * 
	 * not(A) or B is the basic A implies B, A or not(B) will be in the other
	 * direction
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isImplies(IConstraint c) {
		// Or-based way to define implies
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
						if (!((Literal) child).positive) {
							nots++;
						}
						literals++;
					}
				}
				if (literals == 2 && nots == 1) {
					return true;
				}
			}
		}
		// Implies-based way to define implies
		if (c.getNode() instanceof Implies) {
			Implies impliesNode = (Implies) c.getNode();
			Node[] children = impliesNode.getChildren();
			if (children.length == 2) {
				for (Node child : children) {
					if (child instanceof Literal) {
						if (!((Literal) child).positive) {
							return false;
						}
					} else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Is excludes
	 * 
	 * not(A) or not(B) is the basic A excludes B
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isExcludes(IConstraint c) {
		// Or-based way to define excludes
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
						if (!((Literal) child).positive) {
							nots++;
						}
						literals++;
					}
				}
				if (literals == 2 && nots == 2) {
					return true;
				}
			}
		}
		// Implies-based way to define excludes
		if (c.getNode() instanceof Implies) {
			Implies impliesNode = (Implies) c.getNode();
			Node[] children = impliesNode.getChildren();
			if (children.length == 2) {
				// first one must be a positive literal
				if (!(children[0] instanceof Literal)) {
					return false;
				} else {
					if (!((Literal)children[0]).positive) {
						return false;
					}
				}
				// second one must be a negated literal
				if ((children[1] instanceof Literal) && !((Literal) children[1]).positive) {
					return true;
				} else if (children[1] instanceof Not) {
					Not not = (Not) children[1];
					if ((not.getChildren()[0] instanceof Literal)) {
						if (((Literal)not.getChildren()[0]).positive){
							return true;
						}
					}
				}
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

}

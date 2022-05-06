package acapulco.featureide.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import org.prop4j.Implies;
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
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

/**
 * Feature IDE Utils
 * 
 */
public class FeatureIDEUtils {

	public static String createConfigFileContent(List<String> features) {
		StringBuffer buffer = new StringBuffer();
		for (String f : features) {
			buffer.append(f);
			buffer.append("\n");
		}
		// remove last new line
		if (!buffer.isEmpty()) {
			buffer.setLength(buffer.length() - 1);
		}
		return buffer.toString();
	}

	private static PrintStream _err;

//	public static List<String> getValidConfiguration(File fmFeatureIDE) throws TimeoutException {
//		IFeatureModel fm = FeatureIDEUtils.load(fmFeatureIDE);
//		final Configuration conf = new Configuration(fm);
//		List<List<String>> solution = conf.getSolutions(1);
//		if (!solution.isEmpty()) {
//			return solution.get(0);
//		} else {
//			return new ArrayList<String>();
//		}
//	}

	/**
	 * Remove redundant constraints
	 * 
	 * @param fm
	 */
	public static void removeRedundantConstraints(IFeatureModel fm) {
		FeatureModelAnalyzer analyzer = FeatureModelManager.getAnalyzer(fm);
//		= fm.getAnalyser();
		List<IConstraint> redundant = analyzer.getRedundantConstraints(new NullMonitor<List<LiteralSet>>());
		redundant.forEach(c -> fm.removeConstraint(c));

//		analyzer.calculateRedundantConstraints = true;
//		analyzer.calculateTautologyConstraints = false;
//		analyzer.calculateDeadConstraints = false;
//		analyzer.calculateFOConstraints = false;
//		HashMap<Object, Object> o = analyzer.analyzeFeatureModel(new NullMonitor());
//		for (Entry<Object, Object> entry : o.entrySet()) {
//			if (entry.getKey() instanceof Constraint) {
//				if (entry.getValue() instanceof ConstraintAttribute) {
//					if ((ConstraintAttribute) entry.getValue() == ConstraintAttribute.REDUNDANT) {
//						fm.removeConstraint((Constraint) entry.getKey());
//					}
//				}
//			}
//		}
	}

	public static acapulco.model.FeatureModel loadFeatureModel(String featureIDE) {
		closeSystemErr();
		acapulco.model.FeatureModel result = loadFeatureModel(Paths.get(featureIDE).toFile());
		openSystemErr();
		return result;
	}

	public static acapulco.model.FeatureModel loadFeatureModel(File featureIDE) {
		closeSystemErr();
		IFeatureModel fm = FeatureModelManager.load(featureIDE.toPath());
		openSystemErr();
		return FeatureIDE2FM.create(fm);
	}

	public static void saveSXFM(File newFile, IFeatureModel fm) {
		SXFMFormat format = new SXFMFormat();
		String fmString = format.write(fm);
		FileUtils.writeStringToFile(newFile, fmString);
	}

	private static void closeSystemErr() {
		_err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
	}

	private static void openSystemErr() {
		System.setErr(_err);
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
	 * Is implies left to right is to be used when you already know that it is an
	 * implies relation
	 * 
	 * @param c
	 * @return true if it is the first feature implies the second one, false
	 *         otherwise
	 */
	public static boolean isImpliesLeftToRight(IConstraint c) {
		// Implies-based way to define implies
		if (c.getNode() instanceof Implies) {
			return true;
		}
		// Or-based way to define implies. It is left to right when the negation is the
		// first feature
		if (c.getNode() instanceof Or) {
			Or orNode = (Or) c.getNode();
			Node[] children = orNode.getChildren();
			Node firstNode = children[0];
			if (firstNode instanceof Not) {
				return true;
			} else if (firstNode instanceof Literal) {
				if (!((Literal) firstNode).positive) {
					return true;
				}
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
					if (!((Literal) children[0]).positive) {
						return false;
					}
				}
				// second one must be a negated literal
				if ((children[1] instanceof Literal) && !((Literal) children[1]).positive) {
					return true;
				} else if (children[1] instanceof Not) {
					Not not = (Not) children[1];
					if ((not.getChildren()[0] instanceof Literal)) {
						if (((Literal) not.getChildren()[0]).positive) {
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

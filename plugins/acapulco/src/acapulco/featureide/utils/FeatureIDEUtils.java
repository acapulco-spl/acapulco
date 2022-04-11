package acapulco.featureide.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
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

}

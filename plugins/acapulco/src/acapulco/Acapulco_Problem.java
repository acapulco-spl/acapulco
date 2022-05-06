package acapulco;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;

public class Acapulco_Problem extends Problem {

	public static String fm;
	public List<IObjective> objectives;
	public List<Integer> minMax;

	// The index corresponds to the solution binary array
	public Map<Integer, String> mapIndexFeatName;

	public static int numFeatures;
	private int numConstraints;
	public static List<List<Integer>> constraints;
	private List<Integer> mandatoryFeaturesIndices, deadFeaturesIndices;
	public static List<Integer> featureIndicesAllowedFlip;
	private int[] seed;

	private static final int N_VARS = 1;

	public Acapulco_Problem(String fm, List<IObjective> objectives, List<Integer> minMax, String mandatory, String dead,
			String seedfile) throws Exception {
		this.numberOfVariables_ = N_VARS;
		this.numberOfObjectives_ = objectives.size();
		this.numberOfConstraints_ = 0;
		this.fm = fm;
		mapIndexFeatName = new LinkedHashMap<Integer, String>();
		this.objectives = objectives;
		this.minMax = minMax;
		loadFM(fm);
		loadMandatoryDeadFeaturesIndices(mandatory, dead);
		loadSeed(seedfile);
		this.solutionType_ = new Acapulco_BinarySolution(this, numFeatures, fm, mandatoryFeaturesIndices,
				deadFeaturesIndices, seed, new ArrayList<>(), new ArrayList<>(), constraints);
	}

	public List<List<Integer>> getConstraints() {
		return constraints;
	}

	@Override
	public void evaluate(Solution sltn) throws JMException {
		Variable[] vars = sltn.getDecisionVariables();
		Binary bin = (Binary) vars[0];
		List<String> config = fromBinaryArrayToFeatures(bin);

		for (int i = 0; i < objectives.size(); i++) {
			IObjective objective = objectives.get(i);
			double value = objective.evaluate(config);

			// by default the algorithm tries to minimize. Negate the value to maximize
			if (minMax.get(i) == IObjective.DEFAULT_MAXIMIZE) {
				value = -value;
			}

			sltn.setObjective(i, value);
		}
	}

	/**
	 * From binary array to features
	 * 
	 * @param binaryArray
	 * @return list of feature names which are selected
	 */
	private List<String> fromBinaryArrayToFeatures(Binary bin) {
		List<String> config = new ArrayList<String>();
		for (int i = 0; i < bin.getNumberOfBits(); i++) {
			boolean b = bin.getIth(i);
			if (b) {
				config.add(mapIndexFeatName.get(i));
			}
		}
		return config;
	}

	public String getFm() {
		return fm;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public int numViolatedConstraints(Binary b) {

		// IVecInt v = bitSetToVecInt(b);
		int s = 0;
		for (List<Integer> constraint : constraints) {
			boolean sat = false;

			for (Integer i : constraint) {
				int abs = (i < 0) ? -i : i;
				boolean sign = i > 0;
				if (b.getIth(abs - 1) == sign) {
					sat = true;
					break;
				}
			}
			if (!sat) {
				s++;
			}

		}

		return s;
	}

	public void loadFM(String fm) throws Exception {
		List<String> lines = FileUtils.getLinesOfFile(new File(fm));
		for (String line : lines) {
			line = line.trim();

			if (line.startsWith("c")) {
				String[] lineSplit = line.split(" ");
				int index = Integer.parseInt(lineSplit[1]) - 1;
				String featName = lineSplit[2];
				mapIndexFeatName.put(index, featName);
			}

			if (line.startsWith("p")) {
				StringTokenizer st = new StringTokenizer(line, " ");
				st.nextToken();
				st.nextToken();
				numFeatures = Integer.parseInt(st.nextToken());
				numConstraints = Integer.parseInt(st.nextToken());
				constraints = new ArrayList<List<Integer>>(numConstraints);
			}

			if (!line.startsWith("c") && !line.startsWith("p") && !line.isEmpty()) {
				StringTokenizer st = new StringTokenizer(line, " ");
				List<Integer> constraint = new ArrayList<Integer>(st.countTokens() - 1);

				while (st.hasMoreTokens()) {
					int i = Integer.parseInt(st.nextToken());
					if (i != 0) {
						constraint.add(i);
					}
				}
				constraints.add(constraint);
			}
		}
	}

	public void loadMandatoryDeadFeaturesIndices(String mandatory, String dead) throws Exception {

		mandatoryFeaturesIndices = new ArrayList<Integer>(numFeatures);
		deadFeaturesIndices = new ArrayList<Integer>(numFeatures);
		featureIndicesAllowedFlip = new ArrayList<Integer>(numFeatures);

		List<String> lines = FileUtils.getLinesOfFile(new File(mandatory));
		for (String line : lines) {
			if (!line.isEmpty()) {
				int i = Integer.parseInt(line) - 1;
				mandatoryFeaturesIndices.add(i);
			}
		}

		lines = FileUtils.getLinesOfFile(new File(dead));
		for (String line : lines) {
			if (!line.isEmpty()) {
				int i = Integer.parseInt(line) - 1;
				deadFeaturesIndices.add(i);
			}
		}

		for (int i = 0; i < numFeatures; i++) {
			if (!mandatoryFeaturesIndices.contains(i) && !deadFeaturesIndices.contains(i))
				featureIndicesAllowedFlip.add(i);

		}

	}

	public void loadSeed(String seedFile) throws Exception {
		seed = new int[numFeatures];

		List<String> lines = FileUtils.getLinesOfFile(new File(seedFile));
		for (String line : lines) {
			line.trim();
			StringTokenizer st = new StringTokenizer(line, " ");
			while (st.hasMoreElements()) {
				int i = Integer.parseInt(st.nextToken());
				int iAbs = (i > 0) ? i : -i;
				seed[iAbs - 1] = (i > 0) ? 1 : -1;
			}
		}
	}

}

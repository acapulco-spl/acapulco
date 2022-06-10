package acapulco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;

import acapulco.activator.Activator;
import acapulco.algorithm.instrumentation.ToolInstrumenter;
import acapulco.algorithm.termination.StoppingCondition;
import acapulco.engine.variability.ConfigurationSearchOperator;
import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;
import acapulco.utils.emf.HenshinUtils;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;

public class Acapulco_Main {

	protected String featureModel;
	protected StoppingCondition stoppingCondition;
	protected Integer stoppingValue;
	protected Integer populationSize;
	protected Double mutationProbability;
	protected Double crossoverProbability;
	protected boolean debugMode;
	protected List<IObjective> objectives;
	protected List<Integer> minMax;

	protected Map<String, Integer> featureName2index;
	protected Map<Integer, String> index2FeatureName;

	public void run() {
		String fm = featureModel;
		StoppingCondition sc = stoppingCondition;
		Integer sv = stoppingValue;
		Integer popSize = populationSize;
		Double mutationProbability = this.mutationProbability;
		Double crossoverProbability = this.crossoverProbability;
		Boolean debug = debugMode;
		List<IObjective> objectives = this.objectives;
		List<Integer> minMax = this.minMax;
		run(fm, objectives, minMax, sc, sv, popSize, mutationProbability, crossoverProbability, debug, new NullProgressMonitor());
	}

	public void run(String fm, List<IObjective> objectives, List<Integer> minMax, StoppingCondition sc, Integer sv,
			Integer popSize, Double mutationProbability, Double crossoverProbability, boolean debug, IProgressMonitor monitor) {
		String rules = fm + ".cpcos";
		readFeatureNameMapFromFile(fm);
		List<ConfigurationSearchOperator> operators = HenshinUtils.readOperatorsFromDirectory(rules, featureName2index);
		run(fm, objectives, minMax, sc, sv, popSize, mutationProbability, crossoverProbability, debug, operators, monitor);
	}

	public void run(String fm, List<IObjective> objectives, List<Integer> minMax, StoppingCondition sc, Integer sv,
			Integer popSize, Double mutationProbability, Double crossoverProbability, boolean debug,
			List<ConfigurationSearchOperator> operators, IProgressMonitor monitor) {
		monitor.subTask("Preparation");
		
		String dead = fm + ".dead";
		String mandatory = fm + ".mandatory";
		String seed = fm + ".richseed";

		Problem p = null;
		Algorithm a = null;
		SolutionSet pop = null;
		try {
			Acapulco_Mutation.DEBUG_MODE = debug;
			p = new Acapulco_Problem(fm, objectives, minMax, mandatory, dead, seed);
			String resultsFolder = new File(fm).getParentFile().getAbsolutePath() + "\\output";
			ToolInstrumenter toolInstrumenter = new ToolInstrumenter(p.getNumberOfObjectives(),
					p.getNumberOfConstraints(), "ACAPULCO", resultsFolder, 1);
			a = new Acapulco_SettingsIBEA(p).configure(toolInstrumenter, sc, sv, popSize, mutationProbability,
					crossoverProbability, fm, ((Acapulco_Problem) p).getNumFeatures(),
					((Acapulco_Problem) p).getConstraints(), operators, monitor);
			
			pop = a.execute();

			System.out.println("******* END OF RUN! SOLUTIONS: ***");
			
			toolInstrumenter.serialiseAccumulator();
			
			// Output the results
			monitor.subTask("Creation of output results");
			
			// get unique solutions (not repeated)
			Map<String, Solution> configs = new LinkedHashMap<String, Solution>();
			for (int i = 0; i < pop.size(); i++) {
				Variable v = pop.get(i).getDecisionVariables()[0];
				if (configs.get(v.toString()) == null) {
					configs.put(v.toString(), pop.get(i));
				}
			}

			// Results csv and .config for each optimal configuration
			StringBuffer resultsCsv = new StringBuffer();
			resultsCsv.append("Config");
			for (int j = 0; j < objectives.size(); j++) {
				resultsCsv.append(",\"" + objectives.get(j).getName() + "\"");
			}
			resultsCsv.append("\n");

			int i = 1;
			for (String key : configs.keySet()) {

				String configFileContent = transformToConfigFile(key);
				File configFile = new File(resultsFolder + "\\optimalConfigs\\" + i + ".config");
				configFile.getParentFile().mkdirs();
				FileUtils.writeStringToFile(configFile, configFileContent);

				resultsCsv.append(i + ".config");
				for (int j = 0; j < objectives.size(); j++) {
					resultsCsv.append("," + configs.get(key).getObjective(j));
				}
				resultsCsv.append("\n");
				i++;
			}

			// remove last new line
			resultsCsv.setLength(resultsCsv.length() - 1);

			File csvFile = new File(resultsFolder + "\\optimalConfigs.csv");
			FileUtils.writeStringToFile(csvFile, resultsCsv.toString());
			
			
			// Pareto-front serialize
			if (objectives.size() == 2) {
				createParetoFrontReport(objectives, csvFile, resultsFolder);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createParetoFrontReport(List<IObjective> objectives, File resultsCsvFile, String outputFolderPath)
			throws IOException, MalformedURLException {
		String base = Platform.getBundle(Activator.PLUGIN_ID).getEntry("/").toString();
		String relativeUri = "paretoFrontTemplate.html";
		URL file = FileLocator.toFileURL(new URL(base + relativeUri));
		File f = new File(file.getFile());
		if (!f.exists()) {
			System.err.println(f.getAbsolutePath() + " does not exist.");
			return;
		}
		String template = FileUtils.getStringOfFile(f);
		template = template.replace("X axis label", objectives.get(0).getName());
		template = template.replace("Y axis label", objectives.get(1).getName());
		
		List<String> csv = FileUtils.getLinesOfFile(resultsCsvFile);
		String data = "";
		// ignore header
		for (int ci = 1; ci < csv.size(); ci ++) {
			String line = csv.get(ci);
			String[] split = line.split(",");
			data += "{x:" + split[1] + ",y:" + split[2] + "}";
			if (ci != csv.size() - 1) {
				data += ",";
			}
		}
		template = template.replace("{x:-10,y:0},{x:0,y:10}", data);
		
		File paretoFrontFile = new File(outputFolderPath + "\\paretoFront.html");
		FileUtils.writeStringToFile(paretoFrontFile, template);
	}

	public String transformToConfigFile(String arrayZeroOnes) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < arrayZeroOnes.length(); i++) {
			if (arrayZeroOnes.charAt(i) == '1') {
				// dimacs starts with 1
				String fname = index2FeatureName.get(i + 1);
				result.append(fname);
				result.append("\n");
			}
		}
		// remove last new line
		if (!result.isEmpty()) {
			result.setLength(result.length() - 1);
		}
		return result.toString();
	}

	private void readFeatureNameMapFromFile(String fm) {
		featureName2index = new HashMap<String, Integer>();
		index2FeatureName = new HashMap<Integer, String>();

		BufferedReader objReader = null;
		try {
			String line;
			objReader = new BufferedReader(new FileReader(fm));
			boolean done = false;
			while (!done && (line = objReader.readLine()) != null) {
				if (line.startsWith("c")) {
					String[] lineSplit = line.split(" ");
					featureName2index.put(lineSplit[2], Integer.parseInt(lineSplit[1]));
					index2FeatureName.put(Integer.parseInt(lineSplit[1]), lineSplit[2]);
				} else {
					done = true;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objReader != null)
					objReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}

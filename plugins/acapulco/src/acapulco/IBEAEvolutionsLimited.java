package acapulco;

import org.eclipse.core.runtime.IProgressMonitor;

import acapulco.algorithm.instrumentation.ToolInstrumenter;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Ranking;

public class IBEAEvolutionsLimited extends AbstractIBEA {

	IProgressMonitor monitor = null;
	
	/**
	 * Constructor. Create a new IBEA instance
	 *
	 * @param problem Problem to solve
	 */
	public IBEAEvolutionsLimited(Problem problem, long stoppingValue, ToolInstrumenter toolInstrumenter, IProgressMonitor monitor) {
		super(problem, stoppingValue, toolInstrumenter);
		this.monitor = monitor;
	} // Spea2

	/**
	 * Runs of the IBEA algorithm.
	 *
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 *         as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		long currentStep = 0;

		int populationSize, archiveSize, evaluations;
		Operator crossoverOperator, mutationOperator, selectionOperator;
		SolutionSet solutionSet, archive, offSpringSolutionSet;

		// Read the params
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();

		// Read the operators
		crossoverOperator = operators_.get("crossover");
		mutationOperator = operators_.get("mutation");
		selectionOperator = operators_.get("selection");

		// Initialize the variables
		solutionSet = new SolutionSet(populationSize);
		archive = new SolutionSet(archiveSize);
		evaluations = 0;

		// -> Create the initial solutionSet
		Solution newSolution;
		for (int i = 0; i < populationSize; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			solutionSet.add(newSolution);
		}

		Long l = Long.valueOf(this.stoppingValue);
		monitor.beginTask("Evolution", l.intValue());
		while (currentStep < this.stoppingValue) {
			
			if (monitor.isCanceled()) {
				break;
			}
			
			monitor.subTask("Evolution " + currentStep + " / " + this.stoppingValue);
			
			// while (evaluations < maxEvaluations){
			SolutionSet union = ((SolutionSet) solutionSet).union(archive);
			calculateFitness(union);
			archive = union;

			while (archive.size() > populationSize) {
				removeWorst(archive);
			}
			// Create a new offspringPopulation
			offSpringSolutionSet = new SolutionSet(populationSize);
			Solution[] parents = new Solution[2];
			while (offSpringSolutionSet.size() < populationSize) {
				int j = 0;
				do {
					j++;
					parents[0] = (Solution) selectionOperator.execute(archive);
				} while (j < IBEAEvolutionsLimited.TOURNAMENTS_ROUNDS); // do-while
				int k = 0;
				do {
					k++;
					parents[1] = (Solution) selectionOperator.execute(archive);
				} while (k < IBEAEvolutionsLimited.TOURNAMENTS_ROUNDS); // do-while

				// make the crossover
				Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
				mutationOperator.execute(offSpring[0]);
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				offSpringSolutionSet.add(offSpring[0]);
				evaluations++;
			} // while
				// End Create a offSpring solutionSet
//			System.out.println("Solution set size: :"+ solutionSet.size());
//			System.out.println("Offspring solution set size: :"+ offSpringSolutionSet.size());
			solutionSet = offSpringSolutionSet;
			
			// Increment the current algorithm step;
			currentStep += 1;
			monitor.worked(1);
			this.toolInstrumenter.collectStep(evaluations, archive, offSpringSolutionSet);
//			solutionSet.printObjectives();
		}

		// Do not serialise
		// this.toolInstrumenter.serialiseAccumulator();
		Ranking ranking = new Ranking(archive);
		return ranking.getSubfront(0);
	} // execute
} // Spea2

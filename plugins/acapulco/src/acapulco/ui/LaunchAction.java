package acapulco.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import acapulco.Acapulco_Main;
import acapulco.algorithm.termination.StoppingCondition;
import acapulco.evaluation.output.ParseResults;
import acapulco.evaluation.output.Results;
import acapulco.objectives.IObjective;
import acapulco.objectives.ObjectivesHelper;

public class LaunchAction implements IObjectActionDelegate {

	private static String outputPath;

	ISelection selection = null;

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			Object featureModel = ((IStructuredSelection) selection).getFirstElement();
			File fmFile = getFileFromIResource((IResource) featureModel);
			File fmFolder = fmFile.getParentFile();
			outputPath = new File(fmFolder, "output").getAbsolutePath();
			String fmNameInput = fmFile.getName().substring(0, fmFile.getName().length() - ".dimacs".length());

			List<IObjective> availableObjectives = ObjectivesHelper.getAllRegisteredObjectives();

			LaunchActionDialog launchDialog = new LaunchActionDialog(Display.getCurrent().getActiveShell(),
					availableObjectives);
			int buttonPressed = launchDialog.open();
			if (buttonPressed != LaunchActionDialog.OK) {
				return;
			}

			System.out.println("Launching Acapulco...");

			StoppingCondition sc = StoppingCondition.values()[launchDialog.getStoppingConditionIndex()];
			String svString = launchDialog.getStoppingValue();
			int sv = Integer.parseInt(svString);
			String popSizeString = launchDialog.getPopulationSize();
			int popSize = Integer.parseInt(popSizeString);
			String mutationProbabilityString = launchDialog.getMutationProbability();
			double mutationProbability = Double.parseDouble(mutationProbabilityString);
			String crossoverProbabilityString = launchDialog.getCrossoverProbability();
			double crossoverProbability = Double.parseDouble(crossoverProbabilityString);

			List<IObjective> objectives = launchDialog.getObjectives();
			List<Integer> minMax = launchDialog.getMinMax();

			Acapulco_Main acapulcoSearch = new Acapulco_Main();
			String fullFmPath = fmFile.getAbsolutePath();

			// Launch Progress dialog
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			try {
				progressDialog.run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.setTaskName("Acapulco Launch");
						acapulcoSearch.run(fullFmPath, objectives, minMax, sc, sv, popSize, mutationProbability,
								crossoverProbability, false, monitor);
						monitor.done();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

//			try {
//				acapulcoSearch.run(fullFmPath, objectives, minMax, sc, sv, popSize, mutationProbability,
//						crossoverProbability, false);
//				// Thread.sleep(1000);
//				// System.gc();
//
//				// parseResults(ACAPULCO_TOOL_NAME, fmNameInput, 1, null);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			System.out.println("Done!");

			// refresh the workspace to show the new folders and files
			try {
				((IResource) featureModel).getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	/**
	 * get File from IResource
	 * 
	 * @param iresource (including IFile)
	 * @return File
	 */
	public static File getFileFromIResource(IResource resource) {
		if (resource instanceof IProject) {
			// for some reason rawlocation in projects return null
			IProject project = (IProject) resource;
			if (!project.exists()) {
				return null;
			}
			return project.getLocation().makeAbsolute().toFile();
		}
		if (resource.getRawLocation() != null) {
			return resource.getRawLocation().makeAbsolute().toFile();
		}
		return null;
	}

	private static void parseResults(String sTool, String fmNameInput, int runs, double[][] ptf) {
		// Parse results
		System.out.println("Parsing results for " + sTool + "...");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Results results = new Results();
		List<String> resultFilepaths = ParseResults.getAllResultsFilepath(outputPath, sTool, runs);
		for (int i = 1; i <= runs; i++) {
			System.out.println(resultFilepaths.get(i - 1));
			results.addRun(i, ParseResults.getResults(resultFilepaths.get(i - 1), sTool));
		}

		// Serialize the results
		String statsResultsFilename = outputPath + "/" + sTool + "_" + fmNameInput + "_statsResults.dat";
		String runsResultsFilename = outputPath + "/" + sTool + "_" + fmNameInput + "_" + runs + "runs_results.dat";
		if (ptf == null) {
			results.saveResults(statsResultsFilename);
			results.saveRunsResults(runsResultsFilename);
		} else {
			try {
				results.saveResults(statsResultsFilename, ptf);
				results.saveRunsResults(runsResultsFilename, ptf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

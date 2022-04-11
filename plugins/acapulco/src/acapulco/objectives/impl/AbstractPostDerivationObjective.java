package acapulco.objectives.impl;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import acapulco.featureide.utils.FeatureIDEUtils;
import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;
import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;

public abstract class AbstractPostDerivationObjective implements IObjective {

	public IFeatureProject featureProject;
	public List<String> features;

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public void configure() {
		Shell shell = Display.getCurrent().getActiveShell();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setTitle("Select FeatureIDE project");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);

		if (dialog.open() != Window.OK) {
			return;
		}

		IResource resource = (IResource) dialog.getFirstResult();
		featureProject = CorePlugin.getFeatureProject(resource);
	}

	@Override
	public double evaluate(List<String> features) {
		this.features = features;
		
		// create config file
		String configFileContent = FeatureIDEUtils.createConfigFileContent(features);
		File configFile = new File(featureProject.getConfigPath(), "temp.config");
		FileUtils.writeStringToFile(configFile, configFileContent);

		// set as current config
		featureProject.setCurrentConfiguration(configFile.toPath());

		// build
		featureProject.buildRelevantChanges();
		try {
			featureProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		featureProject.built();
		
		// call the method to evaluate the derived
		String buildPath = featureProject.getBuildPath();
		return evaluateDerivation(buildPath);
	}
	
	public abstract double evaluateDerivation(String buildPath);
	
}

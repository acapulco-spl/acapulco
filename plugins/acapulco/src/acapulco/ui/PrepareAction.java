package acapulco.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import acapulco.featureide.utils.FeatureIDE2FM;
import acapulco.model.FeatureModel;
import acapulco.preparation.PreparationPipeline;
import acapulco.rulesgeneration.CpcoGenerator;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;

public class PrepareAction implements IObjectActionDelegate {

	ISelection selection = null;

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			Object featureModel = ((IStructuredSelection) selection).getFirstElement();
			File fmFile = getFileFromIResource((IResource) featureModel);

			// load FM
			IFeatureModel fideFM = FeatureModelManager.load(fmFile.toPath());
			if (fideFM == null) {
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
				messageBox.setText("Error loading the feature model");
				messageBox.setMessage(
						"It was not possible to load the feature model or it is not a format supported by FeatureIDE");
				messageBox.open();
				return;
			}
			
			// Launch Progress dialog
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getCurrent()
					.getActiveShell());

			try {
				progressDialog.run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						File fmFolder = fmFile.getParentFile();
						String fmName = fmFile.getName().substring(0, fmFile.getName().indexOf('.'));

						int totalWork = 2; // preparation pipeline + generate cpcos
						monitor.beginTask("Acapulco Prepare", totalWork);

						FeatureModel fm = FeatureIDE2FM.create(fideFM);

						monitor.subTask("Preparation pipeline");
						try {
							PreparationPipeline.generateAllFromFm(fmFile.getAbsolutePath(), fmName, fmName,
									fmFolder.getAbsolutePath(), monitor);
						} catch (IOException e) {
							e.printStackTrace();
						}

						monitor.worked(1);
						
						CpcoGenerator.generatorCPCOs(fm, fmName, fmFolder.getAbsolutePath(), fmFile.getAbsolutePath(), monitor);
						monitor.worked(1);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			// refresh the workspace to show the new folders and files
			try {
				((IResource) featureModel).getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}

			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information",
					"New acapulco folder created for this feature model.\nRight click .dimacs file to launch acapulco");
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

}

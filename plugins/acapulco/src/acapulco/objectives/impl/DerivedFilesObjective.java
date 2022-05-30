package acapulco.objectives.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;

public class DerivedFilesObjective extends AbstractPostDerivationObjective {

	List<String> extensions = new ArrayList<String>();

	@Override
	public String getName() {
		return "Number of derived files";
	}

	@Override
	public int getDefaultMinimizeOrMaximize() {
		return IObjective.DEFAULT_MINIMIZE;
	}

	@Override
	public double evaluateDerivation(String buildPath) {
		List<File> files = getFilesToConsider(buildPath);
		return files.size();
	}

	@Override
	public void configure() {
		super.configure();
		Shell shell = Display.getCurrent().getActiveShell();
		InputDialog id = new InputDialog(shell, "Acapulco",
				"Enter a comma-separated list of file extensions.\nFor example: ccp,h\nOr leave it empty to count all. ",
				"", null);
		id.open();
		String value = id.getValue();
		if (value != null && !value.isEmpty()) {
			String[] extens = value.split(",");
			for (String ext : extens) {
				extensions.add(ext.trim());
			}
		}
	}
	
	public List<File> getFilesToConsider(String buildPath) {
		List<File> files = new ArrayList<File>();
		FileUtils.getListOfAllFiles(buildPath, files);
		if (extensions.isEmpty()) {
			return files;
		}

		List<File> filteredFiles = new ArrayList<File>();
		for (File file : files) {
			for (String extension : extensions) {
				if (file.getName().endsWith("." + extension)) {
					filteredFiles.add(file);
				}
			}
		}
		return filteredFiles;
	}

}

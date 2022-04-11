package acapulco.objectives.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;

public class DerivedFilesObjective extends AbstractPostDerivationObjective {

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
		// count number of files
		List<File> files = new ArrayList<File>();
		FileUtils.getListOfAllFiles(buildPath, files);
		return files.size();
	}

}

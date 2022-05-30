package acapulco.objectives.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import acapulco.utils.FileUtils;

public class DerivedLinesObjective extends DerivedFilesObjective {

	List<String> extensions = new ArrayList<String>();

	@Override
	public String getName() {
		return "Number of derived lines";
	}

	@Override
	public double evaluateDerivation(String buildPath) {
		List<File> files = getFilesToConsider(buildPath);
		int numberOfLines = 0;
		for (File file : files) {
			int fileLines = FileUtils.getLinesOfFile(file).size();
			numberOfLines += fileLines;
		}
		return numberOfLines;
	}

}

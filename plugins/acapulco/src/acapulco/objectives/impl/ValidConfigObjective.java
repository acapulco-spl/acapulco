package acapulco.objectives.impl;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import acapulco.objectives.IObjective;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.ConfigurationAnalyzer;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;

public class ValidConfigObjective implements IObjective {

	String selectedFM;
	
	@Override
	public String getName() {
		return "Valid configuration";
	}

	@Override
	public double evaluate(List<String> features) {
		IFeatureModel fm = FeatureModelManager.load(new File(selectedFM).toPath());
		FeatureModelFormula fmf = new FeatureModelFormula(fm);
		Configuration config = new Configuration(fmf);
		for (String feature : features) {
			config.setManual(feature, Selection.SELECTED);
		}
		ConfigurationAnalyzer ca = new ConfigurationAnalyzer(fmf, config);
		boolean isValid = ca.isValid();
		if (isValid) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void configure() {
		// user selects the target fm
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Select the feature model");
		selectedFM = dialog.open();
	}

	@Override
	public int getDefaultMinimizeOrMaximize() {
		return IObjective.DEFAULT_MAXIMIZE;
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

}
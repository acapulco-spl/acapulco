package acapulco.objectives.impl;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import acapulco.objectives.IObjective;
import acapulco.utils.FileUtils;

public class CSVFeatureAttributesObjective implements IObjective {

	private static final String CSV_SEPARATOR = ",";
	private String attribute;
	private String selectedCSVFile;
	
	Map<String, Double> featureValues = new HashMap<String, Double>();

	@Override
	public String getName() {
		if (attribute == null || selectedCSVFile == null) {
			return "Feature attributes in a CSV file";
		} else {
			return attribute + " in " + new File(selectedCSVFile).getName();
		}
	}

	@Override
	public double evaluate(List<String> features) {
		double total = 0.0;
		for (String feature : features) {
			Double value = featureValues.get(feature);
			// check if there was a value for the feature
			if (value != null) {
				total += featureValues.get(feature);
			} else {
				System.out.println(feature);
			}
		}
		return total;
	}

	@Override
	public void configure() {

		// user selects the csv file
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Select a csv file with headers");
		dialog.setFilterExtensions(new String[] { "*.csv" });
		selectedCSVFile = dialog.open();
		if (selectedCSVFile != null) {

			// user selects the attribute
			List<String> lines = FileUtils.getLinesOfFile(new File(selectedCSVFile));
			String header = lines.get(0);
			String[] headers = header.split(CSV_SEPARATOR);
			ElementListSelectionDialog sdialog = new ElementListSelectionDialog(shell, new LabelProvider());
			sdialog.setTitle("Select one attribute");
			sdialog.setMessage("Select an attribute (* = any string, ? = any char):");
			sdialog.setElements(Arrays.copyOfRange(headers, 1, headers.length));
			sdialog.setInitialSelections(headers[0]);
			if (sdialog.open() != Dialog.OK) {
				return;
			}
			sdialog.setMultipleSelection(false);
			
			attribute = sdialog.getFirstResult().toString();
			
//			InputDialog inputDialog = new InputDialog(shell, "Acapulco", "Select one attribute\nCSV headers: " + header, "", null);
//			if (inputDialog.open() != Dialog.OK) {
//				return;
//			}
//			attribute = inputDialog.getValue();

			// get the index
			int indexInCSVFile = 1;
			for (int i = 1; i < headers.length; i++) {
				if (attribute.equals(headers[i])) {
					indexInCSVFile = i;
					break;
				}
			}

			// ignore header
			lines.remove(0);
			for (String line : lines) {
				String[] split = line.split(CSV_SEPARATOR);
				String feature = split[0];
				Double value = Double.parseDouble(split[indexInCSVFile]);
				featureValues.put(feature, value);
			}
		}
	}

	@Override
	public int getDefaultMinimizeOrMaximize() {
		return DEFAULT_MINIMIZE;
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

}
package acapulco.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import acapulco.aCaPulCO_SettingsIBEA;
import acapulco.activator.Activator;
import acapulco.objectives.IObjective;

public class LaunchActionDialog extends TitleAreaDialog {

	private List<IObjective> availableObjectives;
	private List<IObjective> currentObjectives = new ArrayList<IObjective>();
	private List<Integer> minMaxSelection = new ArrayList<Integer>();

	private List<Combo> objectivesCombos = new ArrayList<Combo>();
	private List<Combo> objectivesMinMaxCombos = new ArrayList<Combo>();
	private List<Button> objectivesConfigButtons = new ArrayList<Button>();
	private List<Button> objectivesDelButtons = new ArrayList<Button>();

	private Combo stoppingConditionCombo;
	private Text stoppingValueText;
	private Text populationSizeText;
	private Text mutationProbabilityText;
	private Text crossoverProbabilityText;

	private int stoppingCondition;
	private String stoppingValue;
	private String populationSize;
	private String mutationProbability;
	private String crossoverProbability;

	public LaunchActionDialog(Shell parentShell, List<IObjective> availableObjectives) {
		super(parentShell);
		this.availableObjectives = availableObjectives;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Acapulco Launch");
		setMessage("Settings for launching Acapulco", IMessageProvider.INFORMATION);
		getShell().setImage(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/acapulco.png").createImage());
		setTitleImage(null);
		setDialogHelpAvailable(false);
		setHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(4, false);
		container.setLayout(layout);

		createStoppingCondition(container);
		createStoppingValue(container);
		createPopulationSize(container);
		createMutationProbability(container);
		createCrossoverProbability(container);
		
		createObjectivesContainer(container);

		return area;
	}

	/**
	 * Stopping condition
	 * 
	 * @param container
	 */
	private void createStoppingCondition(Composite container) {
		new Label(container, SWT.NONE).setText("Stopping condition");

		GridData dataStoppingCondition = new GridData();
		dataStoppingCondition.grabExcessHorizontalSpace = true;
		dataStoppingCondition.horizontalAlignment = GridData.FILL;

		stoppingConditionCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER);
		stoppingConditionCombo.setLayoutData(dataStoppingCondition);
		// predefined stopping conditions options
		stoppingConditionCombo.add("Number of evolutions");
		stoppingConditionCombo.add("Timeout in milliseconds");
		stoppingConditionCombo.select(0);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

	/**
	 * Stopping value
	 * 
	 * @param container
	 */
	private void createStoppingValue(Composite container) {
		new Label(container, SWT.NONE).setText("Stopping value");

		GridData dataStoppingValue = new GridData();
		dataStoppingValue.grabExcessHorizontalSpace = true;
		dataStoppingValue.horizontalAlignment = GridData.FILL;
		stoppingValueText = new Text(container, SWT.BORDER);
		stoppingValueText.setLayoutData(dataStoppingValue);
		// default for number of evolutions
		stoppingValueText.setText("50");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}
	
	private void createPopulationSize(Composite container) {
		new Label(container, SWT.NONE).setText("Population size");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		populationSizeText = new Text(container, SWT.BORDER);
		populationSizeText.setLayoutData(gridData);
		populationSizeText.setText(aCaPulCO_SettingsIBEA.DEFAULT_POPULATION_SIZE);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}
	
	private void createMutationProbability(Composite container) {
		new Label(container, SWT.NONE).setText("Mutation probability");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		mutationProbabilityText = new Text(container, SWT.BORDER);
		mutationProbabilityText.setLayoutData(gridData);
		mutationProbabilityText.setText(aCaPulCO_SettingsIBEA.DEFAULT_MUTATION_PROBABILITY);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}
	
	private void createCrossoverProbability(Composite container) {
		new Label(container, SWT.NONE).setText("Crossover probability");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		crossoverProbabilityText = new Text(container, SWT.BORDER);
		crossoverProbabilityText.setLayoutData(gridData);
		crossoverProbabilityText.setText(aCaPulCO_SettingsIBEA.DEFAULT_CROSSOVER_PROBABILITY);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

	/**
	 * Create objectives
	 * 
	 * @param container
	 */
	private void createObjectivesContainer(Composite container) {
		new Label(container, SWT.NONE).setText("Objective(s)");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Composite objContainer = new Composite(container, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 4;
		objContainer.setLayoutData(gridData);
		GridLayout layout = new GridLayout(4, false);
		objContainer.setLayout(layout);

		createObjective(objContainer);

		Button addButton = new Button(container, SWT.PUSH);
		addButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		addButton.setText("Add objective");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createObjective(objContainer);

				// TODO find a better way to refresh
				objContainer.requestLayout();
				container.requestLayout();
				objContainer.getShell().layout();
				objContainer.requestLayout();
				container.requestLayout();
			}
		});
	}

	/**
	 * Create objective
	 * 
	 * @param objContainer
	 */
	private void createObjective(Composite objContainer) {

		GridData dataObjective0 = new GridData();
		dataObjective0.grabExcessHorizontalSpace = true;
		dataObjective0.horizontalAlignment = GridData.FILL;
		Combo objTypeCombo = new Combo(objContainer, SWT.BORDER | SWT.READ_ONLY);
		objTypeCombo.setLayoutData(dataObjective0);
		for (IObjective objective : availableObjectives) {
			objTypeCombo.add(objective.getName());
		}
		objTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int comboIndex = objectivesCombos.indexOf((Combo) e.widget);
				int i = ((Combo) e.widget).getSelectionIndex();
				objectivesMinMaxCombos.get(comboIndex)
						.select(availableObjectives.get(i).getDefaultMinimizeOrMaximize());
				objectivesConfigButtons.get(comboIndex).setEnabled(availableObjectives.get(i).isConfigurable());
				IObjective obj = null;
				try {
					obj = availableObjectives.get(i).getClass().getConstructor(null).newInstance(null);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// TODO refresh name of previous selection

				currentObjectives.remove(comboIndex);
				currentObjectives.add(comboIndex, obj);
			}
		});
		objectivesCombos.add(objTypeCombo);

		objTypeCombo.select(0);
		// initialize
		IObjective obj = null;
		try {
			obj = availableObjectives.get(0).getClass().getConstructor(null).newInstance(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentObjectives.add(obj);

		Combo minMaxCombo = new Combo(objContainer, SWT.BORDER | SWT.READ_ONLY);
		minMaxCombo.setLayoutData(dataObjective0);
		minMaxCombo.add("Minimize");
		minMaxCombo.add("Maximize");
		minMaxCombo.select(availableObjectives.get(0).getDefaultMinimizeOrMaximize());
		objectivesMinMaxCombos.add(minMaxCombo);

		Button configButton = new Button(objContainer, SWT.PUSH);
		configButton.setText("Configure");
		configButton.setLayoutData(dataObjective0);
		configButton.setEnabled(availableObjectives.get(0).isConfigurable());
		objectivesConfigButtons.add(configButton);
		configButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int objIndex = objectivesConfigButtons.indexOf((Button) e.widget);
				IObjective obj = currentObjectives.get(objIndex);
				obj.configure();
				// refresh name
				int selectedIndex = objectivesCombos.get(objIndex).getSelectionIndex();
				objectivesCombos.get(objIndex).remove(selectedIndex);
				objectivesCombos.get(objIndex).add(currentObjectives.get(objIndex).getName(), selectedIndex);
				objectivesCombos.get(objIndex).redraw();
				objectivesCombos.get(objIndex).select(selectedIndex);
			}
		});

		Button delButton = new Button(objContainer, SWT.PUSH);
		// TODO make it non visible until it works
		delButton.setVisible(false);
		objectivesDelButtons.add(delButton);
		delButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int buttonIndex = objectivesDelButtons.indexOf((Button)e.widget);
				// TODO implement delete button
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		stoppingCondition = stoppingConditionCombo.getSelectionIndex();
		stoppingValue = stoppingValueText.getText();
		populationSize = populationSizeText.getText();
		mutationProbability = mutationProbabilityText.getText();
		crossoverProbability = crossoverProbabilityText.getText();
		minMaxSelection = new ArrayList<Integer>();
		for (Combo combo : objectivesMinMaxCombos) {
			minMaxSelection.add(combo.getSelectionIndex());
		}
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public int getStoppingConditionIndex() {
		return stoppingCondition;
	}

	public String getStoppingValue() {
		return stoppingValue;
	}
	
	public String getPopulationSize() {
		return populationSize;
	}
	
	public String getMutationProbability() {
		return mutationProbability;
	}
	
	public String getCrossoverProbability() {
		return crossoverProbability;
	}

	public List<IObjective> getObjectives() {
		return currentObjectives;
	}
	
	public List<Integer> getMinMax() {
		return minMaxSelection;
	}

}

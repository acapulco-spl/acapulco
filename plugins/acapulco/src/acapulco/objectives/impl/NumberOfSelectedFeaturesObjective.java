package acapulco.objectives.impl;

import java.util.List;

import acapulco.objectives.IObjective;

public class NumberOfSelectedFeaturesObjective implements IObjective {

	@Override
	public String getName() {
		return "Number of features";
	}

	@Override
	public double evaluate(List<String> features) {
		return features.size();
	}
	
	@Override
	public void configure() {
		// no need
	}

	@Override
	public int getDefaultMinimizeOrMaximize() {
		return IObjective.DEFAULT_MINIMIZE;
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

}
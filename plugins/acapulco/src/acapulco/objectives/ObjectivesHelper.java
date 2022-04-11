package acapulco.objectives;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ObjectivesHelper {

	public static final String OBJECTIVE_EXTENSIONPOINT = "acapulco.objective";

	/**
	 * Get all the registered objectives
	 * 
	 * @return the list of objectives
	 */
	public static List<IObjective> getAllRegisteredObjectives() {
		List<IObjective> objectives = new ArrayList<IObjective>();
		IConfigurationElement[] extensionPoints = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(OBJECTIVE_EXTENSIONPOINT);
		for (IConfigurationElement extensionPoint : extensionPoints) {
			try {
				objectives.add((IObjective) extensionPoint.createExecutableExtension("class"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return objectives;
	}

}

package acapulco.objectives;

import java.util.List;

public interface IObjective {

	public static final int DEFAULT_MINIMIZE = 0;
	public static final int DEFAULT_MAXIMIZE = 1;

	/**
	 * The name of the objective
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * By default, the objective is intended to be minimized or maximized. The user
	 * will be able to change it.
	 * 
	 * @return the constant values DEFAULT_MINIMIZE or DEFAULT_MAXIMIZE
	 */
	public int getDefaultMinimizeOrMaximize();

	/**
	 * Whether it makes sense to ask for user-defined parameters. See configure
	 * method
	 * 
	 * @return is configurable
	 */
	public boolean isConfigurable();

	/**
	 * If needed, to be used for getting information from the user regarding the
	 * initial parameters for the objective evaluation
	 */
	public void configure();

	/**
	 * The actual evaluation of the objective
	 * 
	 * @param features
	 * @return
	 */
	public double evaluate(List<String> features);

}
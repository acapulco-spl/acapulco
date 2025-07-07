package acapulco.rulesgeneration.activationdiagrams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * A node in the feature activation diagram.
 * 
 * We're consciously leaving hashcode and equals to reference object identity. This is only changed for feature decision nodes.
 */
@SuppressWarnings("all")
public abstract class ActivationDiagramNode {
  /**
   * The consequences of this node according to our principles.
   */
  private List<ActivationDiagramNode> consequences = new ArrayList<ActivationDiagramNode>();

  public List<ActivationDiagramNode> getConsequences() {
    return this.consequences;
  }

  /**
   * Collect the nearest feature decisions.
   */
  public Set<FeatureDecision> collectFeatureDecisions() {
    final Function1<ActivationDiagramNode, Set<FeatureDecision>> _function = (ActivationDiagramNode it) -> {
      return it.collectFeatureDecisions();
    };
    return IterableExtensions.<FeatureDecision>toSet(IterableExtensions.<ActivationDiagramNode, FeatureDecision>flatMap(this.consequences, _function));
  }
}

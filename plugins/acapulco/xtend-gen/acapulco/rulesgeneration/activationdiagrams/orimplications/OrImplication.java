package acapulco.rulesgeneration.activationdiagrams.orimplications;

import acapulco.rulesgeneration.activationdiagrams.ActivationDiagramNode;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public abstract class OrImplication {
  public abstract Set<VBRuleOrFeature> resolve(final Map<ActivationDiagramNode, Set<OrImplication>> orImplications, final Set<ActivationDiagramNode> visited);
}

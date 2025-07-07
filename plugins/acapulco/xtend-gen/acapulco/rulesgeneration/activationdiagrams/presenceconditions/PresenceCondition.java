package acapulco.rulesgeneration.activationdiagrams.presenceconditions;

import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public abstract class PresenceCondition {
  public abstract Set<VBRuleFeature> resolve(final Map<FeatureDecision, Set<PresenceCondition>> presenceConditions, final Set<FeatureDecision> visited);
}

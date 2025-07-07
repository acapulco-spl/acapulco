package acapulco.rulesgeneration.activationdiagrams.presenceconditions;

import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class FeaturePresenceCondition extends PresenceCondition {
  private final VBRuleFeature feature;

  @Override
  public Set<VBRuleFeature> resolve(final Map<FeatureDecision, Set<PresenceCondition>> presenceConditions, final Set<FeatureDecision> visited) {
    return Collections.<VBRuleFeature>unmodifiableSet(CollectionLiterals.<VBRuleFeature>newHashSet(this.feature));
  }

  public FeaturePresenceCondition(final VBRuleFeature feature) {
    super();
    this.feature = feature;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.feature== null) ? 0 : this.feature.hashCode());
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FeaturePresenceCondition other = (FeaturePresenceCondition) obj;
    if (this.feature == null) {
      if (other.feature != null)
        return false;
    } else if (!this.feature.equals(other.feature))
      return false;
    return true;
  }

  @Override
  @Pure
  public String toString() {
    return new ToStringBuilder(this)
    	.addAllFields()
    	.toString();
  }

  @Pure
  public VBRuleFeature getFeature() {
    return this.feature;
  }
}

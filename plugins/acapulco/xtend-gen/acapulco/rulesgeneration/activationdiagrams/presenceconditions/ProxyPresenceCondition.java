package acapulco.rulesgeneration.activationdiagrams.presenceconditions;

import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class ProxyPresenceCondition extends PresenceCondition {
  private final FeatureDecision fd;

  @Override
  public Set<VBRuleFeature> resolve(final Map<FeatureDecision, Set<PresenceCondition>> presenceConditions, final Set<FeatureDecision> visited) {
    Set<VBRuleFeature> _xifexpression = null;
    boolean _contains = visited.contains(this.fd);
    if (_contains) {
      _xifexpression = CollectionLiterals.<VBRuleFeature>emptySet();
    } else {
      Set<VBRuleFeature> _xblockexpression = null;
      {
        visited.add(this.fd);
        final Function1<PresenceCondition, Set<VBRuleFeature>> _function = (PresenceCondition it) -> {
          return it.resolve(presenceConditions, visited);
        };
        _xblockexpression = IterableExtensions.<VBRuleFeature>toSet(IterableExtensions.<PresenceCondition, VBRuleFeature>flatMap(presenceConditions.get(this.fd), _function));
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }

  public ProxyPresenceCondition(final FeatureDecision fd) {
    super();
    this.fd = fd;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.fd== null) ? 0 : this.fd.hashCode());
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
    ProxyPresenceCondition other = (ProxyPresenceCondition) obj;
    if (this.fd == null) {
      if (other.fd != null)
        return false;
    } else if (!this.fd.equals(other.fd))
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
  public FeatureDecision getFd() {
    return this.fd;
  }
}

package acapulco.rulesgeneration.activationdiagrams.orimplications;

import acapulco.rulesgeneration.activationdiagrams.ActivationDiagramNode;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class FinalisedOrImplication extends OrImplication {
  private final VBRuleOrFeature node;

  @Override
  public Set<VBRuleOrFeature> resolve(final Map<ActivationDiagramNode, Set<OrImplication>> orImplications, final Set<ActivationDiagramNode> visited) {
    return Collections.<VBRuleOrFeature>unmodifiableSet(CollectionLiterals.<VBRuleOrFeature>newHashSet(this.node));
  }

  public FinalisedOrImplication(final VBRuleOrFeature node) {
    super();
    this.node = node;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.node== null) ? 0 : this.node.hashCode());
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
    FinalisedOrImplication other = (FinalisedOrImplication) obj;
    if (this.node == null) {
      if (other.node != null)
        return false;
    } else if (!this.node.equals(other.node))
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
  public VBRuleOrFeature getNode() {
    return this.node;
  }
}

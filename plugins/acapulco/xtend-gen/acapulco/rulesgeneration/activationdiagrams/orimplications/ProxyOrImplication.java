package acapulco.rulesgeneration.activationdiagrams.orimplications;

import acapulco.rulesgeneration.activationdiagrams.ActivationDiagramNode;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
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
public class ProxyOrImplication extends OrImplication {
  private final ActivationDiagramNode node;

  @Override
  public Set<VBRuleOrFeature> resolve(final Map<ActivationDiagramNode, Set<OrImplication>> orImplications, final Set<ActivationDiagramNode> visited) {
    Set<VBRuleOrFeature> _xifexpression = null;
    boolean _contains = visited.contains(this.node);
    if (_contains) {
      _xifexpression = CollectionLiterals.<VBRuleOrFeature>emptySet();
    } else {
      Set<VBRuleOrFeature> _xblockexpression = null;
      {
        visited.add(this.node);
        final Function1<OrImplication, Set<VBRuleOrFeature>> _function = (OrImplication it) -> {
          return it.resolve(orImplications, visited);
        };
        _xblockexpression = IterableExtensions.<VBRuleOrFeature>toSet(IterableExtensions.<OrImplication, VBRuleOrFeature>flatMap(orImplications.get(this.node), _function));
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }

  public ProxyOrImplication(final ActivationDiagramNode node) {
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
    ProxyOrImplication other = (ProxyOrImplication) obj;
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
  public ActivationDiagramNode getNode() {
    return this.node;
  }
}

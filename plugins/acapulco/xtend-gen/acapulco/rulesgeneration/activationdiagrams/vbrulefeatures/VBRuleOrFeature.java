package acapulco.rulesgeneration.activationdiagrams.vbrulefeatures;

import acapulco.rulesgeneration.activationdiagrams.OrNode;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;

@Data
@SuppressWarnings("all")
public class VBRuleOrFeature extends VBRuleFeature {
  private final OrNode orNode;

  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("OR");
    int _iD = this.getID();
    _builder.append(_iD);
    _builder.append(": ");
    String _name = this.getName();
    _builder.append(_name);
    return _builder.toString();
  }

  public VBRuleOrFeature(final String name, final OrNode orNode) {
    super(name);
    this.orNode = orNode;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * super.hashCode() + ((this.orNode== null) ? 0 : this.orNode.hashCode());
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
    if (!super.equals(obj))
      return false;
    VBRuleOrFeature other = (VBRuleOrFeature) obj;
    if (this.orNode == null) {
      if (other.orNode != null)
        return false;
    } else if (!this.orNode.equals(other.orNode))
      return false;
    return true;
  }

  @Pure
  public OrNode getOrNode() {
    return this.orNode;
  }
}

package acapulco.rulesgeneration.activationdiagrams.vbrulefeatures;

import acapulco.rulesgeneration.activationdiagrams.OrNode;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;

@Data
@SuppressWarnings("all")
public class VBRuleOrAlternative extends VBRuleFeature {
  private final OrNode orNode;

  private final int alternativeID;

  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("OrAlternative");
    _builder.append(this.alternativeID);
    _builder.append(" ");
    String _name = this.getName();
    _builder.append(_name);
    return _builder.toString();
  }

  public VBRuleOrAlternative(final String name, final OrNode orNode, final int alternativeID) {
    super(name);
    this.orNode = orNode;
    this.alternativeID = alternativeID;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.orNode== null) ? 0 : this.orNode.hashCode());
    return prime * result + this.alternativeID;
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
    VBRuleOrAlternative other = (VBRuleOrAlternative) obj;
    if (this.orNode == null) {
      if (other.orNode != null)
        return false;
    } else if (!this.orNode.equals(other.orNode))
      return false;
    if (other.alternativeID != this.alternativeID)
      return false;
    return true;
  }

  @Pure
  public OrNode getOrNode() {
    return this.orNode;
  }

  @Pure
  public int getAlternativeID() {
    return this.alternativeID;
  }
}

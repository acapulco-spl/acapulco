package acapulco.rulesgeneration.activationdiagrams.vbrulefeatures;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;

@Data
@SuppressWarnings("all")
public class VBRuleFeature {
  private final String name;

  private final int ID = VBRuleFeature.getFreshID();

  private final List<VBRuleFeature> children = new ArrayList<VBRuleFeature>();

  @Override
  public String toString() {
    return this.name;
  }

  private static int lastID = 0;

  private static int getFreshID() {
    return VBRuleFeature.lastID++;
  }

  public Iterable<VBRuleFeature> collectFeatures() {
    final Function1<VBRuleFeature, Iterable<VBRuleFeature>> _function = (VBRuleFeature it) -> {
      return it.collectFeatures();
    };
    Iterable<VBRuleFeature> _flatMap = IterableExtensions.<VBRuleFeature, VBRuleFeature>flatMap(this.children, _function);
    return Iterables.<VBRuleFeature>concat(_flatMap, Collections.<VBRuleFeature>unmodifiableSet(CollectionLiterals.<VBRuleFeature>newHashSet(this)));
  }

  public VBRuleFeature(final String name) {
    super();
    this.name = name;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + this.ID;
    return prime * result + ((this.children== null) ? 0 : this.children.hashCode());
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
    VBRuleFeature other = (VBRuleFeature) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (other.ID != this.ID)
      return false;
    if (this.children == null) {
      if (other.children != null)
        return false;
    } else if (!this.children.equals(other.children))
      return false;
    return true;
  }

  @Pure
  public String getName() {
    return this.name;
  }

  @Pure
  public int getID() {
    return this.ID;
  }

  @Pure
  public List<VBRuleFeature> getChildren() {
    return this.children;
  }
}

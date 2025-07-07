package acapulco.rulesgeneration.activationdiagrams;

import acapulco.model.Feature;
import java.util.Collections;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Description of an activation decision for a particular feature.
 */
@SuppressWarnings("all")
public class FeatureDecision extends ActivationDiagramNode {
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final Feature feature;

  @Accessors(AccessorType.PUBLIC_GETTER)
  private final boolean activate;

  public FeatureDecision(final Feature feature, final boolean activate) {
    this.feature = feature;
    this.activate = activate;
  }

  @Override
  public Set<FeatureDecision> collectFeatureDecisions() {
    return Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(this));
  }

  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    String _name = this.feature.getName();
    _builder.append(_name);
    String _xifexpression = null;
    if (this.activate) {
      _xifexpression = "+";
    } else {
      _xifexpression = "-";
    }
    _builder.append(_xifexpression);
    return _builder.toString();
  }

  @Override
  public int hashCode() {
    int _hashCode = this.feature.hashCode();
    int _xifexpression = (int) 0;
    if (this.activate) {
      _xifexpression = 1;
    } else {
      _xifexpression = 0;
    }
    return (_hashCode + _xifexpression);
  }

  @Override
  public boolean equals(final Object other) {
    boolean _xifexpression = false;
    if ((other instanceof FeatureDecision)) {
      _xifexpression = ((this.feature == ((FeatureDecision)other).feature) && (Boolean.valueOf(this.activate) == Boolean.valueOf(((FeatureDecision)other).activate)));
    } else {
      _xifexpression = false;
    }
    return _xifexpression;
  }

  @Pure
  public Feature getFeature() {
    return this.feature;
  }

  @Pure
  public boolean isActivate() {
    return this.activate;
  }
}

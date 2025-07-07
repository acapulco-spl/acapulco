package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class ActPar implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      Set<Set<FeatureDecision>> _xblockexpression = null;
      {
        final Feature parent = fd.getFeature().getParentFeature();
        Set<Set<FeatureDecision>> _xifexpression_1 = null;
        if (((parent != null) && (!fmHelper.getAlwaysActiveFeatures().contains(parent)))) {
          _xifexpression_1 = ApplicationPrinciple.only(ApplicationPrinciple.activate(parent));
        } else {
          _xifexpression_1 = CollectionLiterals.<Set<FeatureDecision>>emptySet();
        }
        _xblockexpression = _xifexpression_1;
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = CollectionLiterals.<Set<FeatureDecision>>emptySet();
    }
    return _xifexpression;
  }
}

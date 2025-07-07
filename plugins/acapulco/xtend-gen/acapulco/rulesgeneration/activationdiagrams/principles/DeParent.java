package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class DeParent implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    boolean _not = (!_isActivate);
    if (_not) {
      Set<Set<FeatureDecision>> _xblockexpression = null;
      {
        final Feature parentFeature = fd.getFeature().getParentFeature();
        Set<Set<FeatureDecision>> _xifexpression_1 = null;
        if ((((parentFeature != null) && (!fmHelper.getAlwaysActiveFeatures().contains(parentFeature))) && (!fd.getFeature().isOptional()))) {
          _xifexpression_1 = ApplicationPrinciple.only(ApplicationPrinciple.deactivate(parentFeature));
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

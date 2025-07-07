package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class DeReq implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    boolean _not = (!_isActivate);
    if (_not) {
      Set<Set<FeatureDecision>> _xifexpression_1 = null;
      boolean _hasDeactivableCTCForDeactivateF = fmHelper.hasDeactivableCTCForDeactivateF(fd.getFeature());
      if (_hasDeactivableCTCForDeactivateF) {
        final Function1<Feature, FeatureDecision> _function = (Feature it) -> {
          return ApplicationPrinciple.deactivate(it);
        };
        _xifexpression_1 = ApplicationPrinciple.allOf(IterableExtensions.<Feature, FeatureDecision>map(fmHelper.getDeactivableCTCFeaturesForDeactivateF(fd.getFeature()), _function));
      } else {
        _xifexpression_1 = CollectionLiterals.<Set<FeatureDecision>>emptySet();
      }
      _xifexpression = _xifexpression_1;
    } else {
      _xifexpression = CollectionLiterals.<Set<FeatureDecision>>emptySet();
    }
    return _xifexpression;
  }
}

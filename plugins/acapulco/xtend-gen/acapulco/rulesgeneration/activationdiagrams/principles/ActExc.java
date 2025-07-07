package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class ActExc implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      Set<Set<FeatureDecision>> _xblockexpression = null;
      {
        final Set<Feature> excludedFeatures = fmHelper.getDeactivableCTCFeaturesForActivateF(fd.getFeature());
        final Function1<Feature, FeatureDecision> _function = (Feature it) -> {
          return ApplicationPrinciple.deactivate(it);
        };
        _xblockexpression = ApplicationPrinciple.allOf(IterableExtensions.<Feature, FeatureDecision>map(excludedFeatures, _function));
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = CollectionLiterals.<Set<FeatureDecision>>emptySet();
    }
    return _xifexpression;
  }
}

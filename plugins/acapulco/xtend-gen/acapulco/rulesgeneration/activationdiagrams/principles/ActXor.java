package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class ActXor implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, @Extension final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      Set<Set<FeatureDecision>> _xifexpression_1 = null;
      boolean _isXORGroup = fmHelper.isXORGroup(fd.getFeature().getParentFeature());
      if (_isXORGroup) {
        final Function1<Feature, Boolean> _function = (Feature it) -> {
          Feature _feature = fd.getFeature();
          return Boolean.valueOf((it == _feature));
        };
        final Function1<Feature, FeatureDecision> _function_1 = (Feature it) -> {
          return ApplicationPrinciple.deactivate(it);
        };
        _xifexpression_1 = ApplicationPrinciple.allOf(IterableExtensions.<Feature, FeatureDecision>map(IterableExtensions.<Feature>reject(fd.getFeature().getParentFeature().getOwnedFeatures(), _function), _function_1));
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

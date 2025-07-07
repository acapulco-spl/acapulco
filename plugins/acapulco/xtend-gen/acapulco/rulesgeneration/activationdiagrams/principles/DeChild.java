package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class DeChild implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    boolean _not = (!_isActivate);
    if (_not) {
      final Function1<Feature, FeatureDecision> _function = (Feature it) -> {
        return ApplicationPrinciple.deactivate(it);
      };
      _xifexpression = ApplicationPrinciple.allOf(ListExtensions.<Feature, FeatureDecision>map(fd.getFeature().getOwnedFeatures(), _function));
    } else {
      _xifexpression = CollectionLiterals.<Set<FeatureDecision>>emptySet();
    }
    return _xifexpression;
  }
}

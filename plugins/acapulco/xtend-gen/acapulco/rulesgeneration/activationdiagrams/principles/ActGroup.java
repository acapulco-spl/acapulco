package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class ActGroup implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, @Extension final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      Set<Set<FeatureDecision>> _xifexpression_1 = null;
      if ((fmHelper.isORGroup(fd.getFeature()) || fmHelper.isXORGroup(fd.getFeature()))) {
        final Function1<Feature, FeatureDecision> _function = (Feature it) -> {
          return ApplicationPrinciple.activate(it);
        };
        _xifexpression_1 = ApplicationPrinciple.oneOf(ListExtensions.<Feature, FeatureDecision>map(fd.getFeature().getOwnedFeatures(), _function));
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

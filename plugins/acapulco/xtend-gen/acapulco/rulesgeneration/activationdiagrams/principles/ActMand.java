package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;

/**
 * Activate the mandatory children of an activated feature
 */
@SuppressWarnings("all")
public class ActMand implements ApplicationPrinciple {
  @Override
  public Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper) {
    Set<Set<FeatureDecision>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      final Function1<Feature, FeatureDecision> _function = (Feature it) -> {
        return ApplicationPrinciple.activate(it);
      };
      _xifexpression = ApplicationPrinciple.allOf(ListExtensions.<Feature, FeatureDecision>map(fmHelper.getMandatoryChildren(fd.getFeature()), _function));
    } else {
      _xifexpression = CollectionLiterals.<Set<FeatureDecision>>emptySet();
    }
    return _xifexpression;
  }
}

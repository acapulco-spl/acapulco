package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import java.util.Collections;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class PrinciplesUtil {
  private static Set<ApplicationPrinciple> activationPrinciples = Collections.<ApplicationPrinciple>unmodifiableSet(CollectionLiterals.<ApplicationPrinciple>newHashSet(new ActMand(), new ActPar(), new ActReq(), new ActGroup(), new ActXor(), new ActExc()));

  private static Set<ApplicationPrinciple> deactivationPrinciples = Collections.<ApplicationPrinciple>unmodifiableSet(CollectionLiterals.<ApplicationPrinciple>newHashSet(new DeChild(), new DeXor(), new DeOr(), new DeParent(), new DeReq()));

  public static Set<Set<Set<FeatureDecision>>> applyPrinciples(final FeatureDecision fd, @Extension final FeatureModelHelper fmHelper) {
    Iterable<Set<Set<FeatureDecision>>> _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      final Function1<ApplicationPrinciple, Set<Set<FeatureDecision>>> _function = (ApplicationPrinciple it) -> {
        return it.applyTo(fd, fmHelper);
      };
      _xifexpression = IterableExtensions.<ApplicationPrinciple, Set<Set<FeatureDecision>>>map(PrinciplesUtil.activationPrinciples, _function);
    } else {
      final Function1<ApplicationPrinciple, Set<Set<FeatureDecision>>> _function_1 = (ApplicationPrinciple it) -> {
        return it.applyTo(fd, fmHelper);
      };
      _xifexpression = IterableExtensions.<ApplicationPrinciple, Set<Set<FeatureDecision>>>map(PrinciplesUtil.deactivationPrinciples, _function_1);
    }
    final Function1<Set<Set<FeatureDecision>>, Set<Set<FeatureDecision>>> _function_2 = (Set<Set<FeatureDecision>> it) -> {
      final Function1<Set<FeatureDecision>, Set<FeatureDecision>> _function_3 = (Set<FeatureDecision> it_1) -> {
        final Function1<FeatureDecision, Boolean> _function_4 = (FeatureDecision it_2) -> {
          return Boolean.valueOf((fmHelper.getAlwaysActiveFeatures().contains(it_2.getFeature()) || fmHelper.getAlwaysActiveGroupFeatures().contains(it_2.getFeature())));
        };
        return IterableExtensions.<FeatureDecision>toSet(IterableExtensions.<FeatureDecision>reject(it_1, _function_4));
      };
      final Function1<Set<FeatureDecision>, Boolean> _function_4 = (Set<FeatureDecision> it_1) -> {
        return Boolean.valueOf(it_1.isEmpty());
      };
      return IterableExtensions.<Set<FeatureDecision>>toSet(IterableExtensions.<Set<FeatureDecision>>reject(IterableExtensions.<Set<FeatureDecision>, Set<FeatureDecision>>map(it, _function_3), _function_4));
    };
    final Function1<Set<Set<FeatureDecision>>, Boolean> _function_3 = (Set<Set<FeatureDecision>> it) -> {
      return Boolean.valueOf(it.isEmpty());
    };
    return IterableExtensions.<Set<Set<FeatureDecision>>>toSet(IterableExtensions.<Set<Set<FeatureDecision>>>reject(IterableExtensions.<Set<Set<FeatureDecision>>, Set<Set<FeatureDecision>>>map(_xifexpression, _function_2), _function_3));
  }
}

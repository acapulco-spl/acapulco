package acapulco.rulesgeneration.activationdiagrams.principles;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public interface ApplicationPrinciple {
  /**
   * Result is an OR of ANDs
   */
  Set<Set<FeatureDecision>> applyTo(final FeatureDecision fd, final FeatureModelHelper fmHelper);

  /**
   * Produce an OR of the given feature decisions
   */
  static Set<Set<FeatureDecision>> oneOf(final Iterable<FeatureDecision> fds) {
    final Function1<FeatureDecision, Set<FeatureDecision>> _function = (FeatureDecision it) -> {
      return Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(it));
    };
    Set<Set<FeatureDecision>> _set = IterableExtensions.<Set<FeatureDecision>>toSet(IterableExtensions.<FeatureDecision, Set<FeatureDecision>>map(fds, _function));
    return new HashSet<Set<FeatureDecision>>(_set);
  }

  /**
   * Add an OR of the given feature decisions to the existing set of consequences
   */
  static Set<Set<FeatureDecision>> or(final Set<Set<FeatureDecision>> existingConsequences, final FeatureDecision fd) {
    return IterableExtensions.<Set<FeatureDecision>>toSet(Iterables.<Set<FeatureDecision>>concat(existingConsequences, Collections.<Set<FeatureDecision>>unmodifiableSet(CollectionLiterals.<Set<FeatureDecision>>newHashSet(Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(fd))))));
  }

  /**
   * Produce an AND of the given feature decisions
   */
  static Set<Set<FeatureDecision>> allOf(final Iterable<FeatureDecision> fds) {
    Set<FeatureDecision> _set = IterableExtensions.<FeatureDecision>toSet(fds);
    return Collections.<Set<FeatureDecision>>unmodifiableSet(CollectionLiterals.<Set<FeatureDecision>>newHashSet(_set));
  }

  /**
   * Produce a single consequence
   */
  static Set<Set<FeatureDecision>> only(final FeatureDecision fd) {
    return Collections.<Set<FeatureDecision>>unmodifiableSet(CollectionLiterals.<Set<FeatureDecision>>newHashSet(Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(fd))));
  }

  static FeatureDecision activate(final Feature f) {
    return new FeatureDecision(f, true);
  }

  static FeatureDecision deactivate(final Feature f) {
    return new FeatureDecision(f, false);
  }
}

package acapulco.rulesgeneration.activationdiagrams;

import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrAlternative;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Helper class to make the constraint generation reusable.
 */
@SuppressWarnings("all")
public abstract class VBRuleFeatureConstraintGenerator {
  public static String computeConstraintExpression(final FeatureActivationSubDiagram fasd) {
    Iterable<String> _featureModelExpressions = VBRuleFeatureConstraintGenerator.featureModelExpressions(fasd);
    Iterable<String> _orImplicationExpressions = VBRuleFeatureConstraintGenerator.orImplicationExpressions(fasd);
    Iterable<String> _plus = Iterables.<String>concat(_featureModelExpressions, _orImplicationExpressions);
    Iterable<String> _featureExclusionExpressions = VBRuleFeatureConstraintGenerator.featureExclusionExpressions(fasd);
    Iterable<String> _plus_1 = Iterables.<String>concat(_plus, _featureExclusionExpressions);
    Iterable<CharSequence> _orFixingExpressions = VBRuleFeatureConstraintGenerator.orFixingExpressions(fasd);
    Iterable<CharSequence> _plus_2 = Iterables.<CharSequence>concat(_plus_1, _orFixingExpressions);
    Iterable<String> _orCycleBreakers = VBRuleFeatureConstraintGenerator.orCycleBreakers(fasd);
    return IterableExtensions.join(Iterables.<CharSequence>concat(_plus_2, _orCycleBreakers), " & ");
  }

  private static Iterable<CharSequence> orFixingExpressions(final FeatureActivationSubDiagram fasd) {
    final Function1<Map.Entry<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>, Iterable<CharSequence>> _function = (Map.Entry<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> it) -> {
      return VBRuleFeatureConstraintGenerator.orFixingExpressionsFor(it.getKey(), it.getValue());
    };
    return IterableExtensions.<Map.Entry<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>, CharSequence>flatMap(fasd.getOrFixings().entrySet(), _function);
  }

  private static Iterable<CharSequence> orFixingExpressionsFor(final VBRuleOrFeature orFeature, final Map<VBRuleFeature, Set<VBRuleOrAlternative>> fixings) {
    final Function1<Map.Entry<VBRuleFeature, Set<VBRuleOrAlternative>>, CharSequence> _function = (Map.Entry<VBRuleFeature, Set<VBRuleOrAlternative>> it) -> {
      VBRuleFeature _key = it.getKey();
      return VBRuleFeatureConstraintGenerator.allJointlyImply(Collections.<VBRuleFeature>unmodifiableList(CollectionLiterals.<VBRuleFeature>newArrayList(orFeature, _key)), IterableExtensions.<VBRuleOrAlternative>head(it.getValue()));
    };
    return IterableExtensions.<Map.Entry<VBRuleFeature, Set<VBRuleOrAlternative>>, CharSequence>map(fixings.entrySet(), _function);
  }

  private static Iterable<String> orCycleBreakers(final FeatureActivationSubDiagram fasd) {
    Iterable<String> _xblockexpression = null;
    {
      final OrImplicationGraph orImplGraph = new OrImplicationGraph(fasd);
      final Function1<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>, String> _function = (Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>> it) -> {
        return VBRuleFeatureConstraintGenerator.impliesOneOf(it.getKey(), it.getValue());
      };
      _xblockexpression = IterableExtensions.<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>, String>map(orImplGraph.getCycleEntries().entrySet(), _function);
    }
    return _xblockexpression;
  }

  private static Iterable<String> featureExclusionExpressions(final FeatureActivationSubDiagram fasd) {
    final Function1<Pair<VBRuleFeature, VBRuleFeature>, String> _function = (Pair<VBRuleFeature, VBRuleFeature> it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(!");
      String _name = it.getKey().getName();
      _builder.append(_name);
      _builder.append(" | !");
      String _name_1 = it.getValue().getName();
      _builder.append(_name_1);
      _builder.append(")");
      return _builder.toString();
    };
    return IterableExtensions.<Pair<VBRuleFeature, VBRuleFeature>, String>map(fasd.getFeatureExclusions(), _function);
  }

  private static Iterable<String> orImplicationExpressions(final FeatureActivationSubDiagram fasd) {
    Iterable<String> _xblockexpression = null;
    {
      final Function1<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>, Iterable<Pair<VBRuleOrFeature, VBRuleFeature>>> _function = (Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>> e) -> {
        final Function1<VBRuleOrFeature, Pair<VBRuleOrFeature, VBRuleFeature>> _function_1 = (VBRuleOrFeature it) -> {
          VBRuleFeature _key = e.getKey();
          return Pair.<VBRuleOrFeature, VBRuleFeature>of(it, _key);
        };
        return IterableExtensions.<VBRuleOrFeature, Pair<VBRuleOrFeature, VBRuleFeature>>map(e.getValue(), _function_1);
      };
      final Function1<Pair<VBRuleOrFeature, VBRuleFeature>, VBRuleOrFeature> _function_1 = (Pair<VBRuleOrFeature, VBRuleFeature> it) -> {
        return it.getKey();
      };
      final Function1<List<Pair<VBRuleOrFeature, VBRuleFeature>>, Set<VBRuleFeature>> _function_2 = (List<Pair<VBRuleOrFeature, VBRuleFeature>> it) -> {
        final Function1<Pair<VBRuleOrFeature, VBRuleFeature>, VBRuleFeature> _function_3 = (Pair<VBRuleOrFeature, VBRuleFeature> it_1) -> {
          return it_1.getValue();
        };
        return IterableExtensions.<VBRuleFeature>toSet(ListExtensions.<Pair<VBRuleOrFeature, VBRuleFeature>, VBRuleFeature>map(it, _function_3));
      };
      final Map<VBRuleOrFeature, Set<VBRuleFeature>> separatedOrImplications = MapExtensions.<VBRuleOrFeature, List<Pair<VBRuleOrFeature, VBRuleFeature>>, Set<VBRuleFeature>>mapValues(IterableExtensions.<VBRuleOrFeature, Pair<VBRuleOrFeature, VBRuleFeature>>groupBy(IterableExtensions.<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>, Pair<VBRuleOrFeature, VBRuleFeature>>flatMap(fasd.getOrImplications().entrySet(), _function), _function_1), _function_2);
      final Function1<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>, Boolean> _function_3 = (Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>> it) -> {
        return Boolean.valueOf(it.getValue().isEmpty());
      };
      final Function1<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>, Iterable<String>> _function_4 = (Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>> it) -> {
        return VBRuleFeatureConstraintGenerator.impliesAllOf(it.getKey(), it.getValue());
      };
      Iterable<String> _flatMap = IterableExtensions.<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>, String>flatMap(IterableExtensions.<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>>reject(fasd.getOrImplications().entrySet(), _function_3), _function_4);
      final Function1<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>, Boolean> _function_5 = (Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>> it) -> {
        return Boolean.valueOf(it.getValue().isEmpty());
      };
      final Function1<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>, String> _function_6 = (Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>> it) -> {
        return VBRuleFeatureConstraintGenerator.impliesOneOf(it.getKey(), it.getValue());
      };
      Iterable<String> _map = IterableExtensions.<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>, String>map(IterableExtensions.<Map.Entry<VBRuleOrFeature, Set<VBRuleFeature>>>reject(separatedOrImplications.entrySet(), _function_5), _function_6);
      _xblockexpression = Iterables.<String>concat(_flatMap, _map);
    }
    return _xblockexpression;
  }

  private static Iterable<String> featureModelExpressions(final FeatureActivationSubDiagram fasd) {
    String _name = fasd.getVbRuleFeatures().getName();
    final Function1<VBRuleFeature, Iterable<String>> _function = (VBRuleFeature feature) -> {
      String _impliesOneOf = VBRuleFeatureConstraintGenerator.impliesOneOf(feature, feature.getChildren());
      Iterable<String> _eachImplies = VBRuleFeatureConstraintGenerator.eachImplies(feature.getChildren(), feature);
      return Iterables.<String>concat(Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet(_impliesOneOf)), _eachImplies);
    };
    Iterable<String> _flatMap = IterableExtensions.<VBRuleFeature, String>flatMap(fasd.getVbRuleFeatures().getChildren(), _function);
    Iterable<String> _plus = Iterables.<String>concat(Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet(_name)), _flatMap);
    final Function1<VBRuleFeature, Iterable<String>> _function_1 = (VBRuleFeature orFeature) -> {
      final Function1<VBRuleFeature, Iterable<String>> _function_2 = (VBRuleFeature alternative) -> {
        final Function1<VBRuleFeature, Boolean> _function_3 = (VBRuleFeature it) -> {
          return Boolean.valueOf((it == alternative));
        };
        return VBRuleFeatureConstraintGenerator.impliesNoneOf(alternative, IterableExtensions.<VBRuleFeature>reject(orFeature.getChildren(), _function_3));
      };
      return IterableExtensions.<VBRuleFeature, String>flatMap(orFeature.getChildren(), _function_2);
    };
    Iterable<String> _flatMap_1 = IterableExtensions.<VBRuleFeature, String>flatMap(fasd.getVbRuleFeatures().getChildren(), _function_1);
    return Iterables.<String>concat(_plus, _flatMap_1);
  }

  private static Iterable<String> impliesAllOf(final VBRuleFeature antecedent, final Iterable<? extends VBRuleFeature> consequent) {
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(!");
      String _name = antecedent.getName();
      _builder.append(_name);
      _builder.append(" | ");
      String _name_1 = it.getName();
      _builder.append(_name_1);
      _builder.append(")");
      return _builder.toString();
    };
    return IterableExtensions.map(consequent, _function);
  }

  private static Iterable<String> impliesNoneOf(final VBRuleFeature antecedent, final Iterable<? extends VBRuleFeature> consequent) {
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(!");
      String _name = antecedent.getName();
      _builder.append(_name);
      _builder.append(" | !");
      String _name_1 = it.getName();
      _builder.append(_name_1);
      _builder.append(")");
      return _builder.toString();
    };
    return IterableExtensions.map(consequent, _function);
  }

  private static String impliesOneOf(final VBRuleFeature antecedent, final Iterable<? extends VBRuleFeature> consequent) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(!");
    String _name = antecedent.getName();
    _builder.append(_name);
    _builder.append(" | (");
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      return it.getName();
    };
    String _join = IterableExtensions.join(IterableExtensions.map(consequent, _function), " | ");
    _builder.append(_join);
    _builder.append("))");
    return _builder.toString();
  }

  private static Iterable<String> eachImplies(final Iterable<? extends VBRuleFeature> antecedent, final VBRuleFeature consequent) {
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(!");
      String _name = it.getName();
      _builder.append(_name);
      _builder.append(" | ");
      String _name_1 = consequent.getName();
      _builder.append(_name_1);
      _builder.append(")");
      return _builder.toString();
    };
    return IterableExtensions.map(antecedent, _function);
  }

  private static CharSequence allJointlyImply(final Iterable<? extends VBRuleFeature> antecedents, final VBRuleFeature consequent) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("!");
      String _name = it.getName();
      _builder_1.append(_name);
      return _builder_1.toString();
    };
    String _join = IterableExtensions.join(IterableExtensions.map(antecedents, _function), " | ");
    _builder.append(_join);
    _builder.append(" | ");
    String _name = consequent.getName();
    _builder.append(_name);
    _builder.append(")");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}

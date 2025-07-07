package acapulco.rulesgeneration.activationdiagrams;

import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrAlternative;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The graph of or-implications in a FASD.
 */
@SuppressWarnings("all")
public class OrImplicationGraph {
  @Data
  private static class AdditionDeletionSet {
    private final HashSet<VBRuleFeature> additions = new HashSet<VBRuleFeature>();

    private final HashSet<VBRuleFeature> deletions = new HashSet<VBRuleFeature>();

    public Set<VBRuleFeature> getEffectiveSet() {
      final Function1<VBRuleFeature, Boolean> _function = (VBRuleFeature it) -> {
        return Boolean.valueOf(this.deletions.contains(it));
      };
      return IterableExtensions.<VBRuleFeature>toSet(IterableExtensions.<VBRuleFeature>reject(this.additions, _function));
    }

    public boolean operator_add(final OrImplicationGraph.AdditionDeletionSet features) {
      boolean _xblockexpression = false;
      {
        Iterables.<VBRuleFeature>addAll(this.additions, features.additions);
        _xblockexpression = Iterables.<VBRuleFeature>addAll(this.deletions, features.deletions);
      }
      return _xblockexpression;
    }

    public boolean operator_add(final Iterable<VBRuleFeature> features) {
      return Iterables.<VBRuleFeature>addAll(this.additions, features);
    }

    public boolean operator_remove(final VBRuleFeature feature) {
      return this.deletions.add(feature);
    }

    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.additions== null) ? 0 : this.additions.hashCode());
      return prime * result + ((this.deletions== null) ? 0 : this.deletions.hashCode());
    }

    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      OrImplicationGraph.AdditionDeletionSet other = (OrImplicationGraph.AdditionDeletionSet) obj;
      if (this.additions == null) {
        if (other.additions != null)
          return false;
      } else if (!this.additions.equals(other.additions))
        return false;
      if (this.deletions == null) {
        if (other.deletions != null)
          return false;
      } else if (!this.deletions.equals(other.deletions))
        return false;
      return true;
    }

    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("additions", this.additions);
      b.add("deletions", this.deletions);
      return b.toString();
    }

    @Pure
    public HashSet<VBRuleFeature> getAdditions() {
      return this.additions;
    }

    @Pure
    public HashSet<VBRuleFeature> getDeletions() {
      return this.deletions;
    }
  }

  private final FeatureActivationSubDiagram fasd;

  @Accessors(AccessorType.PUBLIC_GETTER)
  private final HashSet<VBRuleFeature> nodes = new HashSet<VBRuleFeature>();

  @Accessors(AccessorType.PUBLIC_GETTER)
  private final HashMap<VBRuleFeature, List<? extends VBRuleFeature>> edges = new HashMap<VBRuleFeature, List<? extends VBRuleFeature>>();

  private final HashMap<VBRuleOrFeature, Set<VBRuleFeature>> invertedOrImplications = new HashMap<VBRuleOrFeature, Set<VBRuleFeature>>();

  public OrImplicationGraph(final FeatureActivationSubDiagram fasd) {
    this.fasd = fasd;
    this.initialise();
  }

  private void initialise() {
    Iterable<VBRuleFeature> _collectFeatures = this.fasd.getVbRuleFeatures().collectFeatures();
    Iterables.<VBRuleFeature>addAll(this.nodes, _collectFeatures);
    final Consumer<VBRuleFeature> _function = (VBRuleFeature orFeature) -> {
      List<VBRuleFeature> _children = orFeature.getChildren();
      ArrayList<VBRuleFeature> _arrayList = new ArrayList<VBRuleFeature>(_children);
      this.edges.put(orFeature, _arrayList);
    };
    this.fasd.getVbRuleFeatures().getChildren().forEach(_function);
    final Consumer<Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>>> _function_1 = (Map.Entry<VBRuleFeature, Set<VBRuleOrFeature>> e) -> {
      this.edges.put(e.getKey(), IterableExtensions.<VBRuleOrFeature>toList(e.getValue()));
      final Consumer<VBRuleOrFeature> _function_2 = (VBRuleOrFeature orAlternativeFeature) -> {
        Set<VBRuleFeature> invertedSet = this.invertedOrImplications.get(orAlternativeFeature);
        if ((invertedSet == null)) {
          HashSet<VBRuleFeature> _hashSet = new HashSet<VBRuleFeature>();
          invertedSet = _hashSet;
          this.invertedOrImplications.put(orAlternativeFeature, invertedSet);
        }
        VBRuleFeature _key = e.getKey();
        invertedSet.add(_key);
      };
      e.getValue().forEach(_function_2);
    };
    this.fasd.getOrImplications().entrySet().forEach(_function_1);
    this.computeCycles();
  }

  /**
   * Final cycle information: each or feature in the keyset is in at least one cycle, the set of mapped or alternatives are all
   * alternatives that are outside the cycle and lead directly into the cycle. In other words, the cycle should only be active
   * if at least one of the mapped elements is active. This can be captured by requiring that the or-node can only be active
   * if at least one of the mapped alternatives is also active.
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final HashMap<VBRuleOrFeature, Set<VBRuleFeature>> cycleEntries = new HashMap<VBRuleOrFeature, Set<VBRuleFeature>>();

  /**
   * Cycle information as collected. Here, we split this into additions and deletions, so that we can ensure deletions take precedence regardless of when they are collected during the DFS.
   */
  private final HashMap<VBRuleOrFeature, OrImplicationGraph.AdditionDeletionSet> internalCycleEntries = new HashMap<VBRuleOrFeature, OrImplicationGraph.AdditionDeletionSet>();

  /**
   * For every cycle, we only need to find:
   * 
   * 1. One back edge (i.e. or alternative) to break to break the cycle when needed, and
   * 2. All or-alternatives leading into the cycle but not part of the cycle itself.
   * 
   * We do this through a single depth-first search sweep through the or-implications graph.
   */
  private void computeCycles() {
    final HashSet<VBRuleFeature> visited = new HashSet<VBRuleFeature>();
    final HashSet<VBRuleOrFeature> stack = new HashSet<VBRuleOrFeature>();
    this.recursivelyComputeCycles(this.fasd.getVbRuleFeatures(), stack, visited, null);
    this.cycleEntries.clear();
    final Function1<OrImplicationGraph.AdditionDeletionSet, Set<VBRuleFeature>> _function = (OrImplicationGraph.AdditionDeletionSet it) -> {
      return it.getEffectiveSet();
    };
    this.cycleEntries.putAll(MapExtensions.<VBRuleOrFeature, OrImplicationGraph.AdditionDeletionSet, Set<VBRuleFeature>>mapValues(this.internalCycleEntries, _function));
  }

  /**
   * @return the set of features for which we have found a cycle
   */
  private Set<VBRuleOrFeature> _recursivelyComputeCycles(final VBRuleFeature feature, final Set<VBRuleOrFeature> stack, final Set<VBRuleFeature> visited, final VBRuleOrAlternative comingFrom) {
    boolean _contains = visited.contains(feature);
    if (_contains) {
      return CollectionLiterals.<VBRuleOrFeature>emptySet();
    }
    visited.add(feature);
    List<? extends VBRuleFeature> _get = this.edges.get(feature);
    if (_get!=null) {
      final Consumer<VBRuleFeature> _function = (VBRuleFeature it) -> {
        this.recursivelyComputeCycles(it, stack, visited, null);
      };
      _get.forEach(_function);
    }
    return CollectionLiterals.<VBRuleOrFeature>emptySet();
  }

  private Set<VBRuleOrFeature> _recursivelyComputeCycles(final VBRuleOrAlternative feature, final Set<VBRuleOrFeature> stack, final Set<VBRuleFeature> visited, final VBRuleOrAlternative comingFrom) {
    Set<VBRuleOrFeature> _xblockexpression = null;
    {
      boolean _contains = visited.contains(feature);
      if (_contains) {
        return CollectionLiterals.<VBRuleOrFeature>emptySet();
      }
      visited.add(feature);
      final Function1<VBRuleOrFeature, Boolean> _function = (VBRuleOrFeature it) -> {
        return Boolean.valueOf(this.internalCycleEntries.containsKey(it));
      };
      final Consumer<VBRuleOrFeature> _function_1 = (VBRuleOrFeature orFeature) -> {
        OrImplicationGraph.AdditionDeletionSet _get = this.internalCycleEntries.get(orFeature);
        _get.operator_remove(feature);
      };
      IterableExtensions.<VBRuleOrFeature>filter(Iterables.<VBRuleOrFeature>filter(stack, VBRuleOrFeature.class), _function).forEach(_function_1);
      List<? extends VBRuleFeature> _get = this.edges.get(feature);
      Iterable<VBRuleOrFeature> _flatMap = null;
      if (_get!=null) {
        final Function1<VBRuleFeature, Set<VBRuleOrFeature>> _function_2 = (VBRuleFeature it) -> {
          return this.recursivelyComputeCycles(it, stack, visited, feature);
        };
        _flatMap=IterableExtensions.flatMap(_get, _function_2);
      }
      Set<VBRuleOrFeature> _set = null;
      if (_flatMap!=null) {
        _set=IterableExtensions.<VBRuleOrFeature>toSet(_flatMap);
      }
      final Set<VBRuleOrFeature> result = _set;
      Set<VBRuleOrFeature> _xifexpression = null;
      if ((result != null)) {
        _xifexpression = result;
      } else {
        _xifexpression = CollectionLiterals.<VBRuleOrFeature>emptySet();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private Set<VBRuleOrFeature> _recursivelyComputeCycles(final VBRuleOrFeature feature, final Set<VBRuleOrFeature> stack, final Set<VBRuleFeature> visited, final VBRuleOrAlternative comingFrom) {
    Set<VBRuleOrFeature> _xblockexpression = null;
    {
      boolean _contains = visited.contains(feature);
      if (_contains) {
        boolean _contains_1 = stack.contains(feature);
        if (_contains_1) {
          OrImplicationGraph.AdditionDeletionSet cycleEntriesForFeature = this.internalCycleEntries.get(feature);
          if ((cycleEntriesForFeature == null)) {
            OrImplicationGraph.AdditionDeletionSet _additionDeletionSet = new OrImplicationGraph.AdditionDeletionSet();
            cycleEntriesForFeature = _additionDeletionSet;
            this.internalCycleEntries.put(feature, cycleEntriesForFeature);
            final List<VBRuleFeature> featuresToAdd = IterableExtensions.<VBRuleFeature>toList(this.invertedOrImplications.get(feature));
            cycleEntriesForFeature.operator_add(featuresToAdd);
            cycleEntriesForFeature.operator_remove(comingFrom);
          } else {
            cycleEntriesForFeature.operator_remove(comingFrom);
          }
          return Collections.<VBRuleOrFeature>unmodifiableSet(CollectionLiterals.<VBRuleOrFeature>newHashSet(feature));
        } else {
          return CollectionLiterals.<VBRuleOrFeature>emptySet();
        }
      }
      stack.add(feature);
      visited.add(feature);
      List<? extends VBRuleFeature> _get = this.edges.get(feature);
      Iterable<VBRuleOrFeature> _flatMap = null;
      if (_get!=null) {
        final Function1<VBRuleFeature, Set<VBRuleOrFeature>> _function = (VBRuleFeature it) -> {
          return this.recursivelyComputeCycles(it, stack, visited, null);
        };
        _flatMap=IterableExtensions.flatMap(_get, _function);
      }
      Set<VBRuleOrFeature> _set = null;
      if (_flatMap!=null) {
        _set=IterableExtensions.<VBRuleOrFeature>toSet(_flatMap);
      }
      final Set<VBRuleOrFeature> result = _set;
      stack.remove(feature);
      Set<VBRuleOrFeature> _xifexpression = null;
      if ((result != null)) {
        Set<VBRuleOrFeature> _xblockexpression_1 = null;
        {
          final OrImplicationGraph.AdditionDeletionSet cycleEntriesForFeature_1 = this.internalCycleEntries.get(feature);
          if ((cycleEntriesForFeature_1 != null)) {
            result.remove(feature);
            final Consumer<VBRuleOrFeature> _function_1 = (VBRuleOrFeature cycleFeature) -> {
              OrImplicationGraph.AdditionDeletionSet _get_1 = this.internalCycleEntries.get(cycleFeature);
              _get_1.operator_add(cycleEntriesForFeature_1);
              OrImplicationGraph.AdditionDeletionSet _get_2 = this.internalCycleEntries.get(cycleFeature);
              _get_2.operator_remove(comingFrom);
            };
            result.forEach(_function_1);
          } else {
            final List<VBRuleFeature> nodesToAdd = IterableExtensions.<VBRuleFeature>toList(this.invertedOrImplications.get(feature));
            final Consumer<VBRuleOrFeature> _function_2 = (VBRuleOrFeature cycleFeature) -> {
              final OrImplicationGraph.AdditionDeletionSet cycleEntriesForLoopedFeature = this.internalCycleEntries.get(cycleFeature);
              cycleEntriesForLoopedFeature.operator_add(nodesToAdd);
              cycleEntriesForLoopedFeature.operator_remove(comingFrom);
            };
            result.forEach(_function_2);
            System.err.println("Entered the second branch");
          }
          _xblockexpression_1 = result;
        }
        _xifexpression = _xblockexpression_1;
      } else {
        _xifexpression = CollectionLiterals.<VBRuleOrFeature>emptySet();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private Set<VBRuleOrFeature> recursivelyComputeCycles(final VBRuleFeature feature, final Set<VBRuleOrFeature> stack, final Set<VBRuleFeature> visited, final VBRuleOrAlternative comingFrom) {
    if (feature instanceof VBRuleOrAlternative) {
      return _recursivelyComputeCycles((VBRuleOrAlternative)feature, stack, visited, comingFrom);
    } else if (feature instanceof VBRuleOrFeature) {
      return _recursivelyComputeCycles((VBRuleOrFeature)feature, stack, visited, comingFrom);
    } else if (feature != null) {
      return _recursivelyComputeCycles(feature, stack, visited, comingFrom);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(feature, stack, visited, comingFrom).toString());
    }
  }

  @Pure
  public HashSet<VBRuleFeature> getNodes() {
    return this.nodes;
  }

  @Pure
  public HashMap<VBRuleFeature, List<? extends VBRuleFeature>> getEdges() {
    return this.edges;
  }

  @Pure
  public HashMap<VBRuleOrFeature, Set<VBRuleFeature>> getCycleEntries() {
    return this.cycleEntries;
  }
}

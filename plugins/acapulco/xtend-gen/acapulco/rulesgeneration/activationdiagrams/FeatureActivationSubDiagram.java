package acapulco.rulesgeneration.activationdiagrams;

import acapulco.engine.variability.SatSolver;
import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.orimplications.FinalisedOrImplication;
import acapulco.rulesgeneration.activationdiagrams.orimplications.OrImplication;
import acapulco.rulesgeneration.activationdiagrams.orimplications.ProxyOrImplication;
import acapulco.rulesgeneration.activationdiagrams.presenceconditions.FeaturePresenceCondition;
import acapulco.rulesgeneration.activationdiagrams.presenceconditions.PresenceCondition;
import acapulco.rulesgeneration.activationdiagrams.presenceconditions.ProxyPresenceCondition;
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
import java.util.function.Predicate;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * A feature-activation sub-diagram is the subset of the nodes in a feature-activation diagrams required for a particular activation decision.
 * 
 * It contains all information required to generate the VB rule for this feature decision; see public getter methods.
 */
@SuppressWarnings("all")
public class FeatureActivationSubDiagram {
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final FeatureDecision rootDecision;

  @Accessors(AccessorType.PACKAGE_GETTER)
  private final Set<ActivationDiagramNode> subdiagramContents = new HashSet<ActivationDiagramNode>();

  /**
   * The VB-rule feature model's root feature, from which all other features can be found
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private VBRuleFeature vbRuleFeatures = new VBRuleFeature("root");

  /**
   * Minimal set of pairwise exclusions between VB-rule features.
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private Set<Pair<VBRuleFeature, VBRuleFeature>> featureExclusions = null;

  /**
   * Or-fixings show which alternative of an or-node to select depending on choices made for other or-nodes.
   * The map associated to each or-node goes from decisions made elsewhere to the set of or-alternatives of the original or-node.
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private Map<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> orFixings;

  /**
   * The list of features that were removed from the VB rule features because they could never be activated
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private List<VBRuleFeature> deadFeatures;

  /**
   * The final resolved PCs
   */
  private final Map<FeatureDecision, Set<VBRuleFeature>> resolvedPCs = new HashMap<FeatureDecision, Set<VBRuleFeature>>();

  /**
   * The final resolved or implications
   */
  private final HashMap<VBRuleFeature, Set<VBRuleOrFeature>> resolvedOrImplications = new HashMap<VBRuleFeature, Set<VBRuleOrFeature>>();

  /**
   * Helper for quick lookups
   */
  private final HashMap<OrNode, VBRuleOrFeature> orFeatureMap = new HashMap<OrNode, VBRuleOrFeature>();

  /**
   * For every feature touched by this sub-diagram, record the decisions contained in the sub-diagram. There are either 1 or 2 such decisions. If there are 2, then they are conflicting decisions.
   */
  private final HashMap<Feature, Set<FeatureDecision>> featureDecisions = new HashMap<Feature, Set<FeatureDecision>>();

  /**
   * Presence conditions; these may need resolving
   */
  private final HashMap<FeatureDecision, Set<PresenceCondition>> presenceConditions = new HashMap<FeatureDecision, Set<PresenceCondition>>();

  /**
   * Or-implications: Which ORs do we need to make a decision on when we have decided on a given root feature or OrAlternative feature.
   * 
   * This will be filled directly in the forward sweep, but may still contain unresolved proxies at that point. Proxies are then resolved using information from the followOr map.
   */
  private final HashMap<VBRuleFeature, Set<OrImplication>> orImplications = new HashMap<VBRuleFeature, Set<OrImplication>>();

  /**
   * Internal storage for keeping track of information collected at each node during the forward sweep:
   * this is the set of Or nodes directly reachable from the given node.
   */
  private final HashMap<ActivationDiagramNode, Set<OrImplication>> followOrs = new HashMap<ActivationDiagramNode, Set<OrImplication>>();

  public FeatureActivationSubDiagram(final FeatureDecision decision) {
    this.rootDecision = decision;
    this.initialise();
  }

  public Iterable<FeatureDecision> getFeatureDecisions() {
    return Iterables.<FeatureDecision>filter(this.subdiagramContents, FeatureDecision.class);
  }

  public Map<FeatureDecision, Set<VBRuleFeature>> getPresenceConditions() {
    return this.resolvedPCs;
  }

  public HashMap<VBRuleFeature, Set<VBRuleOrFeature>> getOrImplications() {
    return this.resolvedOrImplications;
  }

  public Iterable<VBRuleFeature> collectAllFeatures() {
    return this.vbRuleFeatures.collectFeatures();
  }

  private void initialise() {
    FeaturePresenceCondition _featurePresenceCondition = new FeaturePresenceCondition(this.vbRuleFeatures);
    final Set<OrImplication> rootImplications = this.visit(this.rootDecision, _featurePresenceCondition, this.rootDecision);
    this.orImplications.put(this.vbRuleFeatures, rootImplications);
    final Function1<Map.Entry<FeatureDecision, Set<PresenceCondition>>, Pair<FeatureDecision, Set<VBRuleFeature>>> _function = (Map.Entry<FeatureDecision, Set<PresenceCondition>> e) -> {
      Pair<FeatureDecision, Set<VBRuleFeature>> _xblockexpression = null;
      {
        final Function1<PresenceCondition, Set<VBRuleFeature>> _function_1 = (PresenceCondition it) -> {
          Set<VBRuleFeature> _xblockexpression_1 = null;
          {
            FeatureDecision _key = e.getKey();
            final HashSet<FeatureDecision> fds = new HashSet<FeatureDecision>(Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(_key)));
            _xblockexpression_1 = it.resolve(this.presenceConditions, fds);
          }
          return _xblockexpression_1;
        };
        final Set<VBRuleFeature> resolvedPC = IterableExtensions.<VBRuleFeature>toSet(IterableExtensions.<PresenceCondition, VBRuleFeature>flatMap(e.getValue(), _function_1));
        FeatureDecision _key = e.getKey();
        Set<VBRuleFeature> _xifexpression = null;
        boolean _contains = resolvedPC.contains(this.vbRuleFeatures);
        if (_contains) {
          _xifexpression = Collections.<VBRuleFeature>unmodifiableSet(CollectionLiterals.<VBRuleFeature>newHashSet(this.vbRuleFeatures));
        } else {
          _xifexpression = resolvedPC;
        }
        _xblockexpression = Pair.<FeatureDecision, Set<VBRuleFeature>>of(_key, _xifexpression);
      }
      return _xblockexpression;
    };
    final Consumer<Pair<FeatureDecision, Set<VBRuleFeature>>> _function_1 = (Pair<FeatureDecision, Set<VBRuleFeature>> it) -> {
      this.resolvedPCs.put(it.getKey(), it.getValue());
    };
    IterableExtensions.<Map.Entry<FeatureDecision, Set<PresenceCondition>>, Pair<FeatureDecision, Set<VBRuleFeature>>>map(this.presenceConditions.entrySet(), _function).forEach(_function_1);
    final Function1<Map.Entry<VBRuleFeature, Set<OrImplication>>, Pair<VBRuleFeature, Set<VBRuleOrFeature>>> _function_2 = (Map.Entry<VBRuleFeature, Set<OrImplication>> e) -> {
      VBRuleFeature _key = e.getKey();
      final Function1<OrImplication, Set<VBRuleOrFeature>> _function_3 = (OrImplication it) -> {
        Set<VBRuleOrFeature> _xblockexpression = null;
        {
          final HashSet<ActivationDiagramNode> nodes = new HashSet<ActivationDiagramNode>();
          _xblockexpression = it.resolve(this.followOrs, nodes);
        }
        return _xblockexpression;
      };
      Set<VBRuleOrFeature> _set = IterableExtensions.<VBRuleOrFeature>toSet(IterableExtensions.<OrImplication, VBRuleOrFeature>flatMap(e.getValue(), _function_3));
      return Pair.<VBRuleFeature, Set<VBRuleOrFeature>>of(_key, _set);
    };
    final Consumer<Pair<VBRuleFeature, Set<VBRuleOrFeature>>> _function_3 = (Pair<VBRuleFeature, Set<VBRuleOrFeature>> it) -> {
      this.resolvedOrImplications.put(it.getKey(), it.getValue());
    };
    IterableExtensions.<Map.Entry<VBRuleFeature, Set<OrImplication>>, Pair<VBRuleFeature, Set<VBRuleOrFeature>>>map(this.orImplications.entrySet(), _function_2).forEach(_function_3);
    this.orFixings = this.calculateOrFixings();
    final Function1<Set<FeatureDecision>, Boolean> _function_4 = (Set<FeatureDecision> it) -> {
      int _size = it.size();
      return Boolean.valueOf((_size == 2));
    };
    final Function1<Set<FeatureDecision>, Iterable<Pair<VBRuleFeature, VBRuleFeature>>> _function_5 = (Set<FeatureDecision> it) -> {
      Iterable<Pair<VBRuleFeature, VBRuleFeature>> _xblockexpression = null;
      {
        final Set<VBRuleFeature> leftDecisions = this.getPresenceConditions().get(((Object[])Conversions.unwrapArray(it, Object.class))[0]);
        final Set<VBRuleFeature> rightDecisions = this.getPresenceConditions().get(((Object[])Conversions.unwrapArray(it, Object.class))[1]);
        final Function1<VBRuleFeature, Iterable<Pair<VBRuleFeature, VBRuleFeature>>> _function_6 = (VBRuleFeature ld) -> {
          final Function1<VBRuleFeature, Pair<VBRuleFeature, VBRuleFeature>> _function_7 = (VBRuleFeature rd) -> {
            Pair<VBRuleFeature, VBRuleFeature> _xifexpression = null;
            int _iD = ld.getID();
            int _iD_1 = rd.getID();
            boolean _greaterThan = (_iD > _iD_1);
            if (_greaterThan) {
              _xifexpression = Pair.<VBRuleFeature, VBRuleFeature>of(rd, ld);
            } else {
              _xifexpression = Pair.<VBRuleFeature, VBRuleFeature>of(ld, rd);
            }
            return _xifexpression;
          };
          return IterableExtensions.<VBRuleFeature, Pair<VBRuleFeature, VBRuleFeature>>map(rightDecisions, _function_7);
        };
        _xblockexpression = IterableExtensions.<VBRuleFeature, Pair<VBRuleFeature, VBRuleFeature>>flatMap(leftDecisions, _function_6);
      }
      return _xblockexpression;
    };
    Set<Pair<VBRuleFeature, VBRuleFeature>> _set = IterableExtensions.<Pair<VBRuleFeature, VBRuleFeature>>toSet(IterableExtensions.<Set<FeatureDecision>, Pair<VBRuleFeature, VBRuleFeature>>flatMap(IterableExtensions.<Set<FeatureDecision>>filter(this.featureDecisions.values(), _function_4), _function_5));
    HashSet<Pair<VBRuleFeature, VBRuleFeature>> _hashSet = new HashSet<Pair<VBRuleFeature, VBRuleFeature>>(_set);
    this.featureExclusions = _hashSet;
    this.removeDeadOrUnusedFeatures();
  }

  private HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> calculateOrFixings() {
    HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> _xblockexpression = null;
    {
      final HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> result = new HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>();
      final Function1<VBRuleFeature, VBRuleOrFeature> _function = (VBRuleFeature it) -> {
        return ((VBRuleOrFeature) it);
      };
      final Consumer<VBRuleOrFeature> _function_1 = (VBRuleOrFeature orFeature) -> {
        final OrNode orNode = orFeature.getOrNode();
        final HashMap<VBRuleFeature, Set<VBRuleOrAlternative>> orNodeResultMap = new HashMap<VBRuleFeature, Set<VBRuleOrAlternative>>();
        result.put(orFeature, orNodeResultMap);
        final Procedure2<ActivationDiagramNode, Integer> _function_2 = (ActivationDiagramNode adn, Integer idx) -> {
          VBRuleFeature _get = orFeature.getChildren().get((idx).intValue());
          final VBRuleOrAlternative correspondingAlternative = ((VBRuleOrAlternative) _get);
          final Consumer<FeatureDecision> _function_3 = (FeatureDecision fd) -> {
            Set<VBRuleFeature> _get_1 = this.resolvedPCs.get(fd);
            if (_get_1!=null) {
              final Consumer<VBRuleFeature> _function_4 = (VBRuleFeature pc) -> {
                Set<VBRuleOrAlternative> alternativesForFeature = orNodeResultMap.get(pc);
                if ((alternativesForFeature == null)) {
                  HashSet<VBRuleOrAlternative> _hashSet = new HashSet<VBRuleOrAlternative>();
                  alternativesForFeature = _hashSet;
                  orNodeResultMap.put(pc, alternativesForFeature);
                }
                alternativesForFeature.add(correspondingAlternative);
              };
              _get_1.forEach(_function_4);
            }
          };
          adn.collectFeatureDecisions().forEach(_function_3);
        };
        IterableExtensions.<ActivationDiagramNode>forEach(orNode.getConsequences(), _function_2);
      };
      ListExtensions.<VBRuleFeature, VBRuleOrFeature>map(this.vbRuleFeatures.getChildren(), _function).forEach(_function_1);
      _xblockexpression = result;
    }
    return _xblockexpression;
  }

  /**
   * Remove all VB-rule features that are either dead (can never be activated) or unused (do not appear in any PC). Unused features may be generated by the simplification of presence conditions that contain root.
   */
  private void removeDeadOrUnusedFeatures() {
    final Function1<VBRuleFeature, Boolean> _function = (VBRuleFeature f) -> {
      return Boolean.valueOf(((f instanceof VBRuleOrFeature) || IterableExtensions.<Set<VBRuleFeature>>exists(this.resolvedPCs.values(), ((Function1<Set<VBRuleFeature>, Boolean>) (Set<VBRuleFeature> it) -> {
        return Boolean.valueOf(it.contains(f));
      }))));
    };
    final List<VBRuleFeature> unusedFeatures = IterableExtensions.<VBRuleFeature>toList(IterableExtensions.<VBRuleFeature>reject(this.collectAllFeatures(), _function));
    final HashMap<String, VBRuleFeature> allFeaturesIndex = new HashMap<String, VBRuleFeature>();
    final Consumer<VBRuleFeature> _function_1 = (VBRuleFeature it) -> {
      allFeaturesIndex.put(it.getName(), it);
    };
    this.collectAllFeatures().forEach(_function_1);
    final Function1<String, VBRuleFeature> _function_2 = (String fn) -> {
      return allFeaturesIndex.get(fn);
    };
    List<VBRuleFeature> _map = ListExtensions.<String, VBRuleFeature>map(SatSolver.calculateDeadFeatures(VBRuleFeatureConstraintGenerator.computeConstraintExpression(this)), _function_2);
    ArrayList<VBRuleFeature> _arrayList = new ArrayList<VBRuleFeature>(_map);
    this.deadFeatures = _arrayList;
    final Function1<VBRuleFeature, Boolean> _function_3 = (VBRuleFeature it) -> {
      final Function1<VBRuleFeature, Boolean> _function_4 = (VBRuleFeature f) -> {
        return Boolean.valueOf((this.deadFeatures.contains(f) || unusedFeatures.contains(f)));
      };
      return Boolean.valueOf(IterableExtensions.<VBRuleFeature>forall(it.getChildren(), _function_4));
    };
    final Iterable<VBRuleFeature> uselessOrFeatures = IterableExtensions.<VBRuleFeature>filter(this.vbRuleFeatures.getChildren(), _function_3);
    Iterable<VBRuleFeature> _plus = Iterables.<VBRuleFeature>concat(this.deadFeatures, unusedFeatures);
    List<VBRuleFeature> _list = IterableExtensions.<VBRuleFeature>toList(Iterables.<VBRuleFeature>concat(_plus, uselessOrFeatures));
    final HashSet<VBRuleFeature> featuresToCleanOut = new HashSet<VBRuleFeature>(_list);
    this.removeUselessFeatures(this.vbRuleFeatures, featuresToCleanOut);
    final Predicate<Pair<VBRuleFeature, VBRuleFeature>> _function_4 = (Pair<VBRuleFeature, VBRuleFeature> it) -> {
      return (featuresToCleanOut.contains(it.getKey()) || featuresToCleanOut.contains(it.getValue()));
    };
    this.featureExclusions.removeIf(_function_4);
    final HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> newOrFixings = new HashMap<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>();
    final Function2<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>, Boolean> _function_5 = (VBRuleOrFeature k, Map<VBRuleFeature, Set<VBRuleOrAlternative>> v) -> {
      boolean _contains = featuresToCleanOut.contains(k);
      return Boolean.valueOf((!_contains));
    };
    final Function1<Map<VBRuleFeature, Set<VBRuleOrAlternative>>, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> _function_6 = (Map<VBRuleFeature, Set<VBRuleOrAlternative>> it) -> {
      final Function2<VBRuleFeature, Set<VBRuleOrAlternative>, Boolean> _function_7 = (VBRuleFeature k2, Set<VBRuleOrAlternative> v2) -> {
        boolean _contains = featuresToCleanOut.contains(k2);
        return Boolean.valueOf((!_contains));
      };
      final Function1<Set<VBRuleOrAlternative>, Set<VBRuleOrAlternative>> _function_8 = (Set<VBRuleOrAlternative> it_1) -> {
        final Function1<VBRuleOrAlternative, Boolean> _function_9 = (VBRuleOrAlternative it_2) -> {
          return Boolean.valueOf(featuresToCleanOut.contains(it_2));
        };
        return IterableExtensions.<VBRuleOrAlternative>toSet(IterableExtensions.<VBRuleOrAlternative>reject(it_1, _function_9));
      };
      final Function2<VBRuleFeature, Set<VBRuleOrAlternative>, Boolean> _function_9 = (VBRuleFeature k2, Set<VBRuleOrAlternative> v2) -> {
        boolean _isEmpty = v2.isEmpty();
        return Boolean.valueOf((!_isEmpty));
      };
      return MapExtensions.<VBRuleFeature, Set<VBRuleOrAlternative>>filter(MapExtensions.<VBRuleFeature, Set<VBRuleOrAlternative>, Set<VBRuleOrAlternative>>mapValues(MapExtensions.<VBRuleFeature, Set<VBRuleOrAlternative>>filter(it, _function_7), _function_8), _function_9);
    };
    final Function2<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>, Boolean> _function_7 = (VBRuleOrFeature k, Map<VBRuleFeature, Set<VBRuleOrAlternative>> v) -> {
      boolean _isEmpty = v.isEmpty();
      return Boolean.valueOf((!_isEmpty));
    };
    newOrFixings.putAll(MapExtensions.<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>filter(MapExtensions.<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>mapValues(MapExtensions.<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>>filter(this.orFixings, _function_5), _function_6), _function_7));
    this.orFixings.clear();
    this.orFixings.putAll(newOrFixings);
    final HashMap<VBRuleFeature, Set<VBRuleOrFeature>> newResolvedOrImplications = new HashMap<VBRuleFeature, Set<VBRuleOrFeature>>();
    final Function2<VBRuleFeature, Set<VBRuleOrFeature>, Boolean> _function_8 = (VBRuleFeature key, Set<VBRuleOrFeature> value) -> {
      boolean _contains = featuresToCleanOut.contains(key);
      return Boolean.valueOf((!_contains));
    };
    final Function1<Set<VBRuleOrFeature>, Set<VBRuleOrFeature>> _function_9 = (Set<VBRuleOrFeature> it) -> {
      final Function1<VBRuleOrFeature, Boolean> _function_10 = (VBRuleOrFeature it_1) -> {
        return Boolean.valueOf(featuresToCleanOut.contains(it_1));
      };
      return IterableExtensions.<VBRuleOrFeature>toSet(IterableExtensions.<VBRuleOrFeature>reject(it, _function_10));
    };
    final Function2<VBRuleFeature, Set<VBRuleOrFeature>, Boolean> _function_10 = (VBRuleFeature k, Set<VBRuleOrFeature> v) -> {
      boolean _isEmpty = v.isEmpty();
      return Boolean.valueOf((!_isEmpty));
    };
    newResolvedOrImplications.putAll(MapExtensions.<VBRuleFeature, Set<VBRuleOrFeature>>filter(MapExtensions.<VBRuleFeature, Set<VBRuleOrFeature>, Set<VBRuleOrFeature>>mapValues(MapExtensions.<VBRuleFeature, Set<VBRuleOrFeature>>filter(this.resolvedOrImplications, _function_8), _function_9), _function_10));
    this.resolvedOrImplications.clear();
    this.resolvedOrImplications.putAll(newResolvedOrImplications);
    final HashMap<FeatureDecision, Set<VBRuleFeature>> newResolvedPCs = new HashMap<FeatureDecision, Set<VBRuleFeature>>();
    final Function1<Set<VBRuleFeature>, Set<VBRuleFeature>> _function_11 = (Set<VBRuleFeature> it) -> {
      final Function1<VBRuleFeature, Boolean> _function_12 = (VBRuleFeature it_1) -> {
        return Boolean.valueOf(featuresToCleanOut.contains(it_1));
      };
      return IterableExtensions.<VBRuleFeature>toSet(IterableExtensions.<VBRuleFeature>reject(it, _function_12));
    };
    newResolvedPCs.putAll(MapExtensions.<FeatureDecision, Set<VBRuleFeature>, Set<VBRuleFeature>>mapValues(this.resolvedPCs, _function_11));
    final Function2<FeatureDecision, Set<VBRuleFeature>, Boolean> _function_12 = (FeatureDecision fd, Set<VBRuleFeature> pcs) -> {
      return Boolean.valueOf(pcs.isEmpty());
    };
    this.subdiagramContents.removeAll(MapExtensions.<FeatureDecision, Set<VBRuleFeature>>filter(newResolvedPCs, _function_12).keySet());
    this.resolvedPCs.clear();
    final Function2<FeatureDecision, Set<VBRuleFeature>, Boolean> _function_13 = (FeatureDecision fd, Set<VBRuleFeature> pcs) -> {
      boolean _isEmpty = pcs.isEmpty();
      return Boolean.valueOf((!_isEmpty));
    };
    this.resolvedPCs.putAll(MapExtensions.<FeatureDecision, Set<VBRuleFeature>>filter(newResolvedPCs, _function_13));
  }

  private void removeUselessFeatures(final VBRuleFeature feature, final Set<VBRuleFeature> uselessFeatures) {
    final Predicate<VBRuleFeature> _function = (VBRuleFeature it) -> {
      return uselessFeatures.contains(it);
    };
    feature.getChildren().removeIf(_function);
    final Consumer<VBRuleFeature> _function_1 = (VBRuleFeature it) -> {
      this.removeUselessFeatures(it, uselessFeatures);
    };
    feature.getChildren().forEach(_function_1);
  }

  private Set<OrImplication> _visit(final OrNode or, final PresenceCondition pc, final FeatureDecision comingFrom) {
    boolean _contains = this.subdiagramContents.contains(or);
    if (_contains) {
      VBRuleOrFeature _featureFor = this.featureFor(or);
      FinalisedOrImplication _finalisedOrImplication = new FinalisedOrImplication(_featureFor);
      return Collections.<OrImplication>unmodifiableSet(CollectionLiterals.<OrImplication>newHashSet(_finalisedOrImplication));
    }
    this.subdiagramContents.add(or);
    final VBRuleOrFeature orFeature = this.createFeatures(or, comingFrom);
    final Procedure2<ActivationDiagramNode, Integer> _function = (ActivationDiagramNode c, Integer idx) -> {
      final VBRuleFeature orAlternativeFeature = orFeature.getChildren().get((idx).intValue());
      FeaturePresenceCondition _featurePresenceCondition = new FeaturePresenceCondition(orAlternativeFeature);
      final Set<OrImplication> followOnOrs = this.visit(c, _featurePresenceCondition, comingFrom);
      this.orImplications.put(orAlternativeFeature, followOnOrs);
    };
    IterableExtensions.<ActivationDiagramNode>forEach(or.getConsequences(), _function);
    FinalisedOrImplication _finalisedOrImplication_1 = new FinalisedOrImplication(orFeature);
    return Collections.<OrImplication>unmodifiableSet(CollectionLiterals.<OrImplication>newHashSet(_finalisedOrImplication_1));
  }

  private Set<OrImplication> _visit(final AndNode and, final PresenceCondition pc, final FeatureDecision comingFrom) {
    boolean _contains = this.subdiagramContents.contains(and);
    if (_contains) {
      ProxyOrImplication _proxyOrImplication = new ProxyOrImplication(and);
      return Collections.<OrImplication>unmodifiableSet(CollectionLiterals.<OrImplication>newHashSet(_proxyOrImplication));
    }
    this.subdiagramContents.add(and);
    final Function1<ActivationDiagramNode, Set<OrImplication>> _function = (ActivationDiagramNode it) -> {
      return this.visit(it, pc, comingFrom);
    };
    final Set<OrImplication> followOrInformation = IterableExtensions.<OrImplication>toSet(IterableExtensions.<ActivationDiagramNode, OrImplication>flatMap(and.getConsequences(), _function));
    this.followOrs.put(and, followOrInformation);
    return followOrInformation;
  }

  private Set<OrImplication> _visit(final FeatureDecision fd, final PresenceCondition pc, final FeatureDecision comingFrom) {
    Set<PresenceCondition> pcs = this.presenceConditions.get(fd);
    if ((pcs == null)) {
      HashSet<PresenceCondition> _hashSet = new HashSet<PresenceCondition>();
      pcs = _hashSet;
      this.presenceConditions.put(fd, pcs);
    }
    pcs.add(pc);
    boolean _contains = this.subdiagramContents.contains(fd);
    if (_contains) {
      ProxyOrImplication _proxyOrImplication = new ProxyOrImplication(fd);
      return Collections.<OrImplication>unmodifiableSet(CollectionLiterals.<OrImplication>newHashSet(_proxyOrImplication));
    }
    this.subdiagramContents.add(fd);
    Set<FeatureDecision> decisionsSoFar = this.featureDecisions.get(fd.getFeature());
    if ((decisionsSoFar == null)) {
      HashSet<FeatureDecision> _hashSet_1 = new HashSet<FeatureDecision>();
      decisionsSoFar = _hashSet_1;
      this.featureDecisions.put(fd.getFeature(), decisionsSoFar);
    }
    decisionsSoFar.add(fd);
    final ProxyPresenceCondition presenceCondition = new ProxyPresenceCondition(fd);
    final Function1<ActivationDiagramNode, Set<OrImplication>> _function = (ActivationDiagramNode it) -> {
      return this.visit(it, presenceCondition, fd);
    };
    final Set<OrImplication> followOrInformation = IterableExtensions.<OrImplication>toSet(IterableExtensions.<ActivationDiagramNode, OrImplication>flatMap(fd.getConsequences(), _function));
    this.followOrs.put(fd, followOrInformation);
    return followOrInformation;
  }

  /**
   * Create a fresh set of VB rule features for the given OrNode.
   */
  private VBRuleOrFeature createFeatures(final OrNode or, final FeatureDecision source) {
    VBRuleOrFeature _xblockexpression = null;
    {
      String _name = this.getName(source);
      VBRuleOrFeature _vBRuleOrFeature = new VBRuleOrFeature(_name, or);
      final Procedure1<VBRuleOrFeature> _function = (VBRuleOrFeature it) -> {
        final ArrayList<Integer> counter = new ArrayList<Integer>();
        counter.add(Integer.valueOf(0));
        List<VBRuleFeature> _children = it.getChildren();
        final Function1<ActivationDiagramNode, VBRuleOrAlternative> _function_1 = (ActivationDiagramNode it_1) -> {
          VBRuleOrAlternative _xblockexpression_1 = null;
          {
            Integer _head = IterableExtensions.<Integer>head(counter);
            final int ID = ((_head).intValue() + 1);
            counter.set(0, Integer.valueOf(ID));
            StringConcatenation _builder = new StringConcatenation();
            String _name_1 = this.getName(source);
            _builder.append(_name_1);
            _builder.append("_Alternative");
            _builder.append(ID);
            _builder.append("_");
            String _name_2 = this.getName(it_1);
            _builder.append(_name_2);
            _xblockexpression_1 = new VBRuleOrAlternative(_builder.toString(), or, ID);
          }
          return _xblockexpression_1;
        };
        List<VBRuleOrAlternative> _map = ListExtensions.<ActivationDiagramNode, VBRuleOrAlternative>map(or.getConsequences(), _function_1);
        Iterables.<VBRuleFeature>addAll(_children, _map);
      };
      final VBRuleOrFeature vbRuleOrFeature = ObjectExtensions.<VBRuleOrFeature>operator_doubleArrow(_vBRuleOrFeature, _function);
      List<VBRuleFeature> _children = this.vbRuleFeatures.getChildren();
      _children.add(vbRuleOrFeature);
      this.orFeatureMap.put(or, vbRuleOrFeature);
      _xblockexpression = vbRuleOrFeature;
    }
    return _xblockexpression;
  }

  private VBRuleOrFeature featureFor(final OrNode or) {
    return this.orFeatureMap.get(or);
  }

  private String _getName(final ActivationDiagramNode node) {
    return this.getName(IterableExtensions.<ActivationDiagramNode>head(node.getConsequences()));
  }

  private String _getName(final FeatureDecision fd) {
    String _sanitise = this.sanitise(fd.getFeature().getName());
    String _xifexpression = null;
    boolean _isActivate = fd.isActivate();
    if (_isActivate) {
      _xifexpression = "Act";
    } else {
      _xifexpression = "DeAct";
    }
    return (_sanitise + _xifexpression);
  }

  /**
   * Sanitise feature names so they can serve as variable identifiers in the formula given to the SAT solver
   */
  private String sanitise(final String featureName) {
    return featureName.replaceAll("\\W", "");
  }

  private Set<OrImplication> visit(final ActivationDiagramNode and, final PresenceCondition pc, final FeatureDecision comingFrom) {
    if (and instanceof AndNode) {
      return _visit((AndNode)and, pc, comingFrom);
    } else if (and instanceof FeatureDecision) {
      return _visit((FeatureDecision)and, pc, comingFrom);
    } else if (and instanceof OrNode) {
      return _visit((OrNode)and, pc, comingFrom);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(and, pc, comingFrom).toString());
    }
  }

  private String getName(final ActivationDiagramNode fd) {
    if (fd instanceof FeatureDecision) {
      return _getName((FeatureDecision)fd);
    } else if (fd != null) {
      return _getName(fd);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(fd).toString());
    }
  }

  @Pure
  public FeatureDecision getRootDecision() {
    return this.rootDecision;
  }

  @Pure
  Set<ActivationDiagramNode> getSubdiagramContents() {
    return this.subdiagramContents;
  }

  @Pure
  public VBRuleFeature getVbRuleFeatures() {
    return this.vbRuleFeatures;
  }

  @Pure
  public Set<Pair<VBRuleFeature, VBRuleFeature>> getFeatureExclusions() {
    return this.featureExclusions;
  }

  @Pure
  public Map<VBRuleOrFeature, Map<VBRuleFeature, Set<VBRuleOrAlternative>>> getOrFixings() {
    return this.orFixings;
  }

  @Pure
  public List<VBRuleFeature> getDeadFeatures() {
    return this.deadFeatures;
  }
}

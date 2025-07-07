package acapulco.rulesgeneration.activationdiagrams;

import acapulco.featuremodel.FeatureModelHelper;
import acapulco.model.Feature;
import acapulco.model.FeatureModel;
import acapulco.rulesgeneration.activationdiagrams.principles.PrinciplesUtil;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Main class for generating rules. This should be reused for all feature decisions for which rules are to be generated within one feature model.
 * 
 * Create a new instance, providing the feature model. Then invoke calculateSubdiagramFor for each feature decision for which to generate a
 * rule and read off the rule information from the sub diagram produced.
 */
@SuppressWarnings("all")
public class FeatureActivationDiagram {
  /**
   * The full diagram.
   */
  private final Set<ActivationDiagramNode> diagram = new HashSet<ActivationDiagramNode>();

  /**
   * Helper for analysing the feature model
   */
  private final FeatureModelHelper fmHelper;

  public FeatureActivationDiagram(final FeatureModel featureModel) {
    FeatureModelHelper _featureModelHelper = new FeatureModelHelper(featureModel);
    this.fmHelper = _featureModelHelper;
  }

  /**
   * For testing purposes
   */
  public Set<ActivationDiagramNode> getDiagramNodes() {
    return Collections.<ActivationDiagramNode>unmodifiableSet(this.diagram);
  }

  /**
   * Calculate the subdiagram for the given feature decision.
   * 
   * A sub-diagram contains all information needed for generating the VB rule for this feature decision.
   */
  public FeatureActivationSubDiagram calculateSubdiagramFor(final Feature f, final boolean activate) {
    final FeatureDecision featureDecision = this.addFeatureDecision(f, activate);
    return new FeatureActivationSubDiagram(featureDecision);
  }

  /**
   * Add the feature decision and its consequences to the diagram and return the corresponding feature-decision node.
   * 
   * Note that the feature decision may already have been added in a previous step (as a consequence of a different decision),
   * in which case we simply return that decision node.
   */
  private FeatureDecision addFeatureDecision(final Feature f, final boolean activate) {
    FeatureDecision _xblockexpression = null;
    {
      final FeatureDecision decisionNode = new FeatureDecision(f, activate);
      _xblockexpression = this.addFeatureDecision(decisionNode);
    }
    return _xblockexpression;
  }

  private FeatureDecision addFeatureDecision(final FeatureDecision decisionNode) {
    boolean _contains = this.diagram.contains(decisionNode);
    if (_contains) {
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(Objects.equal(it, decisionNode));
      };
      ActivationDiagramNode _findFirst = IterableExtensions.<ActivationDiagramNode>findFirst(this.diagram, _function);
      return ((FeatureDecision) _findFirst);
    }
    this.diagram.add(decisionNode);
    final Set<Set<Set<FeatureDecision>>> immediateConsequences = PrinciplesUtil.applyPrinciples(decisionNode, this.fmHelper);
    final Consumer<Set<Set<FeatureDecision>>> _function_1 = (Set<Set<FeatureDecision>> it) -> {
      this.addConsequencesTo(it, decisionNode);
    };
    immediateConsequences.forEach(_function_1);
    return decisionNode;
  }

  /**
   * Add consequences from one principle application
   */
  private boolean addConsequencesTo(final Set<Set<FeatureDecision>> consequencesSet, final FeatureDecision parent) {
    boolean _xifexpression = false;
    boolean _isEmpty = consequencesSet.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      boolean _xifexpression_1 = false;
      int _size = consequencesSet.size();
      boolean _greaterThan = (_size > 1);
      if (_greaterThan) {
        boolean _xblockexpression = false;
        {
          OrNode _orNode = new OrNode();
          final Procedure1<OrNode> _function = (OrNode it) -> {
            List<ActivationDiagramNode> _consequences = it.getConsequences();
            final Function1<Set<FeatureDecision>, ActivationDiagramNode> _function_1 = (Set<FeatureDecision> it_1) -> {
              return this.toActivationDiagramNode(it_1);
            };
            Iterable<ActivationDiagramNode> _map = IterableExtensions.<Set<FeatureDecision>, ActivationDiagramNode>map(consequencesSet, _function_1);
            Iterables.<ActivationDiagramNode>addAll(_consequences, _map);
          };
          final OrNode orNode = ObjectExtensions.<OrNode>operator_doubleArrow(_orNode, _function);
          this.diagram.add(orNode);
          List<ActivationDiagramNode> _consequences = parent.getConsequences();
          _xblockexpression = _consequences.add(orNode);
        }
        _xifexpression_1 = _xblockexpression;
      } else {
        List<ActivationDiagramNode> _consequences = parent.getConsequences();
        ActivationDiagramNode _activationDiagramNode = this.toActivationDiagramNode(IterableExtensions.<Set<FeatureDecision>>head(consequencesSet));
        _xifexpression_1 = _consequences.add(_activationDiagramNode);
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  private ActivationDiagramNode toActivationDiagramNode(final Set<FeatureDecision> decisions) {
    ActivationDiagramNode _xifexpression = null;
    int _size = decisions.size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      AndNode _xblockexpression = null;
      {
        AndNode _andNode = new AndNode();
        final Procedure1<AndNode> _function = (AndNode it) -> {
          List<ActivationDiagramNode> _consequences = it.getConsequences();
          final Function1<FeatureDecision, FeatureDecision> _function_1 = (FeatureDecision it_1) -> {
            return this.addFeatureDecision(it_1);
          };
          Iterable<FeatureDecision> _map = IterableExtensions.<FeatureDecision, FeatureDecision>map(decisions, _function_1);
          Iterables.<ActivationDiagramNode>addAll(_consequences, _map);
        };
        final AndNode andNode = ObjectExtensions.<AndNode>operator_doubleArrow(_andNode, _function);
        this.diagram.add(andNode);
        _xblockexpression = andNode;
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = this.addFeatureDecision(IterableExtensions.<FeatureDecision>head(decisions));
    }
    return _xifexpression;
  }
}

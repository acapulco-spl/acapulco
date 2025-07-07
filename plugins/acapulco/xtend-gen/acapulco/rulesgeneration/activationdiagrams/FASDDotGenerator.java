package acapulco.rulesgeneration.activationdiagrams;

import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * Generates a dot file from a feature-activation sub-diagram so that it can be rendered with GraphViz for debugging and publication purposes.
 */
@SuppressWarnings("all")
public class FASDDotGenerator {
  private final FeatureActivationSubDiagram fasd;

  private final boolean showPCs;

  /**
   * If not null, render only the direct consequences of the decisions included.
   */
  private final Set<FeatureDecision> startDecisions;

  private final Set<ActivationDiagramNode> nodesToRender;

  public FASDDotGenerator(final FeatureActivationSubDiagram fasd, final boolean showPCs) {
    this(fasd, showPCs, null);
  }

  public FASDDotGenerator(final FeatureActivationSubDiagram fasd, final boolean showPCs, final Set<FeatureDecision> startDecisions) {
    this.fasd = fasd;
    this.showPCs = showPCs;
    this.startDecisions = startDecisions;
    if ((startDecisions == null)) {
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(this.notInFASD(it));
      };
      this.nodesToRender = IterableExtensions.<ActivationDiagramNode>toSet(IterableExtensions.<ActivationDiagramNode>reject(fasd.getSubdiagramContents(), _function));
    } else {
      final Function1<ActivationDiagramNode, Boolean> _function_1 = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(this.notInFASD(it));
      };
      this.nodesToRender = IterableExtensions.<ActivationDiagramNode>toSet(IterableExtensions.<ActivationDiagramNode>reject(this.collectDirectConsequences(startDecisions), _function_1));
    }
  }

  public String render() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("strict digraph \"");
    FeatureDecision _rootDecision = this.fasd.getRootDecision();
    _builder.append(_rootDecision);
    _builder.append("\" {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _renderNodes = this.renderNodes();
    _builder.append(_renderNodes, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    String _renderEdges = this.renderEdges();
    _builder.append(_renderEdges, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }

  private HashSet<ActivationDiagramNode> collectDirectConsequences(final Set<FeatureDecision> _decisions) {
    HashSet<ActivationDiagramNode> _xblockexpression = null;
    {
      final HashSet<ActivationDiagramNode> result = new HashSet<ActivationDiagramNode>();
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(_decisions.contains(it));
      };
      final Iterable<ActivationDiagramNode> decisions = IterableExtensions.<ActivationDiagramNode>filter(this.fasd.getSubdiagramContents(), _function);
      Iterables.<ActivationDiagramNode>addAll(result, decisions);
      final Set<ActivationDiagramNode> visited = new HashSet<ActivationDiagramNode>();
      Iterables.<ActivationDiagramNode>addAll(visited, decisions);
      final Function1<ActivationDiagramNode, List<ActivationDiagramNode>> _function_1 = (ActivationDiagramNode it) -> {
        return it.getConsequences();
      };
      final Function1<ActivationDiagramNode, Iterable<ActivationDiagramNode>> _function_2 = (ActivationDiagramNode it) -> {
        return this.collectConsequencesToNextOr(it, visited, false);
      };
      Iterable<ActivationDiagramNode> _flatMap = IterableExtensions.<ActivationDiagramNode, ActivationDiagramNode>flatMap(IterableExtensions.<ActivationDiagramNode>toSet(IterableExtensions.<ActivationDiagramNode, ActivationDiagramNode>flatMap(decisions, _function_1)), _function_2);
      Iterables.<ActivationDiagramNode>addAll(result, _flatMap);
      _xblockexpression = result;
    }
    return _xblockexpression;
  }

  protected Iterable<ActivationDiagramNode> _collectConsequencesToNextOr(final FeatureDecision fd, final Set<ActivationDiagramNode> visited, final boolean stopAtOr) {
    Iterable<ActivationDiagramNode> _xblockexpression = null;
    {
      boolean _contains = visited.contains(fd);
      if (_contains) {
        return CollectionLiterals.<ActivationDiagramNode>emptySet();
      }
      visited.add(fd);
      final Function1<ActivationDiagramNode, Iterable<ActivationDiagramNode>> _function = (ActivationDiagramNode it) -> {
        return this.collectConsequencesToNextOr(it, visited, true);
      };
      Iterable<ActivationDiagramNode> _flatMap = IterableExtensions.<ActivationDiagramNode, ActivationDiagramNode>flatMap(fd.getConsequences(), _function);
      _xblockexpression = Iterables.<ActivationDiagramNode>concat(Collections.<FeatureDecision>unmodifiableSet(CollectionLiterals.<FeatureDecision>newHashSet(fd)), _flatMap);
    }
    return _xblockexpression;
  }

  protected Iterable<ActivationDiagramNode> _collectConsequencesToNextOr(final OrNode or, final Set<ActivationDiagramNode> visited, final boolean stopAtOr) {
    Iterable<ActivationDiagramNode> _xblockexpression = null;
    {
      boolean _contains = visited.contains(or);
      if (_contains) {
        return CollectionLiterals.<ActivationDiagramNode>emptySet();
      }
      visited.add(or);
      Iterable<ActivationDiagramNode> _xifexpression = null;
      if (stopAtOr) {
        _xifexpression = CollectionLiterals.<ActivationDiagramNode>emptySet();
      } else {
        final Function1<ActivationDiagramNode, Iterable<ActivationDiagramNode>> _function = (ActivationDiagramNode it) -> {
          return this.collectConsequencesToNextOr(it, visited, true);
        };
        _xifexpression = IterableExtensions.<ActivationDiagramNode, ActivationDiagramNode>flatMap(or.getConsequences(), _function);
      }
      _xblockexpression = Iterables.<ActivationDiagramNode>concat(Collections.<OrNode>unmodifiableSet(CollectionLiterals.<OrNode>newHashSet(or)), _xifexpression);
    }
    return _xblockexpression;
  }

  protected Iterable<ActivationDiagramNode> _collectConsequencesToNextOr(final AndNode and, final Set<ActivationDiagramNode> visited, final boolean stopAtOr) {
    Iterable<ActivationDiagramNode> _xblockexpression = null;
    {
      boolean _contains = visited.contains(and);
      if (_contains) {
        return CollectionLiterals.<ActivationDiagramNode>emptySet();
      }
      visited.add(and);
      final Function1<ActivationDiagramNode, Iterable<ActivationDiagramNode>> _function = (ActivationDiagramNode it) -> {
        return this.collectConsequencesToNextOr(it, visited, true);
      };
      Iterable<ActivationDiagramNode> _flatMap = IterableExtensions.<ActivationDiagramNode, ActivationDiagramNode>flatMap(and.getConsequences(), _function);
      _xblockexpression = Iterables.<ActivationDiagramNode>concat(Collections.<AndNode>unmodifiableSet(CollectionLiterals.<AndNode>newHashSet(and)), _flatMap);
    }
    return _xblockexpression;
  }

  private String renderNodes() {
    final Function1<ActivationDiagramNode, String> _function = (ActivationDiagramNode it) -> {
      return this.renderNode(it);
    };
    return IterableExtensions.join(IterableExtensions.<ActivationDiagramNode, String>map(this.nodesToRender, _function), "\n");
  }

  private boolean needToRender(final ActivationDiagramNode node) {
    return ((!this.notInFASD(node)) && ((this.startDecisions == null) || this.nodesToRender.contains(node)));
  }

  private boolean _notInFASD(final ActivationDiagramNode node) {
    return false;
  }

  private boolean _notInFASD(final FeatureDecision node) {
    Set<VBRuleFeature> _get = this.fasd.getPresenceConditions().get(node);
    return (_get == null);
  }

  private boolean _notInFASD(final AndNode node) {
    return false;
  }

  private boolean _notInFASD(final OrNode node) {
    final Function1<VBRuleOrFeature, Boolean> _function = (VBRuleOrFeature it) -> {
      OrNode _orNode = it.getOrNode();
      return Boolean.valueOf((_orNode == node));
    };
    VBRuleOrFeature _findFirst = IterableExtensions.<VBRuleOrFeature>findFirst(Iterables.<VBRuleOrFeature>filter(this.fasd.getVbRuleFeatures().getChildren(), VBRuleOrFeature.class), _function);
    return (_findFirst == null);
  }

  private int andNodeIndex = 0;

  private int orNodeIndex = 0;

  private final HashMap<AndNode, Integer> andNodeRegistry = new HashMap<AndNode, Integer>();

  private final HashMap<OrNode, Integer> orNodeRegistry = new HashMap<OrNode, Integer>();

  private String _renderNode(final AndNode andNode) {
    String _xblockexpression = null;
    {
      final int ID = this.andNodeIndex++;
      this.andNodeRegistry.put(andNode, Integer.valueOf(ID));
      StringConcatenation _builder = new StringConcatenation();
      String _nodeID = this.nodeID(andNode);
      _builder.append(_nodeID);
      _builder.append(" [shape = rectangle, label = <AND<SUB>");
      _builder.append(ID);
      _builder.append("</SUB>>]");
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }

  private String _renderNode(final OrNode orNode) {
    String _xblockexpression = null;
    {
      final int ID = this.orNodeIndex++;
      this.orNodeRegistry.put(orNode, Integer.valueOf(ID));
      StringConcatenation _builder = new StringConcatenation();
      String _nodeID = this.nodeID(orNode);
      _builder.append(_nodeID);
      _builder.append(" [shape = diamond, label = <OR<SUB>");
      _builder.append(ID);
      _builder.append("</SUB>>]");
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }

  private String _renderNode(final FeatureDecision decision) {
    String _xifexpression = null;
    if ((this.startDecisions == null)) {
      String _xifexpression_1 = null;
      FeatureDecision _rootDecision = this.fasd.getRootDecision();
      boolean _tripleEquals = (_rootDecision == decision);
      if (_tripleEquals) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("subgraph { rank = source");
        _builder.newLine();
        _builder.append("\t");
        String _nodeID = this.nodeID(decision);
        _builder.append(_nodeID, "\t");
        _builder.append(" [style = filled, fillcolor = lightblue, label = ");
        CharSequence _renderLabel = this.renderLabel(decision);
        _builder.append(_renderLabel, "\t");
        _builder.append("]");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _builder.newLine();
        _xifexpression_1 = _builder.toString();
      } else {
        StringConcatenation _builder_1 = new StringConcatenation();
        String _nodeID_1 = this.nodeID(decision);
        _builder_1.append(_nodeID_1);
        _builder_1.append(" [style = filled, fillcolor = lightgrey, label = ");
        CharSequence _renderLabel_1 = this.renderLabel(decision);
        _builder_1.append(_renderLabel_1);
        _builder_1.append("]");
        _builder_1.newLineIfNotEmpty();
        _xifexpression_1 = _builder_1.toString();
      }
      _xifexpression = _xifexpression_1;
    } else {
      String _xifexpression_2 = null;
      boolean _contains = this.startDecisions.contains(decision);
      if (_contains) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("subgraph { rank = source");
        _builder_2.newLine();
        _builder_2.append("\t");
        String _nodeID_2 = this.nodeID(decision);
        _builder_2.append(_nodeID_2, "\t");
        _builder_2.append(" [style = filled, fillcolor = lightblue, label = ");
        CharSequence _renderLabel_2 = this.renderLabel(decision);
        _builder_2.append(_renderLabel_2, "\t");
        _builder_2.append("]");
        _builder_2.newLineIfNotEmpty();
        _builder_2.append("}");
        _builder_2.newLine();
        _xifexpression_2 = _builder_2.toString();
      } else {
        StringConcatenation _builder_3 = new StringConcatenation();
        String _nodeID_3 = this.nodeID(decision);
        _builder_3.append(_nodeID_3);
        _builder_3.append(" [style = filled, fillcolor = lightgrey, label = ");
        CharSequence _renderLabel_3 = this.renderLabel(decision);
        _builder_3.append(_renderLabel_3);
        _builder_3.append("]");
        _builder_3.newLineIfNotEmpty();
        _xifexpression_2 = _builder_3.toString();
      }
      _xifexpression = _xifexpression_2;
    }
    return _xifexpression;
  }

  private CharSequence renderLabel(final FeatureDecision fd) {
    CharSequence _xifexpression = null;
    if ((!this.showPCs)) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("\"");
      String _string = fd.toString();
      _builder.append(_string);
      _builder.append("\"");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("<<TABLE BORDER=\"0\"><TR><TD>");
      _builder_1.append(fd);
      _builder_1.append("</TD></TR><TR><TD>");
      final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
        return it.getName();
      };
      String _join = IterableExtensions.join(IterableExtensions.<VBRuleFeature, String>map(this.fasd.getPresenceConditions().get(fd), _function), ",<BR/>");
      _builder_1.append(_join);
      _builder_1.append("</TD></TR></TABLE>>");
      _builder_1.newLineIfNotEmpty();
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }

  private String renderEdges() {
    String _xblockexpression = null;
    {
      final HashSet<ActivationDiagramNode> visited = new HashSet<ActivationDiagramNode>();
      String _xifexpression = null;
      if ((this.startDecisions == null)) {
        _xifexpression = this.recursivelyRenderConsequenceEdges(this.fasd.getRootDecision(), visited);
      } else {
        final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
          return Boolean.valueOf(this.startDecisions.contains(it));
        };
        final Function1<ActivationDiagramNode, String> _function_1 = (ActivationDiagramNode it) -> {
          return this.recursivelyRenderConsequenceEdges(it, visited);
        };
        _xifexpression = IterableExtensions.join(IterableExtensions.<ActivationDiagramNode, String>map(IterableExtensions.<ActivationDiagramNode>filter(this.fasd.getSubdiagramContents(), _function), _function_1), "\n");
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private String recursivelyRenderConsequenceEdges(final ActivationDiagramNode node, final Set<ActivationDiagramNode> visited) {
    String _xblockexpression = null;
    {
      if ((visited.contains(node) || (!this.needToRender(node)))) {
        return "";
      }
      visited.add(node);
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(this.needToRender(it));
      };
      final Iterable<ActivationDiagramNode> realOutgoingEdges = IterableExtensions.<ActivationDiagramNode>filter(node.getConsequences(), _function);
      final Function1<ActivationDiagramNode, String> _function_1 = (ActivationDiagramNode it) -> {
        return this.renderEdge(node, it, visited);
      };
      _xblockexpression = IterableExtensions.join(IterableExtensions.<ActivationDiagramNode, String>map(realOutgoingEdges, _function_1), "\n");
    }
    return _xblockexpression;
  }

  private String _renderEdge(final ActivationDiagramNode from, final ActivationDiagramNode to, final Set<ActivationDiagramNode> visited) {
    String _xifexpression = null;
    if ((from != to)) {
      _xifexpression = this.defaultRenderEdge(from, to, visited);
    } else {
      _xifexpression = "";
    }
    return _xifexpression;
  }

  private String _renderEdge(final ActivationDiagramNode from, final AndNode to, final Set<ActivationDiagramNode> visited) {
    String _xifexpression = null;
    if ((from instanceof FeatureDecision)) {
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(this.needToRender(it));
      };
      final Function1<ActivationDiagramNode, String> _function_1 = (ActivationDiagramNode it) -> {
        return this.renderEdge(from, it, visited);
      };
      _xifexpression = IterableExtensions.join(IterableExtensions.<ActivationDiagramNode, String>map(IterableExtensions.<ActivationDiagramNode>filter(to.getConsequences(), _function), _function_1), "\n");
    } else {
      _xifexpression = this.defaultRenderEdge(from, to, visited);
    }
    return _xifexpression;
  }

  private String _renderEdge(final ActivationDiagramNode from, final OrNode to, final Set<ActivationDiagramNode> visited) {
    String _xblockexpression = null;
    {
      final Function1<ActivationDiagramNode, Boolean> _function = (ActivationDiagramNode it) -> {
        return Boolean.valueOf(this.needToRender(it));
      };
      final Iterable<ActivationDiagramNode> toConsequences = IterableExtensions.<ActivationDiagramNode>filter(to.getConsequences(), _function);
      String _xifexpression = null;
      int _size = IterableExtensions.size(toConsequences);
      boolean _tripleEquals = (_size == 1);
      if (_tripleEquals) {
        _xifexpression = this.renderEdge(from, IterableExtensions.<ActivationDiagramNode>head(toConsequences), visited);
      } else {
        String _xifexpression_1 = null;
        int _size_1 = IterableExtensions.size(toConsequences);
        boolean _greaterThan = (_size_1 > 1);
        if (_greaterThan) {
          _xifexpression_1 = this.defaultRenderEdge(from, to, visited);
        } else {
          _xifexpression_1 = "";
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }

  private String defaultRenderEdge(final ActivationDiagramNode from, final ActivationDiagramNode to, final Set<ActivationDiagramNode> visited) {
    StringConcatenation _builder = new StringConcatenation();
    String _nodeID = this.nodeID(from);
    _builder.append(_nodeID);
    _builder.append(" -> ");
    String _nodeID_1 = this.nodeID(to);
    _builder.append(_nodeID_1);
    _builder.newLineIfNotEmpty();
    String _recursivelyRenderConsequenceEdges = this.recursivelyRenderConsequenceEdges(to, visited);
    _builder.append(_recursivelyRenderConsequenceEdges);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }

  private String _nodeID(final AndNode andNode) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"AND_");
    Integer _get = this.andNodeRegistry.get(andNode);
    _builder.append(_get);
    _builder.append("\"");
    return _builder.toString();
  }

  private String _nodeID(final OrNode orNode) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"OR_");
    Integer _get = this.orNodeRegistry.get(orNode);
    _builder.append(_get);
    _builder.append("\"");
    return _builder.toString();
  }

  private String _nodeID(final FeatureDecision fd) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"");
    _builder.append(fd);
    _builder.append("\"");
    return _builder.toString();
  }

  public Iterable<ActivationDiagramNode> collectConsequencesToNextOr(final ActivationDiagramNode and, final Set<ActivationDiagramNode> visited, final boolean stopAtOr) {
    if (and instanceof AndNode) {
      return _collectConsequencesToNextOr((AndNode)and, visited, stopAtOr);
    } else if (and instanceof FeatureDecision) {
      return _collectConsequencesToNextOr((FeatureDecision)and, visited, stopAtOr);
    } else if (and instanceof OrNode) {
      return _collectConsequencesToNextOr((OrNode)and, visited, stopAtOr);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(and, visited, stopAtOr).toString());
    }
  }

  private boolean notInFASD(final ActivationDiagramNode node) {
    if (node instanceof AndNode) {
      return _notInFASD((AndNode)node);
    } else if (node instanceof FeatureDecision) {
      return _notInFASD((FeatureDecision)node);
    } else if (node instanceof OrNode) {
      return _notInFASD((OrNode)node);
    } else if (node != null) {
      return _notInFASD(node);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(node).toString());
    }
  }

  private String renderNode(final ActivationDiagramNode andNode) {
    if (andNode instanceof AndNode) {
      return _renderNode((AndNode)andNode);
    } else if (andNode instanceof FeatureDecision) {
      return _renderNode((FeatureDecision)andNode);
    } else if (andNode instanceof OrNode) {
      return _renderNode((OrNode)andNode);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(andNode).toString());
    }
  }

  private String renderEdge(final ActivationDiagramNode from, final ActivationDiagramNode to, final Set<ActivationDiagramNode> visited) {
    if (to instanceof AndNode) {
      return _renderEdge(from, (AndNode)to, visited);
    } else if (to instanceof OrNode) {
      return _renderEdge(from, (OrNode)to, visited);
    } else if (to != null) {
      return _renderEdge(from, to, visited);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(from, to, visited).toString());
    }
  }

  private String nodeID(final ActivationDiagramNode andNode) {
    if (andNode instanceof AndNode) {
      return _nodeID((AndNode)andNode);
    } else if (andNode instanceof FeatureDecision) {
      return _nodeID((FeatureDecision)andNode);
    } else if (andNode instanceof OrNode) {
      return _nodeID((OrNode)andNode);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(andNode).toString());
    }
  }
}

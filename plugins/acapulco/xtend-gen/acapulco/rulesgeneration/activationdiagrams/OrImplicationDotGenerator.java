package acapulco.rulesgeneration.activationdiagrams;

import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrAlternative;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleOrFeature;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class OrImplicationDotGenerator {
  private final OrImplicationGraph orImplications;

  private final FeatureActivationSubDiagram fasd;

  public OrImplicationDotGenerator(final FeatureActivationSubDiagram fasd) {
    this.fasd = fasd;
    OrImplicationGraph _orImplicationGraph = new OrImplicationGraph(fasd);
    this.orImplications = _orImplicationGraph;
  }

  public String render() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("strict digraph \"");
    FeatureDecision _rootDecision = this.fasd.getRootDecision();
    _builder.append(_rootDecision);
    _builder.append("\" { packmode=array_c1;");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    CharSequence _renderNodes = this.renderNodes();
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

  private CharSequence renderNodes() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("subgraph rootGraph { rank = source;");
    _builder.newLine();
    _builder.append("\t");
    CharSequence _renderNode = this.renderNode(this.fasd.getVbRuleFeatures());
    _builder.append(_renderNode, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    final Function1<VBRuleFeature, Boolean> _function = (VBRuleFeature it) -> {
      VBRuleFeature _vbRuleFeatures = this.fasd.getVbRuleFeatures();
      return Boolean.valueOf((it == _vbRuleFeatures));
    };
    final Function1<VBRuleFeature, CharSequence> _function_1 = (VBRuleFeature it) -> {
      return this.renderNode(it);
    };
    String _join = IterableExtensions.join(IterableExtensions.<VBRuleFeature, CharSequence>map(IterableExtensions.<VBRuleFeature>reject(this.orImplications.getNodes(), _function), _function_1), "\n");
    _builder.append(_join);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  private String renderEdges() {
    final Function1<Map.Entry<VBRuleFeature, List<? extends VBRuleFeature>>, List<CharSequence>> _function = (Map.Entry<VBRuleFeature, List<? extends VBRuleFeature>> it) -> {
      return this.doRenderEdges(it);
    };
    return IterableExtensions.join(IterableExtensions.<Map.Entry<VBRuleFeature, List<? extends VBRuleFeature>>, CharSequence>flatMap(this.orImplications.getEdges().entrySet(), _function), "\n");
  }

  private CharSequence _renderNode(final VBRuleFeature node) {
    return this.renderAlternativeNode(node);
  }

  private CharSequence _renderNode(final VBRuleOrFeature node) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = node.getName();
    _builder.append(_name);
    _builder.append(" [shape=diamond]");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  private CharSequence _renderNode(final VBRuleOrAlternative node) {
    return this.renderAlternativeNode(node);
  }

  private CharSequence renderAlternativeNode(final VBRuleFeature node) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = node.getName();
    _builder.append(_name);
    _builder.append(" [shape=rectangle]");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  private List<CharSequence> doRenderEdges(final Map.Entry<VBRuleFeature, List<? extends VBRuleFeature>> edgeSet) {
    final Function1<VBRuleFeature, CharSequence> _function = (VBRuleFeature it) -> {
      return this.renderEdge(edgeSet.getKey(), it);
    };
    return ListExtensions.map(edgeSet.getValue(), _function);
  }

  private CharSequence renderEdge(final VBRuleFeature from, final VBRuleFeature to) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = from.getName();
    _builder.append(_name);
    _builder.append(" -> ");
    String _name_1 = to.getName();
    _builder.append(_name_1);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  private CharSequence renderNode(final VBRuleFeature node) {
    if (node instanceof VBRuleOrAlternative) {
      return _renderNode((VBRuleOrAlternative)node);
    } else if (node instanceof VBRuleOrFeature) {
      return _renderNode((VBRuleOrFeature)node);
    } else if (node != null) {
      return _renderNode(node);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(node).toString());
    }
  }
}

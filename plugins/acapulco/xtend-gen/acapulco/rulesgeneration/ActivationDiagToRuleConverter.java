package acapulco.rulesgeneration;

import acapulco.model.Feature;
import acapulco.rulesgeneration.activationdiagrams.FeatureActivationSubDiagram;
import acapulco.rulesgeneration.activationdiagrams.FeatureDecision;
import acapulco.rulesgeneration.activationdiagrams.VBRuleFeatureConstraintGenerator;
import acapulco.rulesgeneration.activationdiagrams.vbrulefeatures.VBRuleFeature;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class ActivationDiagToRuleConverter {
  public static final String KEY_PRESENCE_CONDITION = "presenceCondition";

  public static final String KEY_FEATURE_CONSTRAINT = "featureModel";

  public static final String KEY_INJ_MATCHING = "injectiveMatchingPresenceCondition";

  public static final String KEY_FEATURES = "features";

  public static Rule convert(final FeatureActivationSubDiagram activationDiagram, final Map<Feature, EClass> features2Classes) {
    Rule _createRule = HenshinFactory.eINSTANCE.createRule();
    final Procedure1<Rule> _function = (Rule r) -> {
      final FeatureDecision rootDec = activationDiagram.getRootDecision();
      StringConcatenation _builder = new StringConcatenation();
      String _xifexpression = null;
      boolean _isActivate = rootDec.isActivate();
      if (_isActivate) {
        _xifexpression = "Act";
      } else {
        _xifexpression = "De";
      }
      _builder.append(_xifexpression);
      _builder.append("_");
      String _name = rootDec.getFeature().getName();
      _builder.append(_name);
      r.setName(_builder.toString());
      final Consumer<Map.Entry<FeatureDecision, Set<VBRuleFeature>>> _function_1 = (Map.Entry<FeatureDecision, Set<VBRuleFeature>> it) -> {
        FeatureDecision _key = it.getKey();
        final boolean isRoot = (rootDec == _key);
        final EClass type = features2Classes.get(it.getKey().getFeature());
        final boolean activate = it.getKey().isActivate();
        ActivationDiagToRuleConverter.addPreserveNode(r, type, activate, it.getValue(), isRoot);
      };
      activationDiagram.getPresenceConditions().entrySet().forEach(_function_1);
      ActivationDiagToRuleConverter.addAnnotation(r, ActivationDiagToRuleConverter.KEY_FEATURE_CONSTRAINT, VBRuleFeatureConstraintGenerator.computeConstraintExpression(activationDiagram));
      ActivationDiagToRuleConverter.addAnnotation(r, ActivationDiagToRuleConverter.KEY_INJ_MATCHING, "false");
      final Function1<VBRuleFeature, String> _function_2 = (VBRuleFeature it) -> {
        return it.getName();
      };
      ActivationDiagToRuleConverter.addAnnotation(r, ActivationDiagToRuleConverter.KEY_FEATURES, IterableExtensions.join(IterableExtensions.<VBRuleFeature, String>map(activationDiagram.collectAllFeatures(), _function_2), ","));
    };
    return ObjectExtensions.<Rule>operator_doubleArrow(_createRule, _function);
  }

  private static void addPreserveNode(final Rule rule, final EClass type, final boolean activate, final Set<VBRuleFeature> pcComponents, final boolean isRoot) {
    if ((type == null)) {
      InputOutput.<String>println("type was null");
    }
    final Node lhsNode = HenshinFactory.eINSTANCE.createNode(rule.getLhs(), type, "");
    final Node rhsNode = HenshinFactory.eINSTANCE.createNode(rule.getRhs(), type, "");
    rule.getMappings().add(lhsNode, rhsNode);
    final EAttribute attributeType = IterableExtensions.<EAttribute>head(type.getEAllAttributes());
    if (isRoot) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append((!activate));
      HenshinFactory.eINSTANCE.createAttribute(lhsNode, attributeType, _builder.toString());
    }
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append(activate);
    HenshinFactory.eINSTANCE.createAttribute(rhsNode, attributeType, _builder_1.toString());
    final Function1<VBRuleFeature, String> _function = (VBRuleFeature it) -> {
      return it.getName();
    };
    final String pc = IterableExtensions.join(IterableExtensions.<VBRuleFeature, String>map(pcComponents, _function), "|");
    ActivationDiagToRuleConverter.addAnnotation(lhsNode, ActivationDiagToRuleConverter.KEY_PRESENCE_CONDITION, pc);
    ActivationDiagToRuleConverter.addAnnotation(rhsNode, ActivationDiagToRuleConverter.KEY_PRESENCE_CONDITION, pc);
  }

  private static void addAnnotation(final ModelElement elem, final String aKey, final String aValue) {
    EList<Annotation> _annotations = elem.getAnnotations();
    Annotation _createAnnotation = HenshinFactory.eINSTANCE.createAnnotation();
    final Procedure1<Annotation> _function = (Annotation it) -> {
      it.setKey(aKey);
      it.setValue(aValue);
    };
    Annotation _doubleArrow = ObjectExtensions.<Annotation>operator_doubleArrow(_createAnnotation, _function);
    _annotations.add(_doubleArrow);
  }
}

package acapulco.rulesgeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.henshin.model.Rule;

import acapulco.featureide.utils.FeatureIDE2FM;
import acapulco.featuremodel.FeatureModelHelper;
import acapulco.featuremodel.configuration.FMConfigurationMetamodelGenerator;
import acapulco.model.Feature;
import acapulco.model.FeatureModel;
import acapulco.rulesgeneration.activationdiagrams.FeatureActivationDiagram;
import acapulco.rulesgeneration.activationdiagrams.FeatureActivationSubDiagram;
import acapulco.utils.emf.HenshinConfigurator;
import acapulco.utils.emf.HenshinFileWriter;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class CpcoGenerator {
	public static void generatorCPCOs(IFeatureModel fmFeatureIDE, String fmName, String outpath, IProgressMonitor monitor) {
		
		FeatureModel fm = FeatureIDE2FM.create(fmFeatureIDE);
		
		outpath += "/acapulco/" + fmName;
		FeatureActivationDiagram ad = new FeatureActivationDiagram(fm); // FM-specific
		FMConfigurationMetamodelGenerator metamodelGen = new FMConfigurationMetamodelGenerator(fm, fmName, fmName,
				"http://"+fmName);
		
		metamodelGen.generateMetamodel();
		System.out.println(outpath);
		metamodelGen.saveMetamodel(outpath + "/" + fmName+".dimacs.ecore");
		metamodelGen.saveMetamodel(outpath + "/" + fmName+".dimacs.cpcos/"+fmName+".ecore");
		
		FeatureModelHelper helper = new FeatureModelHelper(fm);
		List<Feature> trueOptional = new ArrayList<>(helper.getFeatures());
		trueOptional.removeAll(helper.getAlwaysActiveFeatures());
		
		// Remove dead features:
		FeatureModelAnalyzer operator = new FeatureModelAnalyzer(fmFeatureIDE);
		List<String> deadFeatures = operator.getDeadFeatures(new NullMonitor<LiteralSet>()).stream().map(f -> f.getName()).collect(Collectors.toList());
		
		System.out.println("+++++++++++++++++++++++++++++ Dead features ***+++++++++++++++++++++++++++++++++++");
		for (String f : deadFeatures) {
			System.out.println("Dead feature: " + f);
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		int i = 1;
		for (Feature f : trueOptional) {
			if (monitor.isCanceled()) {
				return;
			}
			if (!deadFeatures.contains(f.getName())) {
				monitor.subTask("Generating CPCOs " + i + "/" + trueOptional.size() + ": Act CPCO for feature: " + f.getName());	
				FeatureActivationSubDiagram sd = ad.calculateSubdiagramFor(f, true); // CPCO-specific
				Rule rule = ActivationDiagToRuleConverter.convert(sd, metamodelGen.geteClasses());
				rule = HenshinConfigurator.removeVariability(rule);
				HenshinFileWriter.writeModuleToPath(Collections.singletonList(rule), outpath + "/" + fmName+".dimacs.cpcos/"+rule.getName()+".hen");
				
				monitor.subTask("Generating CPCOs " + i + "/" + trueOptional.size() + ": De  CPCO for feature: " + f.getName());
				sd = ad.calculateSubdiagramFor(f, false); // CPCO-specific
				rule = ActivationDiagToRuleConverter.convert(sd, metamodelGen.geteClasses());
				rule = HenshinConfigurator.removeVariability(rule);
				HenshinFileWriter.writeModuleToPath(Collections.singletonList(rule), outpath + "/" + fmName+".dimacs.cpcos/"+rule.getName()+".hen");
			}
			i++;
		}
	}
}

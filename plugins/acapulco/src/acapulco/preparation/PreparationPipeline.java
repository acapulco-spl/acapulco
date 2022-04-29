package acapulco.preparation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import acapulco.preparation.cleaning.Cleaner;
import acapulco.preparation.cleaning.ComplexConstraintRemover;
import acapulco.utils.FileUtils;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.dimacs.DIMACSFormat;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;

public class PreparationPipeline {
	private static PrintStream _err;

	public static void generateAllFromFm(IFeatureModel inputFM, String fmNameInput, String outputPath, IProgressMonitor monitor)
			throws IOException {

		// String directoryInput = new File(fmPath).getParentFile().getAbsolutePath();
		File outputFolder = new File(outputPath);
		outputFolder.mkdirs();

		System.out.println("[Start cleaning.] ");
		closeSystemErr();
		// Clean

		String outputNoComplexConstraintsFMPath = new File(outputFolder, fmNameInput + "-nocomplex.sxfm.xml")
				.getAbsolutePath();
		List<IConstraint> removedConstraints = ComplexConstraintRemover.removeComplexConstraints(inputFM,
				outputNoComplexConstraintsFMPath);

		openSystemErr();

		File outputNoComplexConstraintsCleanFMPath = new File(outputNoComplexConstraintsFMPath.substring(0,
				outputNoComplexConstraintsFMPath.length() - ".sxfm.xml".length()) + ".clean.sxfm.xml");
		Cleaner.clean(outputNoComplexConstraintsFMPath, outputNoComplexConstraintsCleanFMPath.getAbsolutePath());
		System.out.println("[Done cleaning.] ");

		System.out.println("[Start acapulco inputs.] ");
		// dimacs
		File dimacsFm = new File(outputNoComplexConstraintsCleanFMPath.getParentFile(), fmNameInput + ".dimacs");
		File dimacsFmDead = new File(outputNoComplexConstraintsCleanFMPath.getParentFile(),
				fmNameInput + ".dimacs.dead");
		File dimacsFmMandatory = new File(outputNoComplexConstraintsCleanFMPath.getParentFile(),
				fmNameInput + ".dimacs.mandatory");

		IFeatureModel fm = FeatureModelManager.load(outputNoComplexConstraintsCleanFMPath.toPath());
		DIMACSFormat format = new DIMACSFormat();
		String fmString = format.write(fm);
		FileUtils.writeStringToFile(dimacsFm, fmString);
		
		Map<String, String> dimacsNumberToF = new LinkedHashMap<String, String>();
		Map<String, String> fToDimacsNumber = new LinkedHashMap<String, String>();
		for (String line : FileUtils.getLinesOfFile(dimacsFm)) {
			if (line.startsWith("p cnf")) {
				break;
			}
			String[] lineSplit = line.split(" ");
			dimacsNumberToF.put(lineSplit[1], lineSplit[2]);
			fToDimacsNumber.put(lineSplit[2], lineSplit[1]);
		}

		// dead features
		StringBuffer deadString = new StringBuffer();
		FeatureModelAnalyzer operator = new FeatureModelAnalyzer(fm);
		List<IFeature> deadfs = operator.getDeadFeatures(new NullMonitor<LiteralSet>());
		// one id number per line
		for (IFeature f : deadfs) {
			deadString.append(fToDimacsNumber.get(f.getName()) + "\n");
		}
		if (!deadfs.isEmpty()) {
			deadString.setLength(deadString.length() - 1);
		}
		FileUtils.writeStringToFile(dimacsFmDead, deadString.toString());

		// mandatory features
		StringBuffer mandatoryString = new StringBuffer();
		List<IFeature> mandatory = operator.getCoreFeatures(new NullMonitor<LiteralSet>());
		// one id number per line
		for (IFeature f : mandatory) {
			mandatoryString.append(fToDimacsNumber.get(f.getName()) + "\n");
		}
		if (!deadfs.isEmpty()) {
			mandatoryString.setLength(mandatoryString.length() - 1);
		}
		FileUtils.writeStringToFile(dimacsFmMandatory, mandatoryString.toString());
		
		RandomSeedGenerator.createRandomSeed(dimacsFm.getAbsolutePath(), dimacsFm.getAbsolutePath() + ".richseed");

//		// Copy files
//		new File(pathOutput + "\\" + caseName).mkdirs();
//		new File(pathOutput + "\\" + caseName + "\\acapulco").mkdirs();
//		String baseInputPath = directoryInput + "\\" + fmNameInput + ".xml-nocomplex.sxfm.xml.clean.sxfm";
//
//		System.out.println("** Done with file generation. **");
//
		// Copy Acapulco files
//		String baseOutputPath = pathOutput + "\\" + caseName + "\\acapulco\\";
//		Files.copy(new File(baseInputPath + ".dimacs").toPath(),
//				new File(baseOutputPath + caseName + ".dimacs").toPath(), StandardCopyOption.REPLACE_EXISTING);
//		Files.copy(new File(baseInputPath + ".dimacs.augment").toPath(),
//				new File(baseOutputPath + caseName + ".dimacs.augment").toPath(), StandardCopyOption.REPLACE_EXISTING);
//		Files.copy(new File(baseInputPath + ".dimacs.richseed").toPath(),
//				new File(baseOutputPath + caseName + ".dimacs.richseed").toPath(), StandardCopyOption.REPLACE_EXISTING);
//		Files.copy(new File(baseInputPath + ".dimacs.mandatory").toPath(),
//				new File(baseOutputPath + caseName + ".dimacs.mandatory").toPath(),
//				StandardCopyOption.REPLACE_EXISTING);
//		Files.copy(new File(baseInputPath + ".dimacs.dead").toPath(),
//				new File(baseOutputPath + caseName + ".dimacs.dead").toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//		// Cleanup: delete generated files from temporary directory
//		final String fmNameInputFinal = fmNameInput;
//		String[] generatedFiles = new File(directoryInput).list(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.startsWith(fmNameInputFinal) && !name.equals(fmNameInputFinal + ".xml");
//			}
//		});
//		for (String file : generatedFiles)
//			new File(directoryInput + "/" + file).delete();
//
//		System.out.println("** Done with copying. **");
	}

	private static void closeSystemErr() {
		_err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
	}

	private static void openSystemErr() {
		System.setErr(_err);
	}

}

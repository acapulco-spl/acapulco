package acapulco.images.objectives;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import acapulco.images.utils.InteractiveScoreDialog;
import acapulco.objectives.impl.AbstractPostDerivationObjective;

public class ImagesInteractiveObjective extends AbstractPostDerivationObjective {

	@Override
	public String getName() {
		return "Interactive score";
	}

	@Override
	public int getDefaultMinimizeOrMaximize() {
		return DEFAULT_MAXIMIZE;
	}

	@Override
	public double evaluateDerivation(String buildPath) {
		File buildFile = new File(buildPath);
		for (File f : buildFile.listFiles()) {
			if (f.getName().endsWith(".png")) {
				try {
					Display display = new Display();
					Image image = new Image(display, new FileInputStream(f));
					Shell shell = Display.getCurrent().getActiveShell();
					InteractiveScoreDialog dlg = new InteractiveScoreDialog(shell, image);
					dlg.open();
					image.dispose();
					display.dispose();
					return dlg.getScore();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

}

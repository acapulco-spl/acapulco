package acapulco.images.objectives;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import acapulco.images.utils.ImageUtils;
import acapulco.objectives.impl.AbstractPostDerivationObjective;

public class ImagesColorObjective extends AbstractPostDerivationObjective {

	RGB color;

	@Override
	public String getName() {
		if (color == null) {
			return "Color";
		}
		return "Color " + color;
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
					Image image = new Image(Display.getDefault(), new FileInputStream(f));
					ImageData imageData = image.getImageData();
					RGB[] rgbs = imageData.getRGBs();
					if (rgbs != null) {
						// no direct color model
						// TODO
						System.out.println();
					} else {
						double totalSimilarityToColor = 0;
						// direct color model
						for (int x = 0; x < imageData.width; x++) {
							for (int y = 0; y < imageData.height; y++) {
								// ignore transparent
								if (imageData.getAlpha(x, y) != 0) {
									int pixelValue = imageData.getPixel(x, y);
									RGB pixelRGB = imageData.palette.getRGB(pixelValue);
									totalSimilarityToColor += ImageUtils.getColorSimilarity(color, pixelRGB);
								}
							}
						}
						return totalSimilarityToColor;
					}
					image.dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	@Override
	public void configure() {
		super.configure();

		Shell shell = Display.getCurrent().getActiveShell();
		ColorDialog dlg = new ColorDialog(shell);
		dlg.setText("Choose a Color");
		RGB rgb = dlg.open();
		if (rgb != null) {
			color = rgb;
		}
	}



}

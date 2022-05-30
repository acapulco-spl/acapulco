package acapulco.images.objectives;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import acapulco.images.utils.ImageUtils;
import acapulco.objectives.impl.AbstractPostDerivationObjective;

public class ImagesSimilarToImageObjective extends AbstractPostDerivationObjective {

	File selectedFile;

	@Override
	public String getName() {
		if (selectedFile == null) {
			return "Similar to image";
		}
		return "Similar to " + selectedFile.getName();
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
					Image image2 = new Image(Display.getDefault(), new FileInputStream(selectedFile));
					ImageData imageData = image.getImageData();
					ImageData imageData2 = image2.getImageData();
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
								int pixelValue1 = imageData.getPixel(x, y);
								// handle different image sizes
								if (x < imageData2.width && y < imageData2.height) {
									int pixelValue2 = imageData2.getPixel(x, y);
									RGB pixelRGB1 = imageData.palette.getRGB(pixelValue1);
									RGB pixelRGB2 = imageData2.palette.getRGB(pixelValue2);
									totalSimilarityToColor += ImageUtils.getColorSimilarity(pixelRGB1, pixelRGB2);
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
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Select image");
		String[] filterExt = { "*.png" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if (selected != null) {
			selectedFile = new File(selected);
		}
	}

}

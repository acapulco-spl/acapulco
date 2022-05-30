package acapulco.images.utils;

import org.eclipse.swt.graphics.RGB;

public class ImageUtils {

	/**
	 * Color similarity based on euclidean distance
	 * 
	 * @param color1
	 * @param color2
	 * @return a range from 0 to 1: 0 for black to white, 1 for completely equal
	 */
	public static double getColorSimilarity(RGB color1, RGB color2) {
		int r1 = color1.red;
		int r2 = color2.red;
		int g1 = color1.green;
		int g2 = color2.green;
		int b1 = color1.blue;
		int b2 = color2.blue;
		double distance = Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
		double percentageDifference = distance / Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2) + Math.pow(255, 2));
		return 1 - percentageDifference;
	}
	
}

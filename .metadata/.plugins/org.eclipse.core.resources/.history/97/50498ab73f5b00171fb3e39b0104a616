package org.hipi.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SlidingMerge {

	public static void main(String args[]) throws IOException {

		if (args.length != 4) {
			System.out
					.println("Usage: InputPath imageOutput Number of nodes filterSize");
			System.exit(0);
		}
		String inputPath = args[0];
		String imageOutput = args[1];
		int nodes = Integer.valueOf(args[2]);
		int borderSize = Integer.valueOf(args[3]) / 2;
		File f = null;

		// read image
		try {

			// To read image with borders
			// For Image 1
			f = new File(inputPath + "/" + String.valueOf(1) + ".jpg");
			BufferedImage image1 = ImageIO.read(f);
			// Final Image to be output - image
			int width=image1.getWidth();
			int height=image1.getHeight();
			BufferedImage image = new BufferedImage(
					(image1.getWidth() - borderSize * 2) * 2,
					(image1.getHeight() - borderSize * 2) * 2,
					BufferedImage.TYPE_INT_RGB);
			int[] pixels = image1.getRGB(borderSize, borderSize,
					image1.getWidth() - borderSize * 2, image1.getHeight()
							- borderSize * 2, null, 0, image1.getWidth());
			image.setRGB(0, 0, image1.getWidth() - 2 * borderSize,
					image1.getHeight() - 2 * borderSize, pixels, 0,
					image1.getWidth());

			// For Image 2
			f = new File(inputPath + "/" + String.valueOf(2) + ".jpg");
			BufferedImage image2 = ImageIO.read(f);
			pixels = image2.getRGB(borderSize, borderSize,
					image1.getWidth() - borderSize * 2, image1.getHeight()
							- borderSize * 2, null, 0, image1.getWidth());
			image.setRGB(width -borderSize*2, 0, image1.getWidth() - 2 * borderSize,
					image1.getHeight() - 2 * borderSize, pixels, 0,
					image1.getWidth());
			
			// For Image 3
			f = new File(inputPath + "/" + String.valueOf(3) + ".jpg");
			BufferedImage image3 = ImageIO.read(f);
			pixels = image3.getRGB(borderSize, borderSize,
					image1.getWidth() - borderSize * 2, image1.getHeight()
							- borderSize * 2, null, 0, image1.getWidth());
			image.setRGB(0,height-borderSize*2, image1.getWidth() - 2 * borderSize,
					image1.getHeight() - 2 * borderSize, pixels, 0,
					image1.getWidth());
			
			// For Image 4
			f = new File(inputPath + "/" + String.valueOf(4) + ".jpg");
			BufferedImage image4 = ImageIO.read(f);
			pixels = image4.getRGB(borderSize, borderSize,
					image1.getWidth() - borderSize * 2, image1.getHeight()
							- borderSize * 2, null, 0, image1.getWidth());
			image.setRGB(width - borderSize*2, height-borderSize*2, image1.getWidth() - 2 * borderSize,
					image1.getHeight() - 2 * borderSize, pixels, 0,
					image1.getWidth());
			 System.out.println("Reading complete.");

			f = new File(imageOutput);
			ImageIO.write(image, "jpg", f);

			System.out.println("Writing complete.");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

	}

	private static BufferedImage addborder(BufferedImage image, int borderSize) {
		BufferedImage image1 = new BufferedImage(image.getWidth() + borderSize
				* 2, image.getHeight() + borderSize * 2,
				BufferedImage.TYPE_INT_RGB);
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(),
				null, 0, image.getWidth());
		image1.setRGB(borderSize, borderSize, image.getWidth(),
				image.getHeight(), pixels, 0, image.getWidth());
		return image1;
	}
}
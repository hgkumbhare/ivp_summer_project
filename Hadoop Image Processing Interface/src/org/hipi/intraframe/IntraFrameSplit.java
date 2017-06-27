package org.hipi.intraframe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class IntraFrameSplit {

    public static void main(String args[]) throws IOException {

	if (args.length != 4) {
	    System.out
		    .println("Usage: imageInput OutputPath Number of nodes filterSize");
	    System.exit(0);
	}
	String imagePath = args[0];
	String outputPath = args[1];
	int nodes = Integer.valueOf(args[2]);
	int borderSize = Integer.valueOf(args[3]) / 2;

	BufferedImage image = null;
	File f = null;

	// read image
	try {
	    image = ImageIO.read(new File(imagePath));
	    System.out.println("Reading complete.");

	    int width = image.getWidth();
	    int height = image.getHeight();

	    // Add border to image
	    BufferedImage image1 = addborder(image, borderSize);

	    // To write image with borders
	    f = new File(outputPath + "/" + String.valueOf(nodes + 1) + ".jpg");
	    ImageIO.write(image1, "jpg", f);

	    BufferedImage subImage;
	    f = new File(outputPath + "/" + String.valueOf(1) + ".jpg");
	    subImage = image1.getSubimage(0, 0, width / 2 + borderSize * 2,
		    height / 2 + borderSize * 2);
	    ImageIO.write(subImage, "jpg", f);

	    f = new File(outputPath + "/" + String.valueOf(2) + ".jpg");
	    subImage = image1.getSubimage(width / 2, 0, width / 2 + borderSize
		    * 2, height / 2 + borderSize * 2);
	    ImageIO.write(subImage, "jpg", f);

	    f = new File(outputPath + "/" + String.valueOf(3) + ".jpg");
	    subImage = image1.getSubimage(0, height / 2, width / 2 + borderSize
		    * 2, height / 2 + borderSize * 2);
	    ImageIO.write(subImage, "jpg", f);

	    f = new File(outputPath + "/" + String.valueOf(4) + ".jpg");
	    subImage = image1.getSubimage(width / 2, (height / 2), width / 2
		    + borderSize * 2, height / 2 + borderSize * 2);
	    ImageIO.write(subImage, "jpg", f);

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
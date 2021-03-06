import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

/*
 * @author hgkumbhare
 */

/*
 * Contains function useful for dealing with hadoop's text output
 */
public class Text2Image {

    /*
     * Converts hadoop's text output to image in jpeg format
     * 
     * @params String inputfilename : hadoop's output text file name Strin
     * outputdirectory : output directory name for images
     */
    public static void text2image(String inputfilename, String outputdirectory) {
	try {
	    Scanner scanner = new Scanner(new File(inputfilename));

	    int index = 1;
	    while (scanner.hasNext()) {
		System.out.println("Hello" + inputfilename);
		int height = 0, width = 0, channels = 0;

		// height of the image
		height = scanner.nextInt();
		// width of the image
		width = scanner.nextInt();
		// number of channels
		channels = scanner.nextInt();
		channels = 3;

		// object to handle and manipulate the image data
		BufferedImage image = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; ++y) {
		    // System.out.println("Here"+height+" "+width+" "+channels);
		    for (int x = 0; x < width; ++x) {
			// System.out.println("there"+height+" "+width);
			if (channels == 3) {
			    // String value = scanner.next();
			    // System.out.println("value"+value);
			    int r = Math.round(Float.valueOf(scanner.next()));
			    int g = Math.round(Float.valueOf(scanner.next()));
			    int b = Math.round(Float.valueOf(scanner.next()));
			    int gray = r << 16 | g << 8 | b;
			    image.setRGB(x, y, gray);
			    /*
			     * Color myWhite = new Color(scanner.nextInt(),
			     * scanner.nextInt(), scanner.nextInt()); int rgb =
			     * myWhite.getRGB(); image.setRGB(x, y, rgb);
			     */
			} else if (channels == 1) {
			    // System.out.println("i am fine");
			    String value = scanner.next();
			    System.out.println("value" + value);
			    // int gray = value << 16 | value << 8 | value;
			    image.setRGB(x, y, Math.round(Float.valueOf(value)));
			} else {
			    System.out.println("ERROR1");
			    throw new IllegalArgumentException("Invalid Input");
			}
		    }
		}
		// writing image in output directory given in jpeg format
		ImageIO.write(image, "jpg",
			new File(outputdirectory, Integer.toString(index)
				+ ".jpg"));
		index++;
	    }
	    scanner.close();
	} catch (Exception e) {
	    System.out.println("ERROR2" + e);
	}
    }

    static public void main(String args[]) throws IOException {
	System.out.println("Start");
	String inputfilename = args[0];
	String outputdirectory = args[1];
	text2image(inputfilename, outputdirectory);
	System.out.println("Stop");
    }
}

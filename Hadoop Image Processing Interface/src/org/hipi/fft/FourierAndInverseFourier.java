package org.hipi.fft;

//Link : http://users.ecs.soton.ac.uk/msn/book/new_demo/fourier/

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;

import javax.imageio.ImageIO;

public class FourierAndInverseFourier {

    public static FFT fft = new FFT();
    public static InverseFFT inverse = new InverseFFT();

    public static BufferedImage resizeImage(BufferedImage image, int width,
	    int height) {
	// Link :
	// https://stackoverflow.com/questions/16497853/scale-a-bufferedimage-the-fastest-and-easiest-way
	int type = 0;
	type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
		.getType();
	BufferedImage resizedImage = new BufferedImage(width, height, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(image, 0, 0, width, height, null);
	g.dispose();
	return resizedImage;
    }

    public static BufferedImage FFT(BufferedImage inputImage) {
	BufferedImage outputImage = null;
	boolean lowpass = true;
	try {
	    BufferedImage myImage = resizeImage(inputImage, 256, 256);

	    int[] orig = new int[myImage.getWidth() * myImage.getHeight()];
	    PixelGrabber grabber = new PixelGrabber(myImage, 0, 0,
		    myImage.getWidth(), myImage.getHeight(), orig, 0,
		    myImage.getWidth());
	    grabber.grabPixels();

	    fft = new FFT(orig, myImage.getWidth(), myImage.getHeight());
	    fft.intermediate = FreqFilter
		    .filter(fft.intermediate, lowpass, 100);

	    outputImage = new BufferedImage(myImage.getWidth(),
		    myImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	    outputImage.setRGB(0, 0, myImage.getWidth(), myImage.getHeight(),
		    fft.getPixels(), 0, myImage.getWidth());
	} catch (Exception e) {
	    System.out.println(e);
	}
	return outputImage;
    }

    public static void main(String[] args) throws Exception {
	try {
	    System.out.println("STARTED");

	    BufferedImage inputImage = ImageIO.read(new File(
		    "/home/hgkumbhare/Desktop/download.png"));
	    BufferedImage fftoutput = FFT(inputImage);
	    ImageIO.write(fftoutput, "jpg", new File(
		    "/home/hgkumbhare/hogaya.jpg"));

	    BufferedImage inversefftoutput = inverseFFT(fftoutput);
	    ImageIO.write(inversefftoutput, "jpg", new File(
		    "/home/hgkumbhare/outputimage.jpg"));

	} catch (Exception e) {
	    System.out.print("NAHI HUA" + e);
	}
	System.out.println("DONE");
    }

    private static BufferedImage inverseFFT(BufferedImage inputImage) {
	BufferedImage outputImage = null;
	try {
	    TwoDArray output = inverse.transform(fft.intermediate);
	    outputImage = new BufferedImage(inputImage.getWidth(),
		    inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	    outputImage.setRGB(0, 0, inputImage.getWidth(),
		    inputImage.getHeight(), inverse.getPixels(output), 0,
		    inputImage.getWidth());
	} catch (Exception e) {
	    System.out.println(e);
	}
	return outputImage;
    }

}
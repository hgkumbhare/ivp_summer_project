package org.hipi.examples;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.hadoop.mapred.MapOutputCollector;
import org.hipi.image.FloatImage;

public class Paripl {

    /*
     * Contains function useful for writing output to HDFS
     */

    public static void Write_To_Hdfs(FloatImage value, Context context_output) {

	/*
	 * Converts floatImage to text and write it to the hdfs file system
	 * 
	 * @params FloatImage values : hadoop's output FloatImage to be written
	 * to hdfs
	 * 
	 * @params Context context_output :The context of HDFS where the output
	 * is to be written
	 */

	CharSequence cSeq;
	// object to store the image data
	StringBuilder finaldata = new StringBuilder();
	;

	int temp;
	String str;

	// height of image
	int h = value.getHeight();

	// width of image
	int w = value.getWidth();

	// the pointer of image data
	float[] array = value.getData();

	temp = h;
	str = String.valueOf(temp);
	cSeq = str;
	finaldata.append(cSeq);
	cSeq = "\n";
	finaldata.append(cSeq);
	temp = w;
	str = String.valueOf(temp);
	cSeq = str;
	finaldata.append(cSeq);
	cSeq = "\n";
	finaldata.append(cSeq);

	// Traverse image pixel data in raster-scan order and write to HDFS
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {

		if (value.getNumBands() == 1) {
		    temp = (int) (array[j * w + i]);
		    str = String.valueOf(temp);

		    cSeq = str;
		    finaldata.append(cSeq);

		    cSeq = " ";
		    finaldata.append(cSeq);

		}
		if (value.getNumBands() == 3) {
		    temp = (int) (array[(j * w + i) * 3 + 0]);
		    str = String.valueOf(temp);

		    cSeq = str;
		    finaldata.append(cSeq);

		    cSeq = " ";
		    finaldata.append(cSeq);
		    temp = (int) (array[(j * w + i) * 3 + 1]);
		    str = String.valueOf(temp);

		    cSeq = str;
		    finaldata.append(cSeq);

		    cSeq = " ";
		    finaldata.append(cSeq);
		    temp = (int) (array[(j * w + i) * 3 + 2]);
		    str = String.valueOf(temp);

		    cSeq = str;
		    finaldata.append(cSeq);

		    cSeq = " ";
		    finaldata.append(cSeq);
		}
	    }
	}
	cSeq = "\n";
	finaldata.append(cSeq);
	// Emit output of job which will be written to HDFS
	 context.write(null, finaldata);
    }

    /*
     * Contains function useful for Thresholding of given FloatImage
     */
    public static FloatImage Threshold_RGB(FloatImage value, int Tr, int Tg,
	    int Tb) {
	/*
	 * Applies thresholding on the basis of given threshold values
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int Tr :
	 * Threshold Value for Red colour int Tg : Threshold Value for Green
	 * colour int Tb : Threshold Value for Blue colour Return value
	 * FloatImage : Modified FloatImage which is the output to be returned
	 */
	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold threshold pixel value
	float[] thresholdData = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		if ((int) (valData[(j * w + i) * 3 + 0] * 255) > Tr) {
		    thresholdData[(j * w + i) * 3 + 0] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 0] = (float) 0.0;
		}
		if ((int) (valData[(j * w + i) * 3 + 1] * 255) > Tg) {
		    thresholdData[(j * w + i) * 3 + 1] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 1] = (float) 0.0;
		}
		if ((int) (valData[(j * w + i) * 3 + 2] * 255) > Tb) {
		    thresholdData[(j * w + i) * 3 + 2] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 2] = (float) 0.0;
		}
	    }
	}
	// Converting the filtered array of modified image back to FloatImage
	FloatImage result = new FloatImage(w, h, 3, thresholdData);
	return (result);
    }

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
	public void text2image(String inputfilename, String outputdirectory) {
	    try {
		Scanner scanner = new Scanner(new File(inputfilename));

		int index = 1;
		while (scanner.hasNext()) {
		    int height = 0, width = 0, channels = 0;

		    // height of the image
		    height = scanner.nextInt();
		    // width of the image
		    width = scanner.nextInt();
		    // number of channels
		    channels = scanner.nextInt();

		    // object to handle and manipulate the image data
		    BufferedImage image = new BufferedImage(width, height,
			    BufferedImage.TYPE_INT_RGB);
		    for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
			    if (channels == 3) {
				Color myWhite = new Color(scanner.nextInt(),
					scanner.nextInt(), scanner.nextInt());
				int rgb = myWhite.getRGB();
				image.setRGB(x, y, rgb);
			    } else if (channels == 1) {
				int value = scanner.nextInt();
				int gray = value << 16 | value << 8 | value;
				image.setRGB(x, y, gray);
			    } else {
				throw new IllegalArgumentException(
					"Invalid Input");
			    }
			}
		    }
		    // writing image in output directory given in jpeg format
		    ImageIO.write(image, "jpg", new File(outputdirectory,
			    Integer.toString(index) + ".jpg"));
		    index++;
		}
		scanner.close();
	    } catch (Exception e) {
	    }
	}
    }

    /*
     * Contains function useful for dealing with subtraction of a constant value
     * to floatimage
     */
    public static FloatImage SubractConstant(FloatImage value, int constant) {

	/*
	 * Applies subtraction of a constant value to each pixel value of
	 * floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int
	 * constant : constant value to be subtracted from floatimage Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData[(j * w + i) * 3 + 0]
			* 255 - constant;
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}

		filtered[(j * w + i) * 3 + 1] = valData[(j * w + i) * 3 + 1]
			* 255 - constant;
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}

		filtered[(j * w + i) * 3 + 2] = valData[(j * w + i) * 3 + 2]
			* 255 - constant;
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with subtraction of two floatimages
     */
    public static FloatImage Subtract2Images(FloatImage value1,
	    FloatImage value2) {

	/*
	 * Applies subtraction of pixel value of two floatimages of identical
	 * size
	 * 
	 * @params FloatImage value1 : Input first Image in FloatImage format
	 * from which second floatimage will subtracted FloatImage value2 :
	 * Input second Image in FloatImage format which is to be subtracted
	 * Return value FloatImage : Modified FloatImage which is the output to
	 * be returned
	 */

	// Get dimensions of image1(=image2)
	int w = value1.getWidth();
	int h = value1.getHeight();

	// Get pointer to image1 data
	float[] valData1 = value1.getData();

	// Get pointer to image2 data
	float[] valData2 = value2.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData1[(j * w + i) * 3 + 0]
			* 255 - valData2[(j * w + i) * 3 + 0] * 255;
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}

		filtered[(j * w + i) * 3 + 1] = valData1[(j * w + i) * 3 + 1]
			* 255 - valData2[(j * w + i) * 3 + 1] * 255;
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}

		filtered[(j * w + i) * 3 + 2] = valData1[(j * w + i) * 3 + 2]
			* 255 - valData2[(j * w + i) * 3 + 2] * 255;
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}

	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);
    }

    /*
     * Contains function useful for dealing with vertical sobel operator
     */
    public static FloatImage SobelY(FloatImage value) {

	/*
	 * Detects all vertical edges in the given FloatImage and returns the
	 * modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */

	// Defining a vertical sobel operator
	float filter[] = new float[9];
	int size = 3;

	filter[0] = (float) (-1.0);
	filter[1] = (float) (-2.0);
	filter[2] = (float) (-1.0);
	filter[3] = (float) (0.0);
	filter[4] = (float) (0.0);
	filter[5] = (float) (0.0);
	filter[6] = (float) (1.0);
	filter[7] = (float) (2.0);
	filter[8] = (float) (1.0);

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with horizontal sobel operator
     */
    public static FloatImage SobelX(FloatImage value) {

	/*
	 * Detects all horizontal edges in the given FloatImage and returns the
	 * modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which has edges detected
	 */

	// Defining a horizontal sobel operator
	float filter[] = new float[9];
	int size = 3;

	filter[0] = (float) (-1.0);
	filter[1] = (float) (0.0);
	filter[2] = (float) (1.0);
	filter[3] = (float) (-2.0);
	filter[4] = (float) (0.0);
	filter[5] = (float) (2.0);
	filter[6] = (float) (-1.0);
	filter[7] = (float) (0.0);
	filter[8] = (float) (1.0);

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);
    }

    /*
     * Contains function useful for Sharpening of given FloatImage
     */
    public static FloatImage sharpening(FloatImage value) {
	/*
	 * Applies sharpening effect on the floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();
	int size = 3;

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr + (valData[(k * w + l) * 3 + 0] * 255);
			    sumg = sumg + (valData[(k * w + l) * 3 + 1] * 255);
			    sumb = sumb + (valData[(k * w + l) * 3 + 2] * 255);
			}
		    }
		}

		sumr = sumr - valData[(j * w + i) * 3 + 0];
		sumg = sumg - valData[(j * w + i) * 3 + 1];
		sumb = sumb - valData[(j * w + i) * 3 + 2];

		filtered[(j * w + i) * 3 + 0] = valData[(j * w + i) * 3 + 0]
			+ ((8 * valData[(j * w + i) * 3 + 0] - sumr) / (size * size));
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}
		filtered[(j * w + i) * 3 + 1] = valData[(j * w + i) * 3 + 1]
			+ ((8 * valData[(j * w + i) * 3 + 1] - sumg) / (size * size));
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}
		filtered[(j * w + i) * 3 + 2] = valData[(j * w + i) * 3 + 2]
			+ ((8 * valData[(j * w + i) * 3 + 2] - sumb) / (size * size));
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array of modified image back to FloatImage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for converting the rgb scale floatimage to the
     * gray scale floatimage
     */

    public static FloatImage RgbToGray(FloatImage value) {

	/*
	 * Converts the given floatimage to gray scale floatimage
	 * 
	 * @ params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Grayscale FloatImage which is the output to be
	 * returned
	 */
	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h element float array to hold gray pixel value
	float[] grayData = new float[w * h];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		grayData[j * w + i] = (float) 0.299
			* valData[(j * w + i) * 3 + 0] + (float) 0.587
			* valData[(j * w + i) * 3 + 1] + (float) 0.114
			* valData[(j * w + i) * 3 + 2]; // RGB
	    }
	}

	// Converting the filtered array of modified image back to floatimage
	FloatImage result = new FloatImage(w, h, 1, grayData);
	return (result);
    }

    /*
     * Contains function useful for dealing with vertical prewitt operator
     */
    public static FloatImage PrewittY(FloatImage value) {

	/*
	 * Detects all vertical edges in the given FloatImage and returns the
	 * modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */

	// Defining a vertical prewitt operator
	int size = 3;
	float filter[] = new float[9];

	filter[0] = (float) -1.0;
	filter[1] = (float) 0.0;
	filter[2] = (float) 1.0;
	filter[3] = (float) -1.0;
	filter[4] = (float) 0.0;
	filter[5] = (float) 1.0;
	filter[6] = (float) -1.0;
	filter[7] = (float) 0.0;
	filter[8] = (float) 1.0;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with horizontal prewitt operator
     */
    public static FloatImage PrewittX(FloatImage value) {

	/*
	 * Detects all horizontal edges in the given FloatImage and returns the
	 * modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format
	 */

	// Defining a horizontal prewitt operator
	int size = 3;
	float filter[] = new float[9];

	filter[0] = (float) -1.0;
	filter[1] = (float) -1.0;
	filter[2] = (float) -1.0;
	filter[3] = (float) 0.0;
	filter[4] = (float) 0.0;
	filter[5] = (float) 0.0;
	filter[6] = (float) 1.0;
	filter[7] = (float) 1.0;
	filter[8] = (float) 1.0;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array back to floatimage
	// FloatImage result : Modified FloatImage where edges are detected
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    /*
     * Contains function useful for Otsu Thresholding of given FloatImage
     */
    public static FloatImage OtsuThresh_RGB(FloatImage value) {
	/*
	 * Applies thresholding on the basis of given threshold values
	 * 
	 * @params FloatImage value : Input Image in FloatImage format
	 * 
	 * Return value FloatImage : Modified FloatImage which is the output to
	 * be returned
	 */

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold threshold pixel value
	float[] thresholdData = new float[w * h * 3];

	char r = 'R';
	char g = 'G';
	char b = 'B';

	// get histogram for r,g,b components
	int[] histDataR = getHistogram(valData, w, h, 'R');
	int[] histDataG = getHistogram(valData, w, h, 'G');
	int[] histDataB = getHistogram(valData, w, h, 'B');

	// Total number of pixels
	int total = h * w;

	// calculate threshold values
	int Tr = getThresholdValue(histDataR, total);
	int Tg = getThresholdValue(histDataG, total);
	int Tb = getThresholdValue(histDataB, total);

	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		if ((int) (valData[(j * w + i) * 3 + 0] * 255) > Tr) {
		    thresholdData[(j * w + i) * 3 + 0] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 0] = (float) 0.0;
		}
		if ((int) (valData[(j * w + i) * 3 + 1] * 255) > Tg) {
		    thresholdData[(j * w + i) * 3 + 1] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 1] = (float) 0.0;
		}
		if ((int) (valData[(j * w + i) * 3 + 2] * 255) > Tb) {
		    thresholdData[(j * w + i) * 3 + 2] = (float) 255.0;
		} else {
		    thresholdData[(j * w + i) * 3 + 2] = (float) 0.0;
		}
	    }

	}
	// Converting the filtered array of modified image back to FloatImage
	FloatImage result = new FloatImage(w, h, 3, thresholdData);
	return (result);
    }

    public static int[] getHistogram(float[] valData, int width, int height,
	    char color) {
	int[] histogram = new int[256];

	if (color == 'R') {
	    for (int j = 0; j < height; j++) {
		for (int i = 0; i < width; i++) {
		    histogram[(int) (valData[(j * width + i) * 3 + 0] * 255)]++;
		}
	    }
	} else if (color == 'G') {
	    for (int j = 0; j < height; j++) {
		for (int i = 0; i < width; i++) {
		    histogram[(int) (valData[(j * width + i) * 3 + 1] * 255)]++;
		}
	    }
	} else {
	    for (int j = 0; j < height; j++) {
		for (int i = 0; i < width; i++) {
		    histogram[(int) (valData[(j * width + i) * 3 + 2] * 255)]++;
		}
	    }
	}
	return histogram;
    }

    public static int getThresholdValue(int[] histData, int total) {
	float sum = 0;
	for (int t = 0; t < 256; t++) {
	    sum = sum + (t * histData[t]);
	}

	float sumB = 0;
	int wB = 0;
	int wF = 0;

	float varMax = 0;
	int threshold = 0;

	for (int t = 0; t < 256; t++) {
	    // Weight Background
	    wB = wB + histData[t];
	    if (wB == 0) {
		continue;
	    }
	    // Weight Foreground
	    wF = total - wB;
	    if (wF == 0) {
		break;
	    }

	    sumB = (float) (sumB + (t * histData[t]));

	    // Mean Background
	    float mB = sumB / wB;
	    // Mean Foreground
	    float mF = (sum - sumB) / wF;

	    // Calculate Between Class Variance
	    float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

	    // Check if new maximum found
	    if (varBetween > varMax) {
		varMax = varBetween;
		threshold = t;
	    }
	}
	return threshold;
    }

    /*
     * Contains function useful for Otsu Thresholding of given FloatImage
     */
    public static FloatImage OtsuThresh_Gray(FloatImage value) {
	/*
	 * Applies thresholding on the basis of given threshold values
	 * 
	 * @params FloatImage value : Input Image in FloatImage format
	 */
	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h element float array to hold threshold pixel value
	float[] thresholdData = new float[w * h];

	// get histogram
	int[] histData = getHistogram(valData);

	// Total number of pixels
	int total = valData.length;

	float sum = 0;
	for (int t = 0; t < 256; t++) {
	    sum = sum + (t * histData[t]);
	}

	float sumB = 0;
	int wB = 0;
	int wF = 0;

	float varMax = 0;
	int threshold = 0;

	for (int t = 0; t < 256; t++) {
	    wB = wB + histData[t]; // Weight Background
	    if (wB == 0) {
		continue;
	    }
	    wF = total - wB; // Weight Foreground
	    if (wF == 0) {
		break;
	    }

	    sumB = (float) (sumB + (t * histData[t]));

	    float mB = sumB / wB; // Mean Background
	    float mF = (sum - sumB) / wF; // Mean Foreground

	    // Calculate Between Class Variance
	    float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

	    // Check if new maximum found
	    if (varBetween > varMax) {
		varMax = varBetween;
		threshold = t;
	    }
	}
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		if (valData[j * w + i] < threshold) {
		    thresholdData[j * w + i] = (float) 0.0;
		} else {
		    thresholdData[j * w + i] = (float) 255.0;
		}
	    }
	}

	// Converting the filtered array of modified image back to FloatImage
	// FloatImage finaldata : Output FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 1, thresholdData);
	return result;

    }

    public static int[] getHistogram(float[] valData) {
	int[] histogram = new int[256];

	for (int index = 0; index < valData.length; index++) {
	    histogram[(int) (valData[index] * 255)]++;
	}
	return histogram;
    }

    /*
     * Contains function useful for dealing with multiplication of a constant
     * value to floatimage
     */
    public static FloatImage MultiplyConstant(FloatImage value, int constant) {

	/*
	 * Applies multiplication of a constant value to each pixel value of
	 * floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int
	 * constant : constant value to be multiplied with floatimage Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */
	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData[(j * w + i) * 3 + 0]
			* 255 * constant;
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}

		filtered[(j * w + i) * 3 + 1] = valData[(j * w + i) * 3 + 1]
			* 255 * constant;
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}

		filtered[(j * w + i) * 3 + 2] = valData[(j * w + i) * 3 + 2]
			* 255 * constant;
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with multiplication of two
     * floatimages
     */
    public static FloatImage Mult2Images(FloatImage value1, FloatImage value2) {

	/*
	 * Applies multiplication of pixel value of two floatimages of identical
	 * size
	 * 
	 * @params FloatImage value1 : Input first Image in FloatImage format
	 * FloatImage value2 : Input second Image in FloatImage format
	 */
	// Get dimensions of image1(=image2)
	int w = value1.getWidth();
	int h = value1.getHeight();

	// Get pointer to image1 data
	float[] valData1 = value1.getData();

	// Get pointer to image2 data
	float[] valData2 = value2.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData1[(j * w + i) * 3 + 0]
			* 255 * valData2[(j * w + i) * 3 + 0] * 255;
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}

		filtered[(j * w + i) * 3 + 1] = valData1[(j * w + i) * 3 + 1]
			* 255 * valData2[(j * w + i) * 3 + 1] * 255;
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}

		filtered[(j * w + i) * 3 + 2] = valData1[(j * w + i) * 3 + 2]
			* 255 * valData2[(j * w + i) * 3 + 2] * 255;
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	// FloatImage result : Modified FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    public static int findmedian(int numarray[], int n) {
	/*
	 * Finds median of the given integer array
	 * 
	 * @params int numarray[] : integer array int n : size of the array
	 * 
	 * @return int : The median value of the array
	 */
	Arrays.sort(numarray);
	int median;
	if (n % 2 == 0) {
	    median = (numarray[n / 2] + numarray[(n / 2) - 1]) / 2;
	} else {
	    median = numarray[n / 2];
	}
	return (median);
    }

    /*
     * Contains function useful to blur the image by Median filtering
     */
    public static FloatImage filtermed(FloatImage value, int size) {
	/*
	 * Applies Median filter i.e. blurring effect to the given FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int size
	 * : size of median filter Return value FloatImage : Modified FloatImage
	 * which is the output to be returned
	 */

	// Filters for R,G,B
	int filter1[] = new int[1000];
	int filter2[] = new int[1000];
	int filter3[] = new int[1000];

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];
	int k, l;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		for (k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {

			    filter1[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 0] * (float) 255.0);
			    filter2[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 1] * (float) 255.0);
			    filter3[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 2] * (float) 255.0);
			}
		    }
		}
		int median1;
		median1 = findmedian(filter1, size * size);
		int median2;
		median2 = findmedian(filter2, size * size);
		int median3;
		median3 = findmedian(filter3, size * size);

		filtered[(j * w + i) * 3 + 0] = (float) median1;
		filtered[(j * w + i) * 3 + 1] = (float) median2;
		filtered[(j * w + i) * 3 + 2] = (float) median3;
	    }
	}

	// Converting the filtered array of modified image back to FloatImage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with Mean filtering of floatimage
     */
    public static FloatImage MeanFilter(FloatImage value, int size) {

	/*
	 * Applies mean filtering i.e blurring effect to the given floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int size
	 * : size of mean filter matrix
	 */

	// Defining a matrix of mean filter according to given size
	float[] filter = new float[size * size];
	for (int i = 0; i < size * size; i++) {
	    filter[i] = (float) (1.0 / (size * size));
	}

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + ((valData[(k * w + l) * 3 + 0] * 255) * filter[(k - (j - (size / 2)))
					    * size + (l - (i - (size / 2)))]);
			    sumg = sumg
				    + ((valData[(k * w + l) * 3 + 1] * 255) * filter[(k - (j - (size / 2)))
					    * size + (l - (i - (size / 2)))]);
			    sumb = sumb
				    + ((valData[(k * w + l) * 3 + 2] * 255) * filter[(k - (j - (size / 2)))
					    * size + (l - (i - (size / 2)))]);
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = sumr;
		filtered[(j * w + i) * 3 + 1] = sumg;
		filtered[(j * w + i) * 3 + 2] = sumb;

	    }
	}
	// Converting the filtered array back to floatimage
	// FloatImage finaldata : Modified FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;
    }

    /*
     * Contains function useful for dealing with Positive Laplacian operator
     */
    public static FloatImage LaplacianPositive(FloatImage value) {

	/*
	 * Positive Laplacian operator is use to take out outward edges in an
	 * image Detects all outward edges in the given FloatImage and returns
	 * the modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which is the output with
	 * modified edges to be returned
	 */

	// Defining a Positive Laplacian operator
	int size = 3;
	float filter[] = new float[9];

	filter[0] = (float) 0.0;
	filter[1] = (float) 1.0;
	filter[2] = (float) 0.0;
	filter[3] = (float) 1.0;
	filter[4] = (float) -4.0;
	filter[5] = (float) 1.0;
	filter[6] = (float) 0.0;
	filter[7] = (float) 1.0;
	filter[8] = (float) 0.0;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array of modified image back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);
    }

    /*
     * Contains function useful for dealing with Negative Laplacian operator
     */
    public static FloatImage LaplacianNegative(FloatImage value) {

	/*
	 * Negative Laplacian operator is use to take out inward edges in an
	 * image Detects all inward edges in the given FloatImage and returns
	 * the modified FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format
	 */

	// Defining a Negative Laplacian operator
	int size = 3;
	float filter[] = new float[9];

	filter[0] = (float) 0.0;
	filter[1] = (float) -1.0;
	filter[2] = (float) 0.0;
	filter[3] = (float) -1.0;
	filter[4] = (float) 4.0;
	filter[5] = (float) -1.0;
	filter[6] = (float) 0.0;
	filter[7] = (float) -1.0;
	filter[8] = (float) 0.0;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array of modified image back to floatimage
	// FloatImage finaldata : Modified FloatImage where edges are detected
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    /*
     * Contains function useful to blur the image by Gaussian filtering
     */

    public static float gauss_value(int x, int y, float sigma) {
	/*
	 * Finds gauss functions value for given x,y
	 * 
	 * @params int x : x value in gauss function int y : y value in gauss
	 * function float sigma : standard deviation of Gaussian Blur
	 */
	float gvalue = (float) ((float) 1.0
		/ ((2 * Math.PI * ((float) (sigma * sigma)))) * (float) Math
		.exp((((x * x) + (y * y))) / (2 * sigma * sigma)));
	return (gvalue);
    }

    public static FloatImage GaussianBlur(FloatImage value, int radius,
	    float sigma) {
	/*
	 * Applies Gaussian filter i.e. Gaussian blur to the given FloatImage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int
	 * radius : Gaussian Radius for the gauss filter float sigma : standard
	 * deviation of Gaussian Blur Return value FloatImage : Modified
	 * FloatImage which is the output to be returned
	 */
	CharSequence cSeq;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// calculate the gaussian filter of given radius
	// array to store gaussian filter
	int size = (radius * 2) + 1;
	float filter[] = new float[size * size];
	float sum = (float) 0.0;
	for (int i = -radius; i <= radius; i++) {
	    for (int j = radius; j >= -radius; j--) {
		filter[(radius + i) * (2 * radius + 1) + (radius - j)] = gauss_value(
			j, i, sigma);
		sum = sum
			+ filter[(radius + i) * (2 * radius + 1) + (radius - j)];
	    }
	}

	for (int i = 0; i < (2 * radius + 1); i++) {
	    for (int j = 0; j < (2 * radius + 1); j++) {
		filter[i * (2 * radius + 1) + j] = (float) (filter[i
			* (2 * radius + 1) + j] / sum);
	    }
	}

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr);
		filtered[(j * w + i) * 3 + 1] = (int) (sumg);
		filtered[(j * w + i) * 3 + 2] = (int) (sumb);
	    }
	}

	// Converting the filtered array of modified image back to FloatImage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful for dealing with divison of a constant value to
     * floatimage
     */
    public static FloatImage DivideConstant(FloatImage value, int constant) {

	/*
	 * Applies divison by a constant value to each pixel value of floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int
	 * constant : constant value as a divisor
	 */

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = (valData[(j * w + i) * 3 + 0] * 255)
			/ constant;

		filtered[(j * w + i) * 3 + 1] = (valData[(j * w + i) * 3 + 1] * 255)
			/ constant;

		filtered[(j * w + i) * 3 + 2] = (valData[(j * w + i) * 3 + 2] * 255)
			/ constant;

	    }
	}
	// Converting the filtered array back to floatimage
	// FloatImage result : Modified FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    /*
     * Contains function useful for dealing with divison of two floatimages
     */
    public static FloatImage Divide2Images(FloatImage value1, FloatImage value2) {

	/*
	 * Applies divison of pixel value of two floatimages of identical size
	 * 
	 * @params FloatImage value1 : Input first Image in FloatImage format
	 * FloatImage value2 : Input second Image in FloatImage format Return
	 * value FloatImage : Modified FloatImage which is the output to be
	 * returned
	 */
	// Get dimensions of image1(=image2)
	int w = value1.getWidth();
	int h = value1.getHeight();

	// Get pointer to image1 data
	float[] valData1 = value1.getData();

	// Get pointer to image2 data
	float[] valData2 = value2.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = (valData1[(j * w + i) * 3 + 0] * 255)
			/ (valData2[(j * w + i) * 3 + 0] * 255);

		filtered[(j * w + i) * 3 + 1] = (valData1[(j * w + i) * 3 + 1] * 255)
			/ (valData2[(j * w + i) * 3 + 1] * 255);

		filtered[(j * w + i) * 3 + 2] = (valData1[(j * w + i) * 3 + 2] * 255)
			/ (valData2[(j * w + i) * 3 + 2] * 255);

	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);

    }

    /*
     * Contains function useful to reduce noise by conservative filtering
     */

    public static FloatImage Conservative_Filter(FloatImage value) {
	/*
	 * Converts input FloatImage to conservative filtered image
	 * 
	 * @params FloatImage value : Input Image in FloatImage format
	 */

	// Filters for R,G,B
	int filter1[] = new int[1000]; // for red band
	int filter2[] = new int[1000]; // for green band
	int filter3[] = new int[1000]; // for blue band
	int size = 3;

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	int k, l;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		for (k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {

			    filter1[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 0] * (float) 255.0);
			    filter2[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 1] * (float) 255.0);
			    filter3[(k - (j - (size / 2))) * size
				    + (l - (i - (size / 2)))] = (int) ((float) valData[(k
				    * w + l) * 3 + 2] * (float) 255.0);
			}
		    }
		}
		int min1, max1;
		min1 = findmin(filter1, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 0]) * 255));
		max1 = findmax(filter1, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 0]) * 255));
		int min2, max2;
		min2 = findmin(filter2, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 1]) * 255));
		max2 = findmax(filter2, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 1]) * 255));
		int min3, max3;
		min3 = findmin(filter3, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 2]) * 255));
		max3 = findmax(filter3, size * size, (int) ((float) (valData[(j
			* w + i) * 3 + 2]) * 255));

		if ((float) (valData[(j * w + i) * 3 + 0] * 255) < min1) {
		    filtered[(j * w + i) * 3 + 0] = (float) min1;
		} else if (((int) (float) valData[(j * w + i) * 3 + 0] * 255) > max1) {
		    filtered[(j * w + i) * 3 + 0] = (float) max1;
		}

		if (((int) (float) valData[(j * w + i) * 3 + 1] * 255) < min2) {
		    filtered[(j * w + i) * 3 + 1] = (float) min2;
		} else if (((int) (float) valData[(j * w + i) * 3 + 1] * 255) > max2) {
		    filtered[(j * w + i) * 3 + 1] = (float) max2;
		}

		if (((int) (float) valData[(j * w + i) * 3 + 2] * 255) < min3) {
		    filtered[(j * w + i) * 3 + 2] = (float) min3;
		} else if (((int) (float) valData[(j * w + i) * 3 + 2] * 255) > max3) {
		    filtered[(j * w + i) * 3 + 2] = (float) max3;
		}

	    }
	}
	// Converting the filtered array of modified image back to FloatImage
	// FloatImage result : Output FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    // Finds minimum in the array except the midval
    public static int findmin(int array[], int n, int midval) {
	Arrays.sort(array);
	int min = array[0];
	if (min == midval) {
	    min = array[1];
	}
	return min;
    }

    // Find maximum in the array except the midval
    public static int findmax(int array[], int n, int midval) {
	Arrays.sort(array);

	int max = array[n - 1];
	if (max == midval) {
	    max = array[n - 2];
	}
	return max;
    }

    public static FloatImage Blending(FloatImage value1, FloatImage value2,
	    float blendratio) {

	/*
	 * Applies blending using given blending ratio to two floatimages of
	 * identical size
	 * 
	 * @params FloatImage value1 : Input first Image in FloatImage format
	 * FloatImage value2 : Input second Image in FloatImage format float
	 * blendratio : value of blending ratio (value lies between 0 to 1)
	 * Return value FloatImage : Modified FloatImage which is the output to
	 * be returned
	 */

	// Get dimensions of image1(=image2)
	int w = value1.getWidth();
	int h = value1.getHeight();

	// Get pointer to image1 data
	float[] valData1 = value1.getData();

	// Get pointer to image2 data
	float[] valData2 = value2.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = (blendratio)
			* (valData1[(j * w + i) * 3 + 0] * 255)
			+ (1 - blendratio)
			* (valData2[(j * w + i) * 3 + 0] * 255);
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}

		filtered[(j * w + i) * 3 + 1] = (blendratio)
			* (valData1[(j * w + i) * 3 + 1] * 255)
			+ (1 - blendratio)
			* (valData2[(j * w + i) * 3 + 1] * 255);
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}

		filtered[(j * w + i) * 3 + 2] = (blendratio)
			* (valData1[(j * w + i) * 3 + 2] * 255)
			+ (1 - blendratio)
			* (valData2[(j * w + i) * 3 + 2] * 255);
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return (result);
    }

    /*
     * Contains function useful for dealing with filtering of floatimage
     */
    public static FloatImage ApplyFilter(FloatImage value, float filter[],
	    int size) {

	/*
	 * Applies general filtering effect to the given floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int size
	 * : size of filter matrix float filter[] : filter matrix to be applied
	 * on floatimage
	 */

	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	float sumr, sumg, sumb;

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		sumr = 0;
		sumg = 0;
		sumb = 0;
		for (int k = j - (size / 2); k <= j + (size / 2); k++) {
		    for (int l = i - (size / 2); l <= i + (size / 2); l++) {
			if (k >= 0 && k < h && l >= 0 && l < w) {
			    sumr = sumr
				    + (valData[(k * w + l) * 3 + 0] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumg = sumg
				    + (valData[(k * w + l) * 3 + 1] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			    sumb = sumb
				    + (valData[(k * w + l) * 3 + 2] * 255)
				    * filter[(k - (j - (size / 2))) * size
					    + (l - (i - (size / 2)))];
			}
		    }
		}
		filtered[(j * w + i) * 3 + 0] = (int) (sumr / (size * size));
		if (filtered[(j * w + i) * 3 + 0] < 0) {
		    filtered[(j * w + i) * 3 + 0] = 0;
		}
		filtered[(j * w + i) * 3 + 1] = (int) (sumg / (size * size));
		if (filtered[(j * w + i) * 3 + 1] < 0) {
		    filtered[(j * w + i) * 3 + 1] = 0;
		}
		filtered[(j * w + i) * 3 + 2] = (int) (sumb / (size * size));
		if (filtered[(j * w + i) * 3 + 2] < 0) {
		    filtered[(j * w + i) * 3 + 2] = 0;
		}
	    }
	}

	// Converting the filtered array back to floatimage
	// FloatImage result : Modified FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

    /*
     * Contains function useful for dealing with addition of a constant value to
     * floatimage
     */
    public static FloatImage AddConstant(FloatImage value, int constant) {

	/*
	 * Applies addition of a constant value to each pixel value of
	 * floatimage
	 * 
	 * @params FloatImage value : Input Image in FloatImage format int
	 * constant : constant value to be added to floatimage Return value
	 * FloatImage : Modified FloatImage which is the output to be returned
	 */
	// Get dimensions of image
	int w = value.getWidth();
	int h = value.getHeight();

	// Get pointer to image data
	float[] valData = value.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData[(j * w + i) * 3 + 0]
			* 255 + constant;
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}

		filtered[(j * w + i) * 3 + 1] = valData[(j * w + i) * 3 + 1]
			* 255 + constant;
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}

		filtered[(j * w + i) * 3 + 2] = valData[(j * w + i) * 3 + 2]
			* 255 + constant;
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	FloatImage result = new FloatImage(w, h, 3, filtered);

	return (result);

    }

    /*
     * Contains function useful for dealing with addition of two floatimages
     */
    public static FloatImage Add2Images(FloatImage value1, FloatImage value2) {

	/*
	 * Applies addition of pixel value of two floatimages of identical size
	 * 
	 * @params FloatImage value1 : Input first Image in FloatImage format
	 * FloatImage value2 : Input second Image in FloatImage format
	 */

	// Get dimensions of image1(=image2)
	int w = value1.getWidth();
	int h = value1.getHeight();

	// Get pointer to image1 data
	float[] valData1 = value1.getData();

	// Get pointer to image2 data
	float[] valData2 = value2.getData();

	// Initialize w*h*3 element float array to hold modified pixel value
	float[] filtered = new float[w * h * 3];

	// Traverse image pixel data in raster-scan order and update
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		filtered[(j * w + i) * 3 + 0] = valData1[(j * w + i) * 3 + 0]
			* 255 + valData2[(j * w + i) * 3 + 0] * 255;
		if (filtered[(j * w + i) * 3 + 0] > 255) {
		    filtered[(j * w + i) * 3 + 0] = 255;
		}

		filtered[(j * w + i) * 3 + 1] = valData1[(j * w + i) * 3 + 1]
			* 255 + valData2[(j * w + i) * 3 + 1] * 255;
		if (filtered[(j * w + i) * 3 + 1] > 255) {
		    filtered[(j * w + i) * 3 + 1] = 255;
		}

		filtered[(j * w + i) * 3 + 2] = valData1[(j * w + i) * 3 + 2]
			* 255 + valData2[(j * w + i) * 3 + 2] * 255;
		if (filtered[(j * w + i) * 3 + 2] > 255) {
		    filtered[(j * w + i) * 3 + 2] = 255;
		}
	    }
	}
	// Converting the filtered array back to floatimage
	// FloatImage result : Modified FloatImage where the output has to be
	// returned
	FloatImage result = new FloatImage(w, h, 3, filtered);
	return result;

    }

}

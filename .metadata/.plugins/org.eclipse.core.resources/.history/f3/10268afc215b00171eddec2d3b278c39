package org.hipi.examples;

import org.hipi.image.FloatImage;
import org.hipi.image.HipiImageHeader;
import org.hipi.imagebundle.mapreduce.HibInputFormat;

import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

import java.awt.*;  
import javax.swing.JFrame;  

public class HelloWorld extends Configured implements Tool {
  
public static int mykeyvalue=0;

public static class HelloWorldMapper extends Mapper<HipiImageHeader, FloatImage, IntWritable, FloatImage> {
    
    public void map(HipiImageHeader key, FloatImage value, Context context) 
        throws IOException, InterruptedException {

	System.out.println("INSIDE MAPPER");
      // Verify that image was properly decoded, is of sufficient size, and has three color channels (RGB)
      if (value != null && value.getWidth() > 1 && value.getHeight() > 1 && value.getNumBands() == 3) {

        // Emit record to reducer
	mykeyvalue+=1;
        context.write(new IntWritable(mykeyvalue), value);

      } // If (value != null...
      
    } // map()

  } // HelloWorldMapper


public static class HelloWorldReducer extends Reducer<IntWritable, FloatImage, IntWritable, FloatImage> {

    public void reduce(IntWritable key, Iterable<FloatImage> values, Context context)
        throws IOException, InterruptedException {

	System.out.println("INSIDE REDUCER");

      for (FloatImage value : values) {
	 // Get dimensions of image
        int w = value.getWidth();
        int h = value.getHeight();

        // Get pointer to image data
        float[] valData = value.getData();

        // Initialize w*h element float array to hold gray pixel value
	float[] grayData = new float[w*h];

        // Traverse image pixel data in raster-scan order and update
        for (int j = 0; j < h; j++) {
          for (int i = 0; i < w; i++) {
            grayData[j*w+i] = (float)0.299*valData[(j*w+i)*3+0]+(float)0.587*valData[(j*w+i)*3+1]+(float)0.114*valData[(j*w+i)*3+2]; // RGB
          }
        }
	
	FloatImage finalData = new FloatImage(h, w, 1,grayData);

	System.out.println("HELLO wt " + finalData.getWidth() + " height "+finalData.getHeight());


        // Emit output of job which will be written to HDFS
        context.write(key, finalData);
      }

    } // reduce()

  } // HelloWorldReducer

  
  public int run(String[] args) throws Exception {
    // Check input arguments
    if (args.length != 2) {
      System.out.println("Usage: helloWorld <input HIB> <output directory>");
      System.exit(0);
    }
    
    // Initialize and configure MapReduce job
    Job job = Job.getInstance();
    // Set input format class which parses the input HIB and spawns map tasks
    job.setInputFormatClass(HibInputFormat.class);
    // Set the driver, mapper, and reducer classes which express the computation
    job.setJarByClass(HelloWorld.class);
    job.setMapperClass(HelloWorldMapper.class);
    job.setReducerClass(HelloWorldReducer.class);
    // Set the types for the key/value pairs passed to/from map and reduce layers
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(FloatImage.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(FloatImage.class);
    
    // Set the input and output paths on the HDFS
    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    // Execute the MapReduce job and block until it complets
    boolean success = job.waitForCompletion(true);
    
    // Return success or failure
    return success ? 0 : 1;
  }



public static class MyCanvas extends Canvas{  
      
    public void paint(Graphics g) {  
  
        Toolkit t=Toolkit.getDefaultToolkit();  
        Image i=t.getImage("p3.gif");  
        g.drawImage(i, 120,100,this);  
          
    }  
  
}  







  public static void main(String[] args) throws Exception {
	System.out.println("JUST INSIDE START");
	long startTime = System.nanoTime();
    ToolRunner.run(new HelloWorld(), args);
	long difference = System.nanoTime() - startTime;
	System.out.println("Total execution time: " + String.format("%d min, %d sec", TimeUnit.NANOSECONDS.toHours(difference), TimeUnit.NANOSECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(difference))));


	 MyCanvas m=new MyCanvas();  
        JFrame f=new JFrame();  
        f.add(m);  
        f.setSize(400,400);  
        f.setVisible(true);  



    System.exit(0);
  }
  
}
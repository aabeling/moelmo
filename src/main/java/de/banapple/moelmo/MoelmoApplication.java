package de.banapple.moelmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import de.banapple.moelmo.utils.ContourAreaComparator;

@SpringBootApplication
public class MoelmoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoelmoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(ApplicationContext context) {

        return args -> {

            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            System.out.println("Welcome to OpenCV " + Core.VERSION);

            // detectFace();
            detectPaper();

        };
    }

    public void detectPaper() {

        System.out.println("detect paper demo");

        Mat image = Highgui.imread(getClass().getResource("/images/20170924_180354.jpg").getPath());
        Mat original = new Mat();
        image.copyTo(original);
        
        /*
         * preprocess(greyscale, blur and threshold) as in
         * http://arnab.org/blog/so-i-suck-24-automating-card-games-using-opencv
         * -and-python
         */
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(image, image, new Size(new Point(1, 1)), 1000);
        Imgproc.threshold(image, image, 120, 255, Imgproc.THRESH_BINARY);

        /*
         * find contours
         */
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("contours found: " + contours.size());

        /* find biggest contour */
        Collections.sort(contours, new ContourAreaComparator());
        MatOfPoint biggestContour = contours.get(0);
        
        /* get rectangular representation of sheet */
        MatOfPoint2f curve = new MatOfPoint2f(biggestContour.toArray());
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        double peri = Imgproc.arcLength(curve, true);
        Imgproc.approxPolyDP(curve, approxCurve, 0.02 * peri, true);
        approxCurve.reshape(-1, 2);
//        drawContour(approxCurve, original);
        
        /* 
         * transform 
         */
        Mat src = new Mat(4,1,CvType.CV_32FC2);
        Point[] points = approxCurve.toArray();        
        src.put(0, 0,                 
                points[0].x,points[0].y,        
                points[3].x,points[3].y,
                points[1].x,points[1].y,
                points[2].x,points[2].y);
        Mat dst = new Mat(4,1,CvType.CV_32FC2);
        double newWidth = 1000.0;
        double newHeight = 1000.0;
        dst.put(0,0,
                0.0,0.0,
                newWidth,0.0, 
                0.0,newHeight,
                newWidth,newHeight);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(
                original, 
                dst, 
                perspectiveTransform, 
                new Size(newWidth, newHeight));
                
        /* Save the visualized detection */
        String filename = "/tmp/paperDetection.png";
        System.out.println(String.format("Writing %s", filename));
//        Highgui.imwrite(filename, original);
        Highgui.imwrite(filename, dst);
    }

    private void drawContour(
            final MatOfPoint2f approxCurve,
            final Mat original) {
        
        MatOfPoint mop = new MatOfPoint();
        approxCurve.convertTo(mop, CvType.CV_32S);
        List<MatOfPoint> polylineContours = new ArrayList<>();
        polylineContours.add(mop);
        Imgproc.drawContours(original, 
                polylineContours , 
                0, new Scalar(0,0,255));
    }
    
    /**
     * Takes a picture with some faces and detects them. The result is written
     * to a file and faces are marked with rectangles.
     */
    public void detectFace() {

        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier(
                getClass().getResource("/opencv/lbpcascade_frontalface.xml").getPath());
        Mat image = Highgui.imread(getClass().getResource("/images/beatles.jpg").getPath());

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            System.out.println(rect.toString());
            Core.rectangle(image,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        // Save the visualized detection.
        String filename = "/tmp/faceDetection.png";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, image);
    }
}

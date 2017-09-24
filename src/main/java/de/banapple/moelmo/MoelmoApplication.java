package de.banapple.moelmo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

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

            detectFace();

        };
    }

    /**
     * Takes a picture with some faces and detects them.
     * The result is written to a file and faces are marked
     * with rectangles.
     */
    public void detectFace() {

        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier(
                getClass().getResource("/lbpcascade_frontalface.xml").getPath());
        Mat image = Highgui.imread(getClass().getResource("/beatles.jpg").getPath());

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

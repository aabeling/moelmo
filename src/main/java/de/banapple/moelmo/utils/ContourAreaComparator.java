package de.banapple.moelmo.utils;

import java.util.Comparator;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ContourAreaComparator implements Comparator<Mat> {

    @Override
    public int compare(Mat l, Mat r) {

        double area0 = Imgproc.contourArea(r);
        double area1 = Imgproc.contourArea(l);

        if (area0 < area1) {
            return -1;
        } else if (area0 > area1) {
            return 1;
        } else {
            return 0;
        }
    }

}

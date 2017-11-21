package com.itech.ocr;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class SkewDetect {
    private static String input = "D:/server/pdfs/1/pos_41.png";
    private static String output = "D:/server/pdfs/1/p16-rotate.jpg";

    @BeforeClass
    public static void init() {
        System.out.println("Load OpenCV library...");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void start() {
        computeSkew(input);
    }

    public Mat deskew(Mat src, double angle) {
        Point center = new Point(src.width()/2, src.height()/2);
        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        //1.0 means 100 % scale
        Size size = new Size(src.width(), src.height());
        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return src;
    }

    public void computeSkew( String inFile ) {
        //Load this image in grayscale
        Mat img = Highgui.imread( inFile, Highgui.IMREAD_GRAYSCALE );

        //Binarize it
        //Use adaptive threshold if necessary
        //Imgproc.adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 40);
        Imgproc.threshold( img, img, 200, 255, THRESH_BINARY );

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not( img, img );
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        //We can now perform our erosion, we must declare our rectangle-shaped structuring element and call the erode function
        Imgproc.erode(img, img, element);

        //Find all white pixels
        Mat wLocMat = Mat.zeros(img.size(),img.type());
        Core.findNonZero(img, wLocMat);

        //Create an empty Mat and pass it to the function
        MatOfPoint matOfPoint = new MatOfPoint( wLocMat );

        //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
        MatOfPoint2f mat2f = new MatOfPoint2f();
        matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

        //Get rotated rect of white pixels
        RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );

        Point[] vertices = new Point[4];
        rotatedRect.points(vertices);
        List<MatOfPoint> boxContours = new ArrayList<>();
        boxContours.add(new MatOfPoint(vertices));
        Imgproc.drawContours( img, boxContours, 0, new Scalar(128, 128, 128), -1);

        double resultAngle = rotatedRect.angle;
        if (rotatedRect.size.width > rotatedRect.size.height)
        {
            rotatedRect.angle += 90.f;
        }

        //Or
        rotatedRect.angle = rotatedRect.angle < -45 ? rotatedRect.angle + 90.f : rotatedRect.angle;
        rotatedRect.angle = rotatedRect.angle > 45 ? rotatedRect.angle - 90.f : rotatedRect.angle;

        System.out.println(rotatedRect.angle);
        Mat result = deskew( Highgui.imread( inFile ), rotatedRect.angle);
        Highgui.imwrite( output, result );

    }
}

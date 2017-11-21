package com.itech.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class DetectPageRotating {
    //D:/server/pdfs/1/K1-008-22_Page_1.jpg
    private static String file = "D:/server/pdfs/1/p16.jpg";
    private static String output = "D:/server/1.jpg";

    @BeforeClass
    public static void init() {
        System.out.println("Load OpenCV library...");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void start() throws IOException {
        computeSkew(file);
        /*PDDocument document = PDDocument.load(new File(file));
        for(int i = 0; i<document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            //System.out.println(page.getMatrix());
            PDGraphicsState aaa = new PDGraphicsState(page.getCropBox());
            System.out.println(aaa.getCurrentTransformationMatrix());
            PDRectangle mediaBox = page.getMediaBox();
            boolean isLandscape = mediaBox.getWidth() > mediaBox.getHeight();
            //System.out.println(page.getRotation()+" "+isLandscape);
        }*/
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
        Mat source = Highgui.imread(inFile,0);
        Size size = source.size();
        Core.bitwise_not(source, source);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(source, lines, 1, Math.PI / 180, 100, size.width / 2.f, 20);
        double angle = 0.;
        for(int i = 0; i<lines.height(); i++){
            for(int j = 0; j<lines.width();j++){
                angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);
            }
        }
        angle /= lines.size().area();
        angle = angle * 180 / Math.PI;
        System.out.println(angle);
    }
}

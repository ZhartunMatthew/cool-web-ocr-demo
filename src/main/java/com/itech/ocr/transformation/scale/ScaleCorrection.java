package com.itech.ocr.transformation.scale;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.itech.ocr.transformation.skew.PdfToImages.directory;

public class ScaleCorrection {
    public static void main(String[] args) {

    }

    public static void correctScale(int countPage) {
        for(int i=0; i<countPage; i++) {
            correct(directory+(i+1)+".png");
        }
    }

    private static void correct(String urlImage) {
        Mat img1= Highgui.imread(urlImage);
        List<Rect> letterBBoxes1 = detectLetters(img1);

        for(int i = 0; i < letterBBoxes1.size(); i++) {
            if(letterBBoxes1.get(i).area() < 300000) continue;
            //Core.rectangle(img1, letterBBoxes1.get(i).br(), letterBBoxes1.get(i).tl(), new Scalar(0, 255, 0), 3, 8, 0);
            Rect rect = letterBBoxes1.get(i);
            Mat ROI = img1.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);
            Highgui.imwrite(urlImage, ROI);
            break;
        }
    }

    public static List<Rect> detectLetters(Mat img){
        List<Rect> boundRect=new ArrayList<>();

        Mat img_gray = new Mat(),
                img_sobel = new Mat(),
                img_threshold = new Mat(),
                kernel = new Mat(),
                dilated = new Mat();

        Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
        //Imgproc.Sobel(img_gray, img_sobel, CvType.CV_8U, 1, 0, 3, 1, 0, 4);

        //at src, Mat dst, double thresh, double maxval, int type
        Imgproc.threshold(img_gray, img_threshold, 150, 255, 1);

        kernel=Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
        Imgproc.dilate(img_threshold, dilated, kernel, new Point(), 5);//todo: change count for iteration, so get max pretty result
        //Imgproc.morphologyEx(dilated, dilated, Imgproc.RETR_EXTERNAL, kernel);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilated, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        //Highgui.imwrite(directory + "abc1.png", dilated);//todo - for check current result
        List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());

        for(int i = 0; i < contours.size(); i++) {

            MatOfPoint2f mMOP2f1 = new MatOfPoint2f();
            MatOfPoint2f mMOP2f2 = new MatOfPoint2f();

            contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
            Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 2, true);
            mMOP2f2.convertTo(contours.get(i), CvType.CV_32S);


            Rect appRect = Imgproc.boundingRect(contours.get(i));
            //if (appRect.width<appRect.height) {
            boundRect.add(appRect);
            //}
        }

        return boundRect;
    }
}

package com.itech.ocr.correction;

import java.io.IOException;

import static com.itech.ocr.transformation.scale.ScaleCorrection.correctScale;
import static com.itech.ocr.transformation.skew.ImagesToPdf.toPdf;
import static com.itech.ocr.transformation.skew.PdfToImages.getImagesFromPDF;
import static com.itech.ocr.transformation.skew.RotationImages.rotate;

public class Correction {
    public static void correctSkewAndScale(String pdfUrl) throws IOException {
        int countPage = getImagesFromPDF(pdfUrl);
        rotate(countPage);
        correctScale(countPage);
        toPdf(countPage, pdfUrl);
    }
}

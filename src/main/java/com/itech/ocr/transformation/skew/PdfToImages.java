package com.itech.ocr.transformation.skew;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.itech.ocr.transformation.skew.ImagesToPdf.toPdf;
import static com.itech.ocr.transformation.skew.RotationImages.rotate;

public class PdfToImages {
    public static int QUALITY_COEFFICIENT = 3;
    public static String directory = "D:\\server\\transformation\\";
    private static String urlPdf = "D:\\server\\transformation\\test.pdf";

    public static int getImagesFromPDF(String urlPdf) throws IOException {
        PDDocument document = PDDocument.load(new File(urlPdf));
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        System.out.println(document.getPage(0).getMediaBox().getWidth());
        System.out.println(document.getPage(0).getMediaBox().getHeight());

        int countPage = document.getNumberOfPages();

        for (int page = 0; page < document.getNumberOfPages(); ++page)
        {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 72*QUALITY_COEFFICIENT, ImageType.GRAY);
            // suffix in filename will be used as the file format
            ImageIOUtil.writeImage(bim, directory + (page+1) + ".png", 300);
        }
        document.close();
        return countPage;
    }

    public static void main(String[] args) throws IOException {
        /*PDDocument document = PDDocument.load(new File(urlPdf));
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        System.out.println(document.getPage(0).getMediaBox().getWidth());
        System.out.println(document.getPage(0).getMediaBox().getHeight());

        int countPage = document.getNumberOfPages();

        for (int page = 0; page < document.getNumberOfPages(); ++page)
        {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 72*QUALITY_COEFFICIENT, ImageType.GRAY);
            // suffix in filename will be used as the file format
            ImageIOUtil.writeImage(bim, directory + (page+1) + ".png", 700);
        }
        document.close();*/

        //rotate(2);
        toPdf(2, directory+"test2.pdf");
    }
}

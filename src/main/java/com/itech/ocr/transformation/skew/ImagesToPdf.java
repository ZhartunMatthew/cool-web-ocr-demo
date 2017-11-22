package com.itech.ocr.transformation.skew;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.itech.ocr.transformation.skew.PdfToImages.QUALITY_COEFFICIENT;
import static com.itech.ocr.transformation.skew.PdfToImages.directory;

public class ImagesToPdf {
    public static void toPdf(int countPdf, String urlPdf) throws IOException {
        PDDocument document = new PDDocument();
        PDPage pages[] = new PDPage[countPdf];
        for(int i=0; i < countPdf; i++) {
            String someImage = directory + (i+1)+".png";
            InputStream in = new FileInputStream(someImage);
            BufferedImage bimg = ImageIO.read(in);
            //ImageIOUtil.writeImage(bimg, directory + (i)+"-2" + ".png", 300);
            float width = bimg.getWidth()/QUALITY_COEFFICIENT;
            float height = bimg.getHeight()/QUALITY_COEFFICIENT;

            pages[i] = new PDPage(new PDRectangle(width, height));
            document.addPage(pages[i]);

            PDImageXObject img = PDImageXObject.createFromFile(someImage, document);
            PDPageContentStream contentStream = new PDPageContentStream(document, pages[i]);
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.drawImage(img, 0, 0, width, height);
            contentStream.close();
            in.close();
        }


        PDRectangle mediaBox = pages[0].getMediaBox();
        System.out.println( "Width result:" + mediaBox.getWidth() );
        System.out.println( "Height result:" + mediaBox.getHeight() );

        document.save(urlPdf);
        document.close();
    }
}

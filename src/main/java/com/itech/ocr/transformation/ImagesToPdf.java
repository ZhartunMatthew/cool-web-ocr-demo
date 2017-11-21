package com.itech.ocr.transformation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.itech.ocr.transformation.PdfToImages.QUALITY_COEFFICIENT;
import static com.itech.ocr.transformation.PdfToImages.directory;

public class ImagesToPdf {
    public static void toPdf(int countPdf) throws IOException {
        PDDocument document = new PDDocument();
        PDPage pages[] = new PDPage[countPdf];
        for(int i=0; i < countPdf; i++) {
            String someImage = directory + (i+1)+".png";
            InputStream in = new FileInputStream(someImage);
            BufferedImage bimg = ImageIO.read(in);
            float width = bimg.getWidth()/QUALITY_COEFFICIENT;
            float height = bimg.getHeight()/QUALITY_COEFFICIENT;
            System.out.println(width);
            pages[i] = new PDPage(new PDRectangle(width, height));
            document.addPage(pages[i]);

            PDImageXObject img = PDImageXObject.createFromFile(someImage, document);
            PDPageContentStream contentStream = new PDPageContentStream(document, pages[i]);

            contentStream.drawImage(img, 0, 0, width, height);
            contentStream.close();
            in.close();
        }


        PDRectangle mediaBox = pages[0].getMediaBox();
        System.out.println( "Width:" + mediaBox.getWidth() );
        System.out.println( "Height:" + mediaBox.getHeight() );

        document.save(directory+"test.pdf");
        document.close();
    }
}

package com.itech.ocr;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class RotatePage {
    private static String file = "D:/server/pdfs/K1-008-2.pdf";
    private static String file_out = "D:/server/pdfs/K1-008-2-10-degrees.pdf";
    @Test
    public void start() throws IOException, DocumentException {
        PdfReader reader = new PdfReader(file);
        int n = reader.getNumberOfPages();
        int rot;
        PdfDictionary pageDict;
        for (int i = 1; i <= n; i++) {
            rot = reader.getPageRotation(i);
            pageDict = reader.getPageN(i);
            pageDict.put(PdfName.ROTATE, new PdfNumber(rot + 90));
        }
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file_out));
        stamper.close();
        reader.close();
    }
}

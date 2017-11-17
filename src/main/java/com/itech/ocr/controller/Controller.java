package com.itech.ocr.controller;

import com.itech.ocr.converter.Converter;
import com.itech.ocr.main.Combiner;
import com.itech.ocr.main.FromXLSXtoJSON;
import com.itech.ocr.main.Recognizer;
import com.itech.ocr.main.UploadToGoogleDrive;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;
//Required imports
import java.io.*;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class Controller extends HttpServlet {

    public static String staticPath = "D:\\server\\";

    public void init(ServletConfig config) throws ServletException{
        System.out.println("INIT");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        System.out.println("GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST");

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload loader = new ServletFileUpload(factory);

        try {
            System.out.println("Before parse");
            List<FileItem> formItems = loader.parseRequest(req);
            FileItem fileItem = formItems.get(0);

            System.out.println("File name: " + fileItem.getName());

            File directory = new File(staticPath);
            if (!directory.exists()) {
                System.out.println("Dir not exist");
            }

            File file = new File(staticPath + "/file.pdf");

            try {
                fileItem.write(file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            double bytesSourceFile = file.length();
            double kilobytes = (bytesSourceFile / 1024);
            System.out.println("kb: "+kilobytes);
            //480, 203, 114

            String[] recognizeArgs = new String[3];
            recognizeArgs[0] = "recognize";
            recognizeArgs[1] = file.getAbsolutePath();
            recognizeArgs[2] = staticPath + "out.pdf";
            Recognizer.main(recognizeArgs);

            //cut page for pdf-tables
            PdfReader pdfReader = new PdfReader(staticPath + "out.pdf");
            pdfReader.selectPages("2,3");
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream(staticPath + "out-pdftables.pdf"));
            pdfStamper.close();
            pdfReader.close();


            String json = "";
            String urlToExcel = "";

            if(kilobytes > 400) { // pdf -> xml&excel
                //TODO: 3 страницы со ссылками попробовать прогнать
                System.out.println("reference type");
                String[] processArgs = new String[4];
                processArgs[0] = "processFields";
                processArgs[1] = recognizeArgs[2];
                processArgs[2] = staticPath + "stmt.xml";
                processArgs[3] = staticPath + "data.xml";
                Recognizer.main(processArgs);
                Recognizer.postProcessingXML(processArgs[3], staticPath + "out.xml");
                Recognizer.PDFtoCSV(staticPath + "out-pdftables.pdf", staticPath + "out.xlsx");
                Combiner.findLinksAndMerge(staticPath + "out.xml", staticPath + "out-merged.xml");
                json = Converter.convert(staticPath + "out-merged.xml", staticPath + "out.json").replaceAll("\\\\\"", "\"");
            } else if(kilobytes > 150 && kilobytes < 400) { // table file
                System.out.println("table type");
                Recognizer.PDFtoCSV(staticPath + "out.pdf", staticPath + "out.xlsx");
                urlToExcel = UploadToGoogleDrive.upload();
            } else {
                System.out.println("just form type");
                String[] processArgs = new String[4];
                processArgs[0] = "processFields";
                processArgs[1] = recognizeArgs[2];
                processArgs[2] = staticPath + "settings.xml";
                processArgs[3] = staticPath + "data.xml";
                Recognizer.main(processArgs);

                Recognizer.postProcessingXML(processArgs[3], staticPath + "out.xml");
                json = Converter.convert(staticPath + "out.xml", staticPath + "out.json");
            }


            String encodedBase64 = null;
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                encodedBase64 = new String(Base64.encodeBase64(bytes));
                fileInputStreamReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            req.setAttribute("idDocument", urlToExcel);
            req.setAttribute("pdf", encodedBase64);
            req.setAttribute("output", json.trim());
            req.getRequestDispatcher("index.jsp").forward(req, resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

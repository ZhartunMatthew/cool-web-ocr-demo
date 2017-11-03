package com.itech.ocr.controller;


import com.itech.ocr.converter.Converter;
import com.itech.ocr.main.Recognizer;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

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

            String[] recognizeArgs = new String[3];
            recognizeArgs[0] = "recognize";
            recognizeArgs[1] = file.getAbsolutePath();
            recognizeArgs[2] = staticPath + "out.pdf";
            Recognizer.main(recognizeArgs);

            String[] processArgs = new String[4];
            processArgs[0] = "processFields";
            processArgs[1] = recognizeArgs[2];
            processArgs[2] = staticPath + "settings.xml";
            processArgs[3] = staticPath + "data.xml";
            Recognizer.main(processArgs);

            Recognizer.postProcessingXML(processArgs[3], staticPath + "out.xml");

            String json = Converter.convert(staticPath + "out.xml", staticPath + "out.json");

//            PrintWriter writer = resp.getWriter();
//            writer.println(json);

            req.setAttribute("output", json.trim());
            req.getRequestDispatcher("index.jsp").forward(req, resp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

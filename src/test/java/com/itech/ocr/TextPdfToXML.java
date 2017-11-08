package com.itech.ocr;

import com.itech.ocr.entity.Check;
import com.itech.ocr.main.ClientSettings;
import com.itech.ocr.main.CmdLineOptions;
import com.itech.ocr.ocr.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TextPdfToXML {
    @Test
    public void processing() {
        String args[] = new String[4];
        args[0] = "processFields";
        args[1] = "D:\\server\\W2.pdf";
        args[2] = "D:\\server\\settingsW2.xml";
        args[3] = "D:\\server\\output-test-junit-W2.xml";

        System.out.println("Process documents using ABBYY Cloud OCR SDK.\n");

        if (!checkAppId()) {
            return;
        }

        restClient = new Client();
        // replace with 'https://cloud.ocrsdk.com' to enable secure connection
        restClient.serverUrl = "http://cloud.ocrsdk.com";
        restClient.applicationId = ClientSettings.APPLICATION_ID;
        restClient.password = ClientSettings.PASSWORD;

        Vector<String> argList = new Vector<String>(Arrays.asList(args));

        // Select processing mode
        String mode = args[0];
        argList.remove(0);

        try {
            if (mode.equalsIgnoreCase("help")) {
                displayDetailedHelp(args[1]);
            } else if (mode.equalsIgnoreCase("recognize")) {
                performRecognition(argList);
            } else if (mode.equalsIgnoreCase("busCard")) {
                performBusinessCardRecognition(argList);
            } else if (mode.equalsIgnoreCase("textField")) {
                performTextFieldRecognition(argList);
            } else if (mode.equalsIgnoreCase("barcode")) {
                performBarcodeRecognition(argList);
            } else if (mode.equalsIgnoreCase("processFields")) {
                performFieldsRecognition(argList);
            } else if (mode.equalsIgnoreCase("MRZ")) {
                performMrzRecognition(argList);
            } else if (mode.equalsIgnoreCase("receipt")) {
                performReceiptProcessing(argList);
            } else {
                System.out.println("Unknown mode: " + mode);
                return;
            }
        } catch (Exception e) {
            System.out.println("Exception occured:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeToNewXML(List<Check> list, String out) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root elements
        Document doc = docBuilder.newDocument();

		/*Element rootElement = doc.createElement("data");
		doc.appendChild(rootElement);*/

        Element rootElement = doc.createElement("check");
        doc.appendChild(rootElement);


        for(Check check : list) {
            //staff elements
            Element row = doc.createElement("row");
            rootElement.appendChild(row);


            //firstname elements
            row.setAttribute("id", check.getId().replaceAll("\"", "").trim().split("=")[1]);

            //lastname elements
            Element value = doc.createElement("value");
            value.appendChild(doc.createTextNode(check.getValue()));
            row.appendChild(value);

            //nickname elements
//			Element line = doc.createElement("line");
//			line.appendChild(doc.createTextNode(check.getLine()));
//			row.appendChild(line);
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result =  new StreamResult(new File(out));
        transformer.transform(source, result);
    }

    /**
     * Check that user specified application id and password.
     *
     * @return false if no application id or password
     */
    private static boolean checkAppId() {
        String appId = ClientSettings.APPLICATION_ID;
        String password = ClientSettings.PASSWORD;
        if (appId.isEmpty() || password.isEmpty()) {
            System.out
                    .println("Error: No application id and password are specified.");
            System.out.println("Please specify them in com.itechart.recognizer.main.ClientSettings.java.");
            return false;
        }
        return true;
    }

    private static void displayHelp() {
        System.out
                .println("This program is able to recognize:\n"
                        + "\n"
                        + "1. Single- and multipage documents and convert them to txt, xml, pdf and other formats.\n"
                        + "  java com.itechart.recognizer.main.Recognizer recognize testImage.jpg result.xml\n"
                        + "  java com.itechart.recognizer.main.Recognizer recognize page1.jpg page2.jpg page3.jpg result.pdf --lang=French,Spanish\n"
                        + "\n"
                        + "2. Business cards to vCard, xml and csv\n"
                        + "  java com.itechart.recognizer.main.Recognizer busCard image.jpg result.xml\n"
                        + "\n"
                        + "3. Printed and handprinted text snippets\n"
                        + "  java com.itechart.recognizer.main.Recognizer textField image.jpg result.xml\n"
                        + "\n"
                        + "4. Barcodes\n"
                        + "  java com.itechart.recognizer.main.Recognizer barcode image.jpg result.xml\n"
                        + "\n"
                        + "5. Many different snippets on document\n"
                        + "  java com.itechart.recognizer.main.Recognizer processFields image1.jpg image2.jpg image3.tif settings.xml result.xml\n"
                        + "\n"
                        + "6. Machine-Readable Zones (MRZ) of Passports, ID cards, Visas and other official documents\n"
                        + "  java com.itechart.recognizer.main.Recognizer MRZ image.jpg result.xml\n"
                        + "\n"
                        + "7. Receipts\n"
                        + "  java com.itechart.recognizer.main.Recognizer receipt image.jpg result.xml\n"
                        + "\n"
                        + "For detailed help, call\n"
                        + "  java com.itechart.recognizer.main.Recognizer help <mode>\n"
                        + "where <mode> is one of: recognize, busCard, textField, barcode, checkmark, processFields, MRZ, receipt");
    }

    /**
     * Display detailed help for each processing mode.
     */
    private static void displayDetailedHelp(String mode) {
        if (mode.equalsIgnoreCase("recognize")) {
            displayRecognizeHelp();
        } else if (mode.equalsIgnoreCase("busCard")) {
            displayBusCardHelp();
        } else if (mode.equalsIgnoreCase("textField")) {
            displayTextFieldHelp();
        } else if (mode.equalsIgnoreCase("barcode")) {
            displayBarcodeHelp();
        } else if (mode.equalsIgnoreCase("processFields")) {
            displayProcessFieldsHelp();
        } else if (mode.equalsIgnoreCase("MRZ")) {
            displayProcessMrzHelp();
        } else if (mode.equalsIgnoreCase("receipt")) {
            displayProcessReceiptHelp();
        } else {
            System.out.println("Unknown processing mode.");
        }
    }

    private static void displayRecognizeHelp() {
        System.out
                .println("Recognize single or multipage documents.\n"
                        + "\n"
                        + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer recognize [--lang=<languages>] <file> [<file2> ..] <output file>\n"
                        + "\n"
                        + "Output format is selected by output file extension. Possible values are:\n"
                        + ".txt, .xml, .pdf, .docx, .rtf\n"
                        + "\n"
                        + "Examples:\n"
                        + "java Recognizer recognize image.tif result.txt\n"
                        + "java com.itechart.recognizer.main.Recognizer recognize --lang=French,Spanish page1.png page2.png page3.png result.pdf\n"
                        + "java com.itechart.recognizer.main.Recognizer recognize --lang=Japanese image.jpg output.rtf\n");
    }

    /**
     * Parse command line and recognize one or more documents.
     */
    public static void performRecognition(Vector<String> argList)
            throws Exception {
        String language = CmdLineOptions.extractRecognitionLanguage(argList);
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        ProcessingSettings.OutputFormat outputFormat = outputFormatByFileExt(outputPath);

        ProcessingSettings settings = new ProcessingSettings();
        settings.setLanguage(language);
        settings.setOutputFormat(outputFormat);

        Task task = null;
        if (argList.size() == 1) {
            System.out.println("Uploading file..");
            System.out.println(argList.elementAt(0));
            task = restClient.processImage(argList.elementAt(0), settings);

        } else if (argList.size() > 1) {

            // Upload images via submitImage and start recognition with
            // processDocument
            for (int i = 0; i < argList.size(); i++) {
                System.out.println(String.format("Uploading image %d/%d..",
                        i + 1, argList.size()));
                String taskId = null;
                if (task != null) {
                    taskId = task.Id;
                }

                Task result = restClient.submitImage(argList.elementAt(i), taskId);
                if (task == null) {
                    task = result;
                }
            }
            task = restClient.processDocument(task.Id, settings);

        } else {
            System.out.println("No files to process.");
            return;
        }

        waitAndDownloadResult(task, outputPath);
    }

    private static void displayBusCardHelp() {
        System.out
                .println("Recognize single business card.\n"
                        + "\n"
                        + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer busCard [--lang=<languages>] <file> <output file>\n"
                        + "\n"
                        + "Output format is selected by output file extension. Possible values are:\n"
                        + ".vcf, .xml, .csv\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.itechart.recognizer.main.Recognizer busCard image.tif result.vcf\n"
                        + "java com.itechart.recognizer.main.Recognizer busCard --lang=French,Spanish image.png result.xml\n");
    }

    /**
     * Perform recognition of single business card.
     *
     * Recognized result will be saved in special format for business cards:
     * vCard, csv or xml
     */
    private static void performBusinessCardRecognition(Vector<String> argList)
            throws Exception {
        String language = CmdLineOptions.extractRecognitionLanguage(argList);
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        BusCardSettings.OutputFormat outputFormat = bcrOutputFormatByFileExt(outputPath);

        BusCardSettings settings = new BusCardSettings();
        settings.setLanguage(language);

        settings.setOutputFormat(outputFormat);

        if (argList.size() != 1) {
            System.out.println("Invalid number of files to process.");
            return;
        }

        System.out.println("Uploading..");
        Task task = restClient.processBusinessCard(argList.elementAt(0),
                settings);
        waitAndDownloadResult(task, outputPath);
    }

    /**
     * Perform receipt processing.
     *
     * Recognized result will be saved as XML
     */
    private static void performReceiptProcessing(Vector<String> argList)
            throws Exception {
        String receiptCountry = CmdLineOptions.extractReceiptCountry(argList);
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        ReceiptSettings settings = new ReceiptSettings();
        settings.setReceiptCountry(receiptCountry);

        if (argList.size() != 1) {
            System.out.println("Invalid number of files to process.");
            return;
        }

        System.out.println("Uploading..");
        Task task = restClient.processReceipt(argList.elementAt(0), settings);
        waitAndDownloadResult(task, outputPath);
    }

    private static void displayTextFieldHelp() {
        System.out
                .println("Recognize printed or handprinted text field.\n"
                        + "\n"
                        + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer textField [--lang=<languages>] [--options=<options] <file> <output file>\n"
                        + "\n"
                        + "<options> - options passed directly to processTextField RESTful call\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.itechart.recognizer.main.Recognizer textField image.tif result.xml\n"
                        + "java com.itechart.recognizer.main.Recognizer textField --options='letterSet=0123456789/&regExp=[0-9][0-9]' image.tif result.xml\n");
    }

    private static void performTextFieldRecognition(Vector<String> argList)
            throws Exception {
        String language = CmdLineOptions.extractRecognitionLanguage(argList);
        String options = extractExtraOptions(argList);
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        TextFieldSettings settings = new TextFieldSettings();
        settings.setLanguage(language);
        if (options != null) {
            settings.setOptions(options);
        }

        // TODO - different processing options

        if (argList.size() != 1) {
            System.out.println("Invalid number of files to process.");
            return;
        }

        System.out.println("Uploading..");
        Task task = restClient.processTextField(argList.elementAt(0), settings);

        waitAndDownloadResult(task, outputPath);
    }

    private static void displayBarcodeHelp() {
        System.out
                .println("Recognize barcode.\n" + "\n" + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer barcode <file> <output file>\n" + "\n"
                        + "Examples:\n"
                        + "java com.itechart.recognizer.main.Recognizer barcode image.tif result.xml\n");
    }

    private static void performBarcodeRecognition(Vector<String> argList)
            throws Exception {
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        BarcodeSettings settings = new BarcodeSettings();

        // TODO: different barcode types

        if (argList.size() != 1) {
            System.out.println("Invalid number of files to process.");
            return;
        }

        System.out.println("Uploading..");
        Task task = restClient.processBarcodeField(argList.elementAt(0),
                settings);

        waitAndDownloadResult(task, outputPath);
    }

    private static void displayProcessFieldsHelp() {
        System.out
                .println("Process different snippets in one- or multipage document.\n"
                        + "\n"
                        + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer processFields <file1> [file2 ..] <settings.xml> <output file>\n"
                        + "\n"
                        + "For details how to create xml settings see\n"
                        + "http://ocrsdk.com/documentation/specifications/xml-scheme-field-settings/\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.itechart.recognizer.main.Recognizer processFields image1.tif settings.xml result.xml\n"
                        + "java com.itechart.recognizer.main.Recognizer processFields image1.tif image2.tif image3.tif settings.xml result.xml\n");
    }

    /**
     * Perform field-level recognition using processFields call.
     *
     * For details see
     * http://ocrsdk.com/documentation/apireference/processFields/
     */
    public static void performFieldsRecognition(Vector<String> argList)
            throws Exception {

        if (argList.size() < 3) {
            System.out.println("Invalid number of arguments");
            return;
        }

        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);

        String settingsPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        Task task = null;
        for (int i = 0; i < argList.size(); i++) {
            System.out.println(String.format("Uploading image %d/%d..\n",
                    i + 1, argList.size()));

            String taskId = null;
            if (task != null) {
                taskId = task.Id;
            }

            Task result = restClient.submitImage(argList.elementAt(i), taskId);
            if (task == null) {
                task = result;
            }
        }

        System.out.println("Processing..");
        task = restClient.processFields(task.Id, settingsPath);

        waitAndDownloadResult(task, outputPath);
    }

    private static void displayProcessMrzHelp() {
        System.out
                .println("Recognize Machine-Readable Zones of official documents\n"
                        + "Both 2 and 3-line MRZ are supported."
                        + "\n" + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer MRZ <file> <output file.xml>\n" + "\n");
    }

    private static void displayProcessReceiptHelp() {
        System.out
                .println("Process receipt\n"
                        + "\n" + "Usage:\n"
                        + "java com.itechart.recognizer.main.Recognizer receipt <file> <output file.xml>\n" + "\n");
    }

    private static void performMrzRecognition(Vector<String> argList)
            throws Exception {
        String outputPath = argList.lastElement();
        argList.remove(argList.size() - 1);
        // argList now contains list of source images to process

        if (argList.size() != 1) {
            System.out.println("Invalid number of files to process.");
            return;
        }

        System.out.println("Uploading..");
        Task task = restClient.processMrz(argList.elementAt(0));

        waitAndDownloadResult(task, outputPath);
    }

    /**
     * Wait until task processing finishes
     */
    private static Task waitForCompletion(Task task) throws Exception {
        // Note: it's recommended that your application waits
        // at least 2 seconds before making the first getTaskStatus request
        // and also between such requests for the same task.
        // Making requests more often will not improve your application performance.
        // Note: if your application queues several files and waits for them
        // it's recommended that you use listFinishedTasks instead (which is described
        // at http://ocrsdk.com/documentation/apireference/listFinishedTasks/).
        while (task.isTaskActive()) {

            Thread.sleep(5000);
            System.out.println("Waiting..");
            task = restClient.getTaskStatus(task.Id);
        }
        return task;
    }

    /**
     * Wait until task processing finishes and download result.
     */
    private static void waitAndDownloadResult(Task task, String outputPath)
            throws Exception {
        task = waitForCompletion(task);

        if (task.Status == Task.TaskStatus.Completed) {
            System.out.println("Downloading..");
            restClient.downloadResult(task, outputPath);
            System.out.println("Ready");
        } else if (task.Status == Task.TaskStatus.NotEnoughCredits) {
            System.out.println("Not enough credits to process document. "
                    + "Please add more pages to your application's account.");
        } else {
            System.out.println("Task failed");
        }

    }

    /**
     * Extract extra RESTful options from command-line parameters. Parameter is
     * removed after extraction
     *
     * @return extra options string or null
     */
    private static String extractExtraOptions(Vector<String> args) {
        // Extra options parameter has from --options=<options>
        return CmdLineOptions.extractParameterValue("options", args);
    }

    /**
     * Extract output format from extension of output file.
     */
    private static ProcessingSettings.OutputFormat outputFormatByFileExt(
            String filePath) {
        int extIndex = filePath.lastIndexOf('.');
        if (extIndex < 0) {
            System.out
                    .println("No file extension specified. Plain text will be used as output format.");
            return ProcessingSettings.OutputFormat.txt;
        }
        String ext = filePath.substring(extIndex).toLowerCase();
        if (ext.equals(".txt")) {
            return ProcessingSettings.OutputFormat.txt;
        } else if (ext.equals(".xml")) {
            return ProcessingSettings.OutputFormat.xml;
        } else if (ext.equals(".pdf")) {
            return ProcessingSettings.OutputFormat.pdfSearchable;
        } else if (ext.equals(".docx")) {
            return ProcessingSettings.OutputFormat.docx;
        } else if (ext.equals(".rtf")) {
            return ProcessingSettings.OutputFormat.rtf;
        } else {
            System.out
                    .println("Unknown output extension. Plain text will be used.");
            return ProcessingSettings.OutputFormat.txt;
        }
    }

    /**
     * Extract output format for business card from extension of output file.
     */
    private static BusCardSettings.OutputFormat bcrOutputFormatByFileExt(
            String filePath) {
        int extIndex = filePath.lastIndexOf('.');
        if (extIndex < 0) {
            System.out
                    .println("No file extension specified. vCard will be used as output format.");
            return BusCardSettings.OutputFormat.vCard;
        }

        String ext = filePath.substring(extIndex).toLowerCase();
        if (ext.equals(".vcf")) {
            return BusCardSettings.OutputFormat.vCard;
        } else if (ext.equals(".xml")) {
            return BusCardSettings.OutputFormat.xml;
        } else if (ext.equals(".csv")) {
            return BusCardSettings.OutputFormat.csv;
        }

        System.out
                .println("Invalid file extension. vCard will be used as output format.");
        return BusCardSettings.OutputFormat.vCard;
    }

    private static Client restClient;
}

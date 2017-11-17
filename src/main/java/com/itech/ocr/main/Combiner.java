package com.itech.ocr.main;

import com.itech.ocr.converter.Converter;
import com.itech.ocr.entity.Check;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itech.ocr.controller.Controller.staticPath;

public class Combiner {
    public static void main(String[] args) {
        try {
            String str = FromXLSXtoJSON.find("Other information");
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String reference = "STMT";
    /*private static String file = "D:\\server\\K1-008-parse.xml";
    private static String out = "D:\\server\\K1-008-parse-2.xml";*/

    public static void findLinksAndMerge(String file, String out) throws ParserConfigurationException, IOException, SAXException {
        List<Check> checkList = new ArrayList<>();
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(file);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            System.out.println("List of books:");
            System.out.println();
            // Просматриваем все подэлементы корневого - т.е. книги
            NodeList books = root.getChildNodes();
            for (int i = 0; i < books.getLength(); i++) {
                Node book = books.item(i);
                String id = book.getAttributes().getNamedItem("id").getTextContent();
                System.out.println(id);
                // Если нода не текст, то это книга - заходим внутрь
                if (book.getNodeType() != Node.TEXT_NODE) {
                    NodeList bookProps = book.getChildNodes();
                    for(int j = 0; j < bookProps.getLength(); j++) {
                        Node bookProp = bookProps.item(j);
                        // Если нода не текст, то это один из параметров книги - печатаем
                        if (bookProp.getNodeType() != Node.TEXT_NODE) {
                            String value = "";
                            if(bookProp.getChildNodes().item(0)!=null) {
                                value = bookProp.getChildNodes().item(0).getTextContent();
                            }
                            System.out.print(bookProp.getNodeName() + ":");
                            if(value.trim().equals(reference)) {
                                System.out.println(FromXLSXtoJSON.find(id));
                                checkList.add(new Check(id, FromXLSXtoJSON.find(id), ""));
                            } else {
                                checkList.add(new Check(id, value, ""));
                                System.out.println(value);
                            }
                        }
                    }
                    System.out.println("===========>>>>");
                }
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        try {
            Recognizer.writeToNewXML(checkList, out);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}


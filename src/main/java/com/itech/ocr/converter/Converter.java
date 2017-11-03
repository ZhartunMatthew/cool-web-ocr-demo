package com.itech.ocr.converter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Converter {

    public static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public static String convert(String in, String outFile) {
        String jsonPrettyPrintString = "";
        try {
            String text = new String(Files.readAllBytes(Paths.get(in)), StandardCharsets.UTF_8);
            JSONObject xmlJSONObj = XML.toJSONObject(text);
            jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            System.out.println(jsonPrettyPrintString);

            PrintWriter out = new PrintWriter(outFile);
            out.println(jsonPrettyPrintString);

        } catch (JSONException | IOException je) {
            je.printStackTrace();
        }

        return jsonPrettyPrintString;
    }

}

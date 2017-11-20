package com.itech.ocr;

import com.itech.ocr.converter.Converter;
import com.itech.ocr.main.Combiner;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.itech.ocr.controller.Controller.staticPath;

public class JsonFixPrettyView {
    @Test
    public void start() throws IOException, SAXException, ParserConfigurationException, JSONException {
        //Combiner.findLinksAndMerge(staticPath + "out.xml", staticPath + "out-merged.xml");
        String json = Converter.convert(staticPath + "out-merged.xml", staticPath + "out.json").replaceAll("\\\\\"", "\"");

        String text2 = new String(Files.readAllBytes(Paths.get(staticPath+"temporary.json")), StandardCharsets.UTF_8);

        System.out.println(text2);
        //JSONObject obj = new JSONObject(text1);

        JSONObject Obj1 = (JSONObject) new JSONObject(json);
        JSONObject Obj2 = (JSONObject) new JSONObject(text2);
        JSONObject combined = new JSONObject();
        combined.put("document", Obj1);
        combined.put("STMT", Obj2);

        System.out.println(combined.toString(2));
    }
}

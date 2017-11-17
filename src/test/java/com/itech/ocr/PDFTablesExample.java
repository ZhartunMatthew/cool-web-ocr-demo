package com.itech.ocr;

import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class PDFTablesExample {

    @Test
    public void main() throws Exception {
        String file = "D:\\server\\12.pdf";
        String to = "D:\\server\\12.xlsx";
        String API_KEY = "6gmknbl3j02o";
        String format = "xlsx-single";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost("https://pdftables.com" +
                    "/api?key=" + API_KEY + "&format="+format);

            FileBody bin = new FileBody(new File(file));

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("f", bin)
                    .build();
            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity resEntity = response.getEntity();
                FileOutputStream fos = new FileOutputStream(to);
                fos.write(EntityUtils.toByteArray(resEntity));
                fos.close();
                System.out.println(response.getStatusLine());

                System.out.println("Done!");
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}

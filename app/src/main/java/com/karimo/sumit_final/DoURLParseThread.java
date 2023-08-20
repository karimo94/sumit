package com.karimo.sumit_final;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class DoURLParseThread implements Runnable {
    //consider moving this to a separate class file
    String myTestString = null;
    StringBuilder sb = new StringBuilder();
    private boolean apiUsedFlag = false;
    int summarySize;
    Document doc = null;
    SummaryApiObj apiSummary = null;
    org.jsoup.Connection.Response response = null;
    org.jsoup.Connection connection = null;

    public DoURLParseThread(String testString, int size) {
        myTestString = testString;
        summarySize = size;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run()
    {
        try
        {
            URLEncoder.encode(myTestString, "UTF-8") ;
        } catch (UnsupportedEncodingException e2)
        {
            e2.printStackTrace();
        }
        try
        {
            response = Jsoup.connect(myTestString)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(5000)
                    .execute();

            if(response.statusCode() == 200)
            {
                connection = Jsoup.connect(myTestString);
                doc = connection.ignoreContentType(true).get();
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        //********* URL parsing section ************
        //Determine if we can grab content via <p>, or do everything via API
        Elements newsHeadlines = null;
        StringBuilder particularArticle = new StringBuilder();

        //we first check html lang tag, and the article source if cnn (since they render via JS)
        if(doc == null) {
            //more of a hotfix to prevent crashes than anything
            try {
                particularArticle = doAPITextExtraction(myTestString);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            sb.append(particularArticle.toString());
        }
        if(doc != null) {
            if((doc.select("html").first().attr("lang").contains("es") ||
                doc.select("html").first().attr("lang").contains("pt") ||
                doc.select("html").first().attr("lang").contains("fr") ||
                doc.select("html").first().attr("lang").contains("it") ||
                doc.select("html").first().attr("lang").contains("de") ||
                doc.select("html").first().attr("lang").contains("nl")) ||
                (myTestString != null && (myTestString.contains("www.cnn") ||
                        myTestString.contains("www.washingtonpost")))) {

                try {
                    particularArticle = doAPITextExtraction(myTestString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(particularArticle.toString());
                /*apiSummary = doAPISummary();
                //set the flag we used an API
                this.apiUsedFlag = true;*/
            }
            else {
                newsHeadlines = doc.select("p");
                sb.append(newsHeadlines.text());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SummaryApiObj doAPISummary() {
        StringBuilder strJsonResponse = new StringBuilder();
        String apiUrl = "https://text-analysis12.p.rapidapi.com/summarize-text/api/v1.1";
        String summaryPercent = Integer.toString(summarySize);
        myTestString = myTestString.replace("\n","").replace('"','\0');

        Gson g = new Gson();

        LinkedHashMap<String, String> jsonMap = new LinkedHashMap<>();
        jsonMap.put("language", "english");
        jsonMap.put("summary_percent", summaryPercent);
        jsonMap.put("text", myTestString);
        String jsonBody = g.toJson(jsonMap);

        try
        {
            URL url = new URL(apiUrl);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestProperty("content-type", "application/json");
            client.setRequestProperty("x-rapidapi-host", "text-analysis12.p.rapidapi.com");
            client.setRequestProperty("x-rapidapi-key", "e14d9e09a5msh348b2bda8798db2p15d68cjsn1f2e7d7c5740");
            client.setRequestProperty("accept", "application/json, text/plain, */*");
            client.setRequestMethod("POST");
            client.setDoOutput(true);

            //POST to endpoint first
            try(OutputStream os = client.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = null;
            if(client.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String responseOutput = null;
                while ((responseOutput = br.readLine()) != null) {
                    strJsonResponse.append(responseOutput);
                }
            }
            else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
                String responseOutput = null;
                while ((responseOutput = br.readLine()) != null) {
                    strJsonResponse.append(responseOutput);
                }
            }
        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        SummaryApiObj apiSummary = null;
        try
        {
            apiSummary = g.fromJson(strJsonResponse.toString(), SummaryApiObj.class);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
        }
        return apiSummary;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private StringBuilder doAPITextExtraction(String urlInput) throws IOException {
        StringBuilder strJsonResponse = new StringBuilder();
        String apiUrl = "https://text-extract7.p.rapidapi.com";
        String apiEndpoint = "/?url=";
        String reqBody = URLEncoder.encode(urlInput, StandardCharsets.UTF_8.toString());

        try{
            URL url = new URL(apiUrl + apiEndpoint + reqBody);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestProperty("x-rapidapi-host", "text-extract7.p.rapidapi.com");
            client.setRequestProperty("x-rapidapi-key", "e14d9e09a5msh348b2bda8798db2p15d68cjsn1f2e7d7c5740");
            client.setRequestProperty("content-type", "application/octet-stream");
            client.setRequestMethod("GET");

            BufferedReader br = null;
            if(client.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String responseOutput = null;
                while ((responseOutput = br.readLine()) != null) {
                    strJsonResponse.append(responseOutput);
                }
            }
            else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
                String responseOutput = null;
                while ((responseOutput = br.readLine()) != null) {
                    strJsonResponse.append(responseOutput);
                }
            }
        }
        catch(MalformedURLException ex) {
            ex.printStackTrace();
        }
        Gson g = new Gson();
        RootExtraction rootJsonData = null;

        try
        {
            JsonObject jsonOBj1 = new JsonParser().parse(strJsonResponse.toString()).getAsJsonObject();
            rootJsonData = g.fromJson(jsonOBj1, RootExtraction.class);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
        }
        StringBuilder extraction =
                new StringBuilder(rootJsonData != null ? rootJsonData.text : "");
        return extraction;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public StringBuilder getUrlText() {
        if(apiUsedFlag) {
            for(Object object : apiSummary.sentences) {
                String sentenceString = (String) object;
                sb.append("• " + sentenceString + System.lineSeparator() + System.lineSeparator());
            }
            return sb;
        }
        return sb;
    }
    public boolean getApiUsedFlag() {
        return apiUsedFlag;
    }
}

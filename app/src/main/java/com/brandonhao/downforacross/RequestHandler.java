package com.brandonhao.downforacross;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

public final class RequestHandler {
    private static final String PATH = "api.foracross.com/api/puzzle_list";
    private static final String PAGE_KEY = "page";
    private static final String PAGE_SIZE_KEY = "pageSize";
    private static final String TEXT_FILTER_KEY = "filter[nameOrTitleFilter]";
    private static final String MINI_FILTER_KEY = "filter%5BsizeFilter%5D%5BMini%5D";
    private static final String NORMAL_FILTER_KEY = "filter%5BsizeFilter%5D%5BStandard%5D";
    private static final String CHAR_ENCODING = "UTF-8";
    private static final int REQUEST_TIMEOUT = 100;

    private String page = "0";
    private String pageSize = "50";
    private String textFilter = "";
    private String allowMiniPuzzles = "true";
    private String allowNormalPuzzles = "true";

    private URL query;
    private URLConnection httpConnection;
    private InputStream responseStream;
    private String responseBody;
    private int responseCode;

    public RequestHandler(){
        try {
            String queryString = String.format("%s?%s%s&%s%s&%s%s&%s%s",
                    URLEncoder.encode(PATH, CHAR_ENCODING),
                    URLEncoder.encode(PAGE_KEY, CHAR_ENCODING),
                    URLEncoder.encode(page, CHAR_ENCODING),
                    URLEncoder.encode(PAGE_SIZE_KEY, CHAR_ENCODING),
                    URLEncoder.encode(pageSize, CHAR_ENCODING),
                    URLEncoder.encode(MINI_FILTER_KEY, CHAR_ENCODING),
                    URLEncoder.encode(allowMiniPuzzles, CHAR_ENCODING),
                    URLEncoder.encode(NORMAL_FILTER_KEY, CHAR_ENCODING),
                    URLEncoder.encode(allowNormalPuzzles, CHAR_ENCODING));
            query = new URL(queryString);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void constructQuery(){
        try {
            String queryString = String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s",
                    PATH,
                    URLEncoder.encode(PAGE_KEY, CHAR_ENCODING),
                    URLEncoder.encode(this.page, CHAR_ENCODING),
                    URLEncoder.encode(PAGE_SIZE_KEY, CHAR_ENCODING),
                    URLEncoder.encode(this.pageSize, CHAR_ENCODING),
                    URLEncoder.encode(TEXT_FILTER_KEY, CHAR_ENCODING),
                    URLEncoder.encode(this.textFilter, CHAR_ENCODING),
                    URLEncoder.encode(MINI_FILTER_KEY, CHAR_ENCODING),
                    URLEncoder.encode(this.allowMiniPuzzles, CHAR_ENCODING),
                    URLEncoder.encode(NORMAL_FILTER_KEY, CHAR_ENCODING),
                    URLEncoder.encode(this.allowNormalPuzzles, CHAR_ENCODING));
            query = new URL(queryString);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void getResponse(){
        try {
            HttpURLConnection connection = (HttpURLConnection)query.openConnection();

            responseCode = connection.getResponseCode();
            if(responseCode != 200){
                return;
            }

            responseStream = connection.getInputStream();
            Scanner scanner = new Scanner(responseStream);
            responseBody = scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPuzzleList(int page, int pageSize, String textFilter, boolean allowMiniPuzzles, boolean allowNormalPuzzles){
        responseCode = 0;
        this.page = Integer.toString(page);
        this.pageSize = Integer.toString(pageSize);
        this.textFilter = textFilter;
        this.allowMiniPuzzles = (allowMiniPuzzles ? "true" : "false");
        this.allowNormalPuzzles = (allowNormalPuzzles ? "true" : "false");

        constructQuery();
        new Thread(this::getResponse).start();
    }

    public String getPuzzleListJson(){ return responseBody; }

    public InputStream getResponseStream() { return responseStream; }
}

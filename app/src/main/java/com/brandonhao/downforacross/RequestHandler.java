package com.brandonhao.downforacross;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RequestHandler{
    private static final String PATH = "https://api.foracross.com/api/puzzle_list";
    private static final String PAGE_KEY = "page";
    private static final String PAGE_SIZE_KEY = "pageSize";
    private static final String TEXT_FILTER_KEY = "filter[nameOrTitleFilter]";
    private static final String MINI_FILTER_KEY = "filter[sizeFilter][Mini]";
    private static final String NORMAL_FILTER_KEY = "filter[sizeFilter][Standard]";
    private static final String CHAR_ENCODING = "UTF-8";

    private String page;
    private String pageSize;
    private String textFilter;
    private String allowMiniPuzzles;
    private String allowNormalPuzzles;

    private URL query;
    private int responseCode;

    public AtomicBoolean threadBusy;
    String queryString;

    public RequestHandler(){
        threadBusy = new AtomicBoolean(false);
        page = "0";
        pageSize = "50";
        textFilter = "";
        allowMiniPuzzles = "true";
        allowNormalPuzzles = "true";
    }

    public RequestHandler(int page, int pageSize, String textFilter, boolean allowMiniPuzzles, boolean allowNormalPuzzles){
        threadBusy = new AtomicBoolean(false);
        this.page = Integer.toString(page);
        this.pageSize = Integer.toString(pageSize);
        this.textFilter = textFilter;
        this.allowMiniPuzzles = (allowMiniPuzzles ? "true" : "false");
        this.allowNormalPuzzles = (allowNormalPuzzles ? "true" : "false");
    }

    private void constructQuery(){
        try {
            queryString = String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
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
            Log.e("REST", e.toString());
        }
    }

    public void setQueryParams(int page, int pageSize, String textFilter, boolean allowMiniPuzzles, boolean allowNormalPuzzles){
        this.page = Integer.toString(page);
        this.pageSize = Integer.toString(pageSize);
        this.textFilter = textFilter;
        this.allowMiniPuzzles = (allowMiniPuzzles ? "true" : "false");
        this.allowNormalPuzzles = (allowNormalPuzzles ? "true" : "false");
    }

    public void nextPageQuery(){
        page = String.valueOf(Integer.parseInt(page) + 1);
    }

    public Result<String> getPuzzleList(){
        try{
            return getPuzzleListSynchronous();
        }
        catch (IOException e){
            return new Result.Error<>(e);
        }
    }

    public Result<String> getPuzzleListSynchronous() throws IOException{
        constructQuery();
        HttpURLConnection connection = (HttpURLConnection)query.openConnection();
        responseCode = connection.getResponseCode();
        InputStream stream = connection.getInputStream();
        Scanner scanner = new Scanner(stream);
        String responseBody = scanner.useDelimiter("\\A").next();
        return new Result.Success<>(responseBody);
    }
}

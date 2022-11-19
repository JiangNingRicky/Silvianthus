package com.expert.domain;

import com.expert.utils.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DocumentDomain {

    public static void main(String[] args) {
        DocumentDomain documentDomain = new DocumentDomain();
        documentDomain.call();
    }

    private void call(){
        download(new QueryParam("20210101","20220101","2000","40000","40100"));

    }

    private static final Map DEFAULT_REQUEST_HEADER = new HashMap();
    private static final String DEFAULT_REQUEST_URL = "https://www1.hkexnews.hk/search/titleSearchServlet.do?";



    private String newsId;
    private String shortText;
    private String stockName;
    private String fileType;
    private String longText;
    private String stockCode;
    private String fileLink;

    class QueryParam{
        QueryParam(String fromDate,String toDate,String recordSize,String categoryCode1,String categoryCode2){
            paraMap.put("fromDate",fromDate);
            paraMap.put("toDate",toDate);
            paraMap.put("t1code",categoryCode1);
            paraMap.put("t2code",categoryCode2);
            paraMap.put("rowRange",recordSize);
            paraMap.put("sortDir","0");
            paraMap.put("sortByOptions","DateTime");
            paraMap.put("category","0");
            paraMap.put("market","SEHK");
            paraMap.put("stockId","-1");
            paraMap.put("documentType","-1");
            paraMap.put("title",null);
            paraMap.put("searchType","1");
            paraMap.put("t2Gcode","-2");
            paraMap.put("lang","zh");
        }
        Map<String,String> paraMap = new HashMap<>();
    }

    public static void download(QueryParam queryParam){
        try {
            System.out.println(HttpClientUtil.doHttpGet(DEFAULT_REQUEST_URL,queryParam.paraMap,DEFAULT_REQUEST_HEADER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static{
        DEFAULT_REQUEST_HEADER.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36);");

    }

}

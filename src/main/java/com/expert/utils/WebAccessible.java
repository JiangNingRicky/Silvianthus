package com.expert.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface WebAccessible {

    void downloadFile(String url, File nativeFile) throws IOException;

    /**
     * 简单请求HTML页面
     *
     * @param url 请求的地址
     * @return 页面的HTML代码
     **/
    String requestPage(String url) throws IOException;


    /**
     * 携带请求头的HTTP请求
     */
    String requestPage(String url, Map<String, String> header) throws URISyntaxException, IOException;


}

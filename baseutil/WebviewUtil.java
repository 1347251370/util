package com.mxnavi.mobile.utils.baseutil;

import android.webkit.WebView;

/**
 * @zhaohj Created on 2018/12/5.
 */
public class WebviewUtil {
    public static void matchHtmlTagToShow(WebView webView, String content) {
        String style = "<style>body{background-color:#1d232b}p{word-break:break-all;color:#5b626c}img{max-width:100%;}</style>";
        webView.loadDataWithBaseURL(null, "<!doctype html>\n" +
                "<html lang=\"zh\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" + style +
                "  </head>\n" +
                "  <body>\n" +
                content +
                "  </body>\n" +
                "</html>", "text/html", "utf-8", null);
    }
}

/////////////////////////////////////////////////
// Project : Mindie WebService
// Package : com.ever.wsframework.utils
// HTMLUtils.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 3, 2013 at 7:01:42 PM
////////

package co.mindie.cindy.webservice.utils;

public class HTMLUtils {

    ////////////////////////
    // VARIABLES
    ////////////////

    ////////////////////////
    // CONSTRUCTORS
    ////////////////

    ////////////////////////
    // METHODS
    ////////////////

    public static String italic(String text) {
        return "<i>" + text + "</i>";
    }

    public static String strong(String text) {
        return "<b>" + text + "</b>";
    }

    public static String nextLine() {
        return "<br>";
    }

    public static String url(String url) {
        return url(url, url);
    }

    public static String url(String url, String name) {
        return "<a href=\"" + url + "\">" + name + "</a>";
    }

    public static String tab() {
        return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    }

    ////////////////////////
    // GETTERS/SETTERS
    ////////////////

}

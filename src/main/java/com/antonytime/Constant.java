package com.antonytime;

public class Constant {

    private static final String consumerKey = "pXX3zrDBM5rnSKTiV4PzGLAGp";
    private static final String consumerSecret = "2gvb3CQdQHWESHVa91ov5Anba8VYs39ZgmRain1st0cuyrADME";

    private static final String URL = "jdbc:mysql://localhost:3306/followers";
    private static final String USERNAME = "root";
    private static final String USERPAWWSORD = "root";
    private static final String QUERYGETFOLLOWERS = "REPLACE INTO followers VALUES(?,?)";

    static String getConsumerKey() {
        return consumerKey;
    }

    static String getConsumerSecret() {
        return consumerSecret;
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getUserpawwsord() {
        return USERPAWWSORD;
    }

    public static String getQuerygetfollowers() {
        return QUERYGETFOLLOWERS;
    }
}

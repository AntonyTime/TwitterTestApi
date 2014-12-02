package com.antonytime;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.*;

public class TestApi {



    Twitter twitter = TwitterFactory.getSingleton();

    public void logIn() throws Exception {

        twitter.setOAuthConsumer(Constant.getConsumerKey(), Constant.getConsumerSecret());
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    System.out.println("Successfully LogIn!");
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }
        }
    }

    public void getFollowers() throws Exception{

        long lCursor = -1;
        long userID = 1260205219;
        IDs followersIDs = twitter.getFollowersIDs(userID, lCursor);

//        System.out.println("==========================");
//        System.out.println(twitter.showUser(userID).getName());
//        System.out.println("Followers: " + twitter.showUser(userID).getFollowersCount());
//        System.out.println("==========================");

        do{
            for (long i : followersIDs.getIDs()){

                Driver driver = new FabricMySQLDriver();
                DriverManager.registerDriver(driver);
                Connection connection = DriverManager.getConnection(Constant.getUrl(), Constant.getUsername(), Constant.getUserpawwsord());

                PreparedStatement preparedStatement = connection.prepareStatement(Constant.getQuerygetfollowers());

                preparedStatement.setLong(1,i);
                preparedStatement.setString(2,twitter.showUser(i).getName());

                preparedStatement.executeUpdate();

//                System.out.println("Name: " + twitter.showUser(i).getName());
//                System.out.println("Follower ID: " + i);
//                System.out.println("==========================");
            }
        } while(followersIDs.hasNext());

    }

}



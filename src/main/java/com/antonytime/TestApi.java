package com.antonytime;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;


import com.mysql.fabric.jdbc.FabricMySQLDriver;

public class TestApi {

    Twitter twitter = TwitterFactory.getSingleton();

    ArrayList<Long> followersListFromDataBase = new ArrayList <Long>();
    ArrayList<Long> followersListFromServer = new ArrayList <Long>();

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

    public Connection connectDataBase() throws Exception{

        Driver driver = new FabricMySQLDriver();
        DriverManager.registerDriver(driver);
        return DriverManager.getConnection(Constant.getUrl(), Constant.getUsername(), Constant.getUserpawwsord());
    }

    public void getFollowers() throws Exception{

        PreparedStatement preparedStatementFollowers = connectDataBase().prepareStatement(Constant.getReplaceIntoFollowersValues());

        long userID = twitter.getId();
        long cursor = -1;
        IDs followersIDs = twitter.getFollowersIDs(userID, cursor);

        do {
            for (long i : followersIDs.getIDs())
            {
                preparedStatementFollowers.setLong(1, i);
                preparedStatementFollowers.executeUpdate();
            }

        } while (followersIDs.hasNext());
    }

    public void getFollowersCollection() throws Exception{

        long userID = twitter.getId();
        long cursor = -1;
        IDs followersIDs = twitter.getFollowersIDs(userID, cursor);

        do
        {
            for (long i : followersIDs.getIDs())
            {
                followersListFromServer.add(i);
            }

        } while(followersIDs.hasNext());

        System.out.println("Col" + followersListFromServer);

    }

    public void getResult() throws Exception{

        PreparedStatement preparedStatementFollowers = connectDataBase().prepareStatement(Constant.getReplaceIntoFollowersValues());
        PreparedStatement preparedStatementUnFollowers = connectDataBase().prepareStatement(Constant.getReplaceIntoUnfollowersValues());

        ResultSet resultset = preparedStatementFollowers.executeQuery("select * from followers");

        while (resultset.next()) {
                followersListFromDataBase.add(resultset.getLong(1));
        }

        System.out.println("DB" + followersListFromDataBase);

        followersListFromDataBase.removeAll(followersListFromServer);

        System.out.println(followersListFromDataBase);

        for (long i : followersListFromDataBase)
        {
            preparedStatementUnFollowers.setLong(1, i);
            preparedStatementUnFollowers.executeUpdate();
        }

    }

}



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


public class TestApi {

    Twitter twitter = TwitterFactory.getSingleton();

    ArrayList<Long> followersListFromDataBase = new ArrayList <Long>();
    ArrayList<Long> followersListFromServer = new ArrayList <Long>();

    public static Statement statement;
    public static ResultSet resultSet;

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

    public static Connection CreateDB() throws ClassNotFoundException, SQLException
    {
        Connection connection;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:DataBase.db");

        System.out.println("DataBase connect!");

        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'followers' ('id'INTEGER PRIMARY KEY NOT NULL );");
        statement.execute("CREATE TABLE if not exists 'unfollowers' ('id' INTEGER PRIMARY KEY NOT NULL);");

        System.out.println("Table created or already exist.");

        return connection;
    }

    public void getFollowersDataBase() throws Exception{

        PreparedStatement preparedStatementFollowers = CreateDB().prepareStatement("REPLACE INTO followers VALUES(?);");


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

        System.out.println("ArrayList followersListFromServer: " + followersListFromServer);

    }

    public void getResult() throws Exception{

        PreparedStatement preparedStatementUnFollowers = CreateDB().prepareStatement("REPLACE INTO unfollowers VALUES(?);");

        resultSet = statement.executeQuery("SELECT * FROM followers");

        while (resultSet.next()) {
            followersListFromDataBase.add(resultSet.getLong(1));
        }

        System.out.println("ArrayList followersListFromDataBase" + followersListFromDataBase);

        followersListFromDataBase.removeAll(followersListFromServer);

        System.out.println(followersListFromDataBase);

        for (long i : followersListFromDataBase)
        {
            preparedStatementUnFollowers.setLong(1, i);
            preparedStatementUnFollowers.executeUpdate();

            System.out.println(twitter.showUser(i).getName());
        }

    }

}



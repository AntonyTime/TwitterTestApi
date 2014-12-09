package com.antonytime;

import org.omg.CORBA.PUBLIC_MEMBER;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.*;

public class TestApi {
    Twitter twitter = TwitterFactory.getSingleton();

    public  Connection connection;
    public  Statement statement;
    public static final String FOLLOWERS_TABLE = "followers";
    public static final String UNFOLLOWERS_TABLE = "unfollowers";

    public TestApi() throws SQLException, ClassNotFoundException {
        createDB();
    }

    public void login() throws Exception {

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
                    System.out.println("Successfully Login!");
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

    public void createDB() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:DataBase.db");

        System.out.println("DataBase connect!");

        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'followers' ('id'INTEGER PRIMARY KEY NOT NULL );");
        statement.execute("CREATE TABLE if not exists 'unfollowers' ('id' INTEGER PRIMARY KEY NOT NULL);");

        System.out.println("Table created or already exist.");
    }

    public IDs getFollewersIDs() throws TwitterException {
        long userID = twitter.getId();
        long cursor = -1;
        return twitter.getFollowersIDs(userID, cursor);
    }

    public List<Long> getFollowersFromServer() throws Exception {
        IDs followersIDs = getFollewersIDs();
        List<Long> followersListFromServer = new ArrayList<Long>();

        for (long i : followersIDs.getIDs())
        {
            followersListFromServer.add(i);
        }

        return followersListFromServer;
    }

    public List<Long> getFollowersFromDB() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM followers");
        List<Long> followersListFromDataBase = new ArrayList<Long>();

        while (resultSet.next()) {
            followersListFromDataBase.add(resultSet.getLong(1));
        }

        return followersListFromDataBase;
    }

    public void findUnfollowers(List<Long> followersListFromDataBase, List<Long> followersListFromServer) throws Exception {
        followersListFromDataBase.removeAll(followersListFromServer);

        System.out.println(followersListFromDataBase);
    }

    public void saveToDB(List<Long> dataList, String tableName) throws Exception {
        String deleteQuery = String.format("DELETE FROM %s", tableName);
        connection.prepareStatement(deleteQuery).execute();

        String query = String.format("INSERT INTO %s VALUES(?)", tableName);
        PreparedStatement preparedStatementFollowers = connection.prepareStatement(query);

        for (long i : dataList)
        {
            preparedStatementFollowers.setLong(1, i);
            preparedStatementFollowers.executeUpdate();
        }
    }
}



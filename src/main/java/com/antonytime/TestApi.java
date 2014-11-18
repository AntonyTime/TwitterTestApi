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

    public void showFollowers() throws Exception{

    long lCursor = -1;
    long userID = 1260205219;
    IDs friendsIDs = twitter.getFriendsIDs(userID, lCursor);

    System.out.println("==========================");
    System.out.println(twitter.showUser(userID).getName());
    System.out.println("Followers: " + twitter.showUser(userID).getFollowersCount());
    System.out.println("==========================");

    do{
        for (long i : friendsIDs.getIDs()){
            System.out.println("Name: " + twitter.showUser(i).getName());
            System.out.println("Follower ID: " + i);
            System.out.println("==========================");
        }
    } while(friendsIDs.hasNext());

    }

}



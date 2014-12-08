package com.antonytime;

import twitter4j.IDs;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        TestApi testApi = new TestApi();

        testApi.login();

        List<Long> followersListFromDataBase = testApi.getFollowersFromDB();
        if (followersListFromDataBase.isEmpty())
            System.out.println("Can't determine unfollowers!");

        List<Long> followersListFromServer = testApi.getFollowersFromServer();
        if (followersListFromServer.isEmpty())
            System.out.println("Can't determine unfollowers!");

        testApi.findUnfollowers(followersListFromDataBase, followersListFromServer);

        testApi.saveFollowersToDB(followersListFromServer);
        testApi.saveUnfollowersToDB(followersListFromDataBase);
    }

}

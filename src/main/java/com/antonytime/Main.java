package com.antonytime;

public class Main {

    public static void main(String[] args) throws Exception {

        TestApi testApi = new TestApi();

        testApi.logIn();

        testApi.getFollowers();

        testApi.getFollowersCollection();

        testApi.getResult();
    }

}

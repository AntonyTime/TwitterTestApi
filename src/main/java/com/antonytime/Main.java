package com.antonytime;

public class Main {

    public static void main(String[] args){

        TestApi testApi = new TestApi();

        try {
            testApi.logIn();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            testApi.getFollowers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

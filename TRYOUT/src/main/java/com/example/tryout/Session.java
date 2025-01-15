package com.example.tryout;

public class Session {
    private static String loggedUserCNP;

    public static String getLoggedUserCNP() {
        return loggedUserCNP;
    }

    public static void setLoggedUserCNP(String cnp) {
        loggedUserCNP = cnp;
    }
}

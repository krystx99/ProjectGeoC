package com.bpmskm.projectgeoc;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static User currentUser;
    private static List<User> topTenUsers = new ArrayList<>();

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static List<User> getTopTenUsers() {
        return topTenUsers;
    }

    public static void setTopTenUsers(List<User> users) {
        if (users != null) {
            topTenUsers = new ArrayList<>(users);
        } else {
            topTenUsers = new ArrayList<>();
        }
    }

    public static void clearTopTenUsers() {
        topTenUsers.clear();
    }
}
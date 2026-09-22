package com.venturelens.model;

/**
 * Thread-safe singleton session tracking the currently logged-in user in VentureLens.
 */
public class UserSession {
    private static volatile UserSession instance;
    private User currentUser;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public synchronized void login(User user) {
        this.currentUser = user;
    }

    public synchronized void logout() {
        this.currentUser = null;
    }

    public synchronized boolean isLoggedIn() {
        return this.currentUser != null;
    }

    public synchronized User getCurrentUser() {
        return this.currentUser;
    }
}

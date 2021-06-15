package rs.ac.bg.etf.running.users;

import android.app.Activity;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.User;

public class Session {
    private static User currentUser;
    private static Playlist currentPlaylist;
    private static Activity mainActivity;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }

    public static Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public static void setCurrentPlaylist(Playlist currentPlaylist) {
        Session.currentPlaylist = currentPlaylist;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        Session.mainActivity = mainActivity;
    }

    public static Activity getMainActivity() {
        return mainActivity;
    }
}
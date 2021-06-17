package rs.ac.bg.etf.running.users;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.concurrent.ExecutionException;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.login.LoginFragment;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;

public class Session {
    private static User currentUser;
    private static Playlist currentPlaylist;
    private static Activity mainActivity;
    private static CurrentWeatherModel currentWeatherModel;

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

    public static void setWeatherModel(CurrentWeatherModel currentWeatherModel) {
        Session.currentWeatherModel = currentWeatherModel;
    }

    public static CurrentWeatherModel getCurrentWeatherModel() {
        return currentWeatherModel;
    }
}

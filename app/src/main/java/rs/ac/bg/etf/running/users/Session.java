package rs.ac.bg.etf.running.users;

import android.app.Activity;

import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;

public class Session {
    private static User currentUser;
    private static Playlist currentPlaylist;
    private static Activity mainActivity; // never used
    private static String currentSong;
    private static CurrentWeatherModel currentWeatherModel;
    private static int currentSteps = 0;
    private static List<Location> currentLocations;

    public static void setMainActivity(Activity mainActivity) {
        Session.mainActivity = mainActivity;
    }

    public static String getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(String currentSong) {
        Session.currentSong = currentSong;
    }

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
        return Session.currentWeatherModel;
    }

    public static void setCurrentSteps(int stepsNum) {
        Session.currentSteps = stepsNum;
    }
    public static int getCurrentSteps() {
        return Session.currentSteps;
    }

    public static void setCurrentLocations(List<Location> l) {
        Session.currentLocations = l;
    }

    public static List<Location> getCurrentLocations(){
        return Session.currentLocations;
    }

}

package rs.ac.bg.etf.running.workouts;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.users.Session;

@AndroidEntryPoint
public class WorkoutService extends LifecycleService {

    public static final String INTENT_ACTION_START_TRAINING = "rs.ac.bg.etf.running.workouts.START";
    public static final String INTENT_ACTION_POWER = "rs.ac.bg.etf.running.workouts.POWER";
    public static final String INTENT_ACTION_LOCATION = "rs.ac.bg.etf.running.workouts.LOCATION";

    private static final String NOTIFICATION_CHANNEL_ID = "workout-notification-channel";
    private static final int NOTIFICATION_ID = 1;
    private static Context context;

    private boolean serviceStarted = false;

    static int indexSongs = 1;
    public static WorkoutService staticService;
    private Timer timer;

    public static int getIndexSongs() {
        return indexSongs;
    }

    public static void setIndexSongs(int indexSongs) {
        WorkoutService.indexSongs = indexSongs;
    }

    @Inject
    public LifecycleAwareMotivator motivator;

    @Inject
    public LifecycleAwarePlayer player;

    @Inject
    public LifecycleAwareMeasurer measurer;

    @Inject
    public LifecycleAwareLocator locator;

    @Inject
    public LifecycleAwareStepCounter stepCounter;

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();

        timer = new Timer();

     //   getLifecycle().addObserver(motivator);
        getLifecycle().addObserver(player);
        getLifecycle().addObserver(measurer);
        getLifecycle().addObserver(locator);
        getLifecycle().addObserver(stepCounter);

        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");
        if (intent.getAction() == INTENT_ACTION_START_TRAINING) {
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, getNotification());
        }
        switch (intent.getAction()) {
            case INTENT_ACTION_START_TRAINING:
                if (!serviceStarted) {
                    serviceStarted = true;
                  //  motivator.start(this);
                    player.start(this);
                    measurer.start(this);
                    locator.getLocation(this);
                    stepCounter.start(this);
                }
                player.getMediaPlayer().setOnCompletionListener(mp -> {
                    player.getMediaPlayer().reset();
                    String songs = Session.getCurrentPlaylist().getMusicList();
                    String[] songsSplit = songs.split("/");
                    File filesDir = Session.getMainActivity().getFilesDir();
                    int indexLambda = 0;
                    String songForShow = "";
                    if(indexSongs < songsSplit.length) {
                        int num = Integer.parseInt(songsSplit[indexSongs]);
                        for (String strFile : filesDir.list()) {
                            if (num == indexLambda) {
                                try {
                                    songForShow = strFile;
                                    player.getMediaPlayer().setDataSource(filesDir.getAbsolutePath() + File.separator + strFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            indexLambda++;
                        }
                        player.getMediaPlayer().setOnPreparedListener(mp1 -> {
                            int duration = player.getMediaPlayer().getDuration();
                            player.getMediaPlayer().start();

                            int seconds = ((duration / 1000) % 60);
                            int minutes = ((duration / (1000 * 60)) % 60);

                            StringBuilder remaining = new StringBuilder();
                            remaining.append(String.format("%02d", minutes)).append(":");
                            remaining.append(String.format("%02d", seconds));

                            TextView tw = Session.getMainActivity().findViewById(R.id.remaining);
                            tw.setText(remaining);
                        });
                        player.getMediaPlayer().prepareAsync();

                        TextView tw = Session.getMainActivity().findViewById(R.id.current_playlist);
                        tw.setText(getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
                        tw = Session.getMainActivity().findViewById(R.id.current_song);
                        tw.setText(getResources().getString(R.string.no_song) + " " + songForShow);

                        indexSongs++;
                    }
                    else
                    {
                        //MainActivity.setMusicPlaying(0);
                        indexSongs = 1;
                        TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.current_song);
                        tw.setText(getResources().getString(R.string.no_song));
                        player.getMediaPlayer().release();
                    }
                });
                break;
            case INTENT_ACTION_POWER:
                if (serviceStarted) {
                 //   motivator.changeMessage(this);
                }
                break;
            case INTENT_ACTION_LOCATION:
//                setStaticService(this);
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        locator.getLocation(WorkoutService.getStaticService());
//                    }
//                }, 0, 5000);
                locator.getLocation(this);

                return START_STICKY;
        }

        return START_REDELIVER_INTENT;
    }

    private void setStaticService(WorkoutService workoutService) {
        staticService = workoutService;
    }

    public static WorkoutService getStaticService(){
        return staticService;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.workout_notification_channel_name))
                .build();
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
    }

    private Notification getNotification() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setAction(MainActivity.INTENT_ACTION_WORKOUT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_directions_run_black_24)
                .setContentTitle(getString(R.string.workout_notification_content_title))
                .setContentText(getString(R.string.workout_notification_content_text))
                .setContentIntent(pendingIntent)
                .setColorized(true)
                .setColor(ContextCompat.getColor(this, R.color.teal_200))
                .build();
    }
}

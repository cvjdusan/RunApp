package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;
import rs.ac.bg.etf.running.location.LocationViewModel;
import rs.ac.bg.etf.running.users.Session;

@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    public static final String START_TIMESTAMP_KEY = "start-timestamp-key";
    public static final String CURRENT_DURATION_KEY = "current-duration-key";
    public static final String CURRENT_START_KEY = "current-start-key";

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private LocationViewModel locationViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private Timer timer, songTimer;
    private boolean isSongPlaying = true;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver updateReceiver;

    public static int currentIndexSong;
    private String currentSongDuration;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if (isPermissionGranted) {
                           // startWorkout(new Date().getTime());
                        }
                    });

    private boolean notPaused = true;

    public WorkoutStartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        locationViewModel = new ViewModelProvider(mainActivity).get(LocationViewModel.class);

        sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();

        filter.addAction("stepCounter");
        filter.addAction("startMusic");

        updateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("stepCounter")) {
                    String stepsString = binding.steps.getText().toString();
                    int stepsNum = Integer.parseInt(stepsString.split(":")[1].substring(1));
                    stepsNum++;
                    binding.steps.setText("Steps: " + stepsNum);
                    Session.setCurrentSteps(stepsNum);
                }
                else if(action.equals("startMusic")){
                    String songName = intent.getStringExtra("songName");
                    String songDuration = intent.getStringExtra("songDuration");
                    if(songName != null && songDuration != null) {
                        currentSongDuration = songDuration;
                        binding.remaining.setText(songDuration);
                        binding.currentPlaylist.setText(mainActivity.getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
                        binding.currentSong.setText(mainActivity.getResources().getString(R.string.no_song) + " " + songName);
                    }
                }
            }
        };

        mainActivity.registerReceiver(updateReceiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutStartBinding.inflate(inflater, container, false);

        timer = new Timer();
        songTimer = new Timer();

        if(Session.getCurrentSong() != null) {
            if(isSongPlaying) {

                if(sharedPreferences.contains(CURRENT_START_KEY))
                    timeStart = sharedPreferences.getLong(CURRENT_START_KEY, timeStart);

                long timeLapsed = System.currentTimeMillis() - timeStart;
                int minutes = (int) ((timeLapsed / 1000) / 60);
                int sec = (int) ((timeLapsed / 1000) % 60);
                String time = sharedPreferences.getString(CURRENT_DURATION_KEY, "");
                String[] helper = time.split(":");
                int minsLeft = Integer.parseInt(helper[0]);
                int secLeft = Integer.parseInt(helper[1]);

                int total = minsLeft * 60 + secLeft;
                int total2 = minutes * 60 + sec;

                if (total - total2 >= 0) {
                    total = total - total2;
                }

                minsLeft = total / 60;
                secLeft = total % 60;

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", minsLeft)).append(":");
                remaining.append(String.format("%02d", secLeft));

                binding.remaining.setText(remaining);
            } else
                binding.remaining.setText(sharedPreferences.getString(CURRENT_DURATION_KEY, ""));
            notPaused = true;
        }


        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()));
        }

        binding.start.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(
                    mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                if(ActivityCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                } else {
                    startWorkout(new Date().getTime());
                }
            }

        });

        binding.finish.setOnClickListener(view -> finishWorkout());
        binding.cancel.setOnClickListener(view -> cancelWorkout());
        binding.pause.setOnClickListener(view -> {
            MediaPlayer m = LifecycleAwarePlayer.getMediaPlayer();
            if(m != null) {
                if (m.isPlaying()) {
                    m.pause();
                    isSongPlaying = false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CURRENT_DURATION_KEY, binding.remaining.getText().toString());
                    editor.commit();
                }
                else {
                    m.start();
                    isSongPlaying = true;
                }
            }
        });

        binding.next.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();
            if(mediaPlayer != null) {
                String songs = Session.getCurrentPlaylist().getMusicListPositions();
                String[] allSongs = songs.split("/");
                currentIndexSong = WorkoutService.getCurrentIndexSongs();
                if (currentIndexSong >= allSongs.length) {
                    Toast.makeText(mainActivity, "There is no next song.", Toast.LENGTH_SHORT).show();
                } else {
                    WorkoutService.setCurrentIndexSongs(currentIndexSong + 1);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    playSong(mediaPlayer, allSongs, songs);
                }
            }
        });

        binding.previous.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();
            if(mediaPlayer != null) {
                String songs = Session.getCurrentPlaylist().getMusicListPositions();
                String[] allSongs = songs.split("/");
                currentIndexSong = WorkoutService.getCurrentIndexSongs();
                if (currentIndexSong <= 1) {
                    Toast.makeText(mainActivity, "This is first song.", Toast.LENGTH_SHORT).show();
                } else {
                    WorkoutService.setCurrentIndexSongs(currentIndexSong - 1);
                    currentIndexSong -= 2;
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    playSong(mediaPlayer, allSongs, songs);
                }
            }
        });

        mainActivity.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        stopWorkout();
                    }
                });

        return binding.getRoot();
    }



    private void playSong(MediaPlayer mediaPlayer, String[] allSongs, String songs) {
        String song = "";

        String[] names = Session.getCurrentPlaylist().getMusicListNames().split("/");
        song = names[currentIndexSong];

        // int songPosition = Integer.parseInt(allSongs[currentIndexSong]);
        //        int currentPosition = 0;
        //        for (String strFile : Objects.requireNonNull(mainActivity.getFilesDir().list())) {
        //            if (songPosition == currentPosition) {
        //                song = strFile;
        //                break;
        //            }
        //            else
        //                currentPosition++;
        //        }

        String path = mainActivity.getFilesDir().getAbsolutePath() + File.separator + song;

        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            int duration = mediaPlayer.getDuration();
            mediaPlayer.start();

            int seconds = (duration / 1000) % 60;
            int minutes = (duration / (1000 * 60)) % 60;

            StringBuilder remaining = new StringBuilder();
            remaining.append(String.format("%02d", minutes)).append(":");
            remaining.append(String.format("%02d", seconds));

            binding.remaining.setText(remaining);
        });
        mediaPlayer.prepareAsync();
        String label = mainActivity.getResources().getString(R.string.no_playlist);
        binding.currentPlaylist.setText(label + " " + Session.getCurrentPlaylist().getName());
        label = Session.getMainActivity().getResources().getString(R.string.no_song);
        binding.currentSong.setText(label + " " + song);
        Session.setCurrentSong(song);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
        songTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.unregisterReceiver(updateReceiver);
    }

    private void startWorkout(long startTimestamp) {
        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
        binding.cancel.setEnabled(true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(START_TIMESTAMP_KEY, startTimestamp);
        editor.commit();

        if(Session.getCurrentSteps() != 0){
            binding.steps.setText("Steps: " + Session.getCurrentSteps());
        }

        if(Session.getCurrentSong() != null) {
            binding.currentPlaylist.setText(mainActivity.getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
            binding.currentSong.setText(mainActivity.getResources().getString((R.string.no_song)) + " " + Session.getCurrentSong());
        }

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = new Date().getTime() - startTimestamp;

                int miliseconds = (int) ((elapsed % 1000) / 10);
                int seconds = (int) ((elapsed / 1000) % 60);
                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

                StringBuilder workoutDuration = new StringBuilder();
                workoutDuration.append(String.format("%02d", hours)).append(":");
                workoutDuration.append(String.format("%02d", minutes)).append(":");
                workoutDuration.append(String.format("%02d", seconds)).append(".");
                workoutDuration.append(String.format("%02d", miliseconds));

                handler.post(() -> binding.workoutDuration.setText(workoutDuration));
            }
        }, 0, 10);


        songTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(notPaused) {
                    String label = binding.remaining.getText().toString();

                    String[] helper = label.split(":");

                    int minutesLeft = Integer.parseInt(helper[0]);
                    int secondsLeft = Integer.parseInt(helper[1]);

                    if(isSongPlaying) {
                        if (!(minutesLeft == 0 && secondsLeft == 0)) {
                            if (secondsLeft == 0) {
                                minutesLeft--;
                                secondsLeft = 60;
                            }
                            secondsLeft--;
                        }
                    }

                    StringBuilder remainingNew = new StringBuilder();
                    remainingNew.append(String.format("%02d", minutesLeft)).append(":");
                    remainingNew.append(String.format("%02d", secondsLeft));

                    handler.post(() -> {
                        binding.remaining.setText(remainingNew);
                        //currentSongDuration = remaining;
                    });
                }
            }
        }, 0, 1000);

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        intent.setAction(WorkoutService.INTENT_ACTION_START_TRAINING);
        LifecycleAwareLocator.allocateLocationsList();
        mainActivity.startService(intent);
    }

    long timeStart = 0;
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(Session.getCurrentSong() != null) {
//            if(isSongPlaying) {
//                long timeLapsed = System.currentTimeMillis() - timeStart;
//                int minutes = (int) ((timeLapsed / 1000) / 60);
//                int sec = (int) ((timeLapsed / 1000) % 60);
//                String time = sharedPreferences.getString(CURRENT_DURATION_KEY, "");
//                String[] helper = time.split(":");
//                int minsLeft = Integer.parseInt(helper[0]);
//                int secLeft = Integer.parseInt(helper[1]);
//
//                int total = minsLeft * 60 + secLeft;
//                int total2 = minutes * 60 + sec;
//
//                if (total - total2 >= 0) {
//                    total = total - total2;
//                }
//
//                minsLeft = total / 60;
//                secLeft = total % 60;
//
//                StringBuilder remaining = new StringBuilder();
//                remaining.append(String.format("%02d", minsLeft)).append(":");
//                remaining.append(String.format("%02d", secLeft));
//
//                binding.remaining.setText(remaining);
//            } else
//                binding.remaining.setText(sharedPreferences.getString(CURRENT_DURATION_KEY, ""));
//            notPaused = true;
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        if(Session.getCurrentSong() != null) {
            timeStart = System.currentTimeMillis();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENT_DURATION_KEY, binding.remaining.getText().toString());
            editor.commit();
            editor.putLong(CURRENT_START_KEY, timeStart);
            editor.commit();
            notPaused = false;
        }
    }

    private void finishWorkout() {
        String username = Session.getCurrentUser().getUsername();
        Workout lastInsertedWorkout = null;
        try {
            lastInsertedWorkout = getLastInserted(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        String s = binding.steps.getText().toString();
        int steps = Integer.parseInt(s.split(":")[1].substring(1));
        Workout w = new Workout(
                0,
                new Date(),
                getText(R.string.workout_label).toString(),
                0.2 * minutes,
                minutes,
                steps,
                Session.getCurrentUser().getUsername());
        workoutViewModel.insertWorkout(w
        );

        // add location

        long id = 1;
        if(lastInsertedWorkout != null)
            id = lastInsertedWorkout.getId() + 1;

        ArrayList<Double> latitudeList = LifecycleAwareLocator.getLatitudeList();
        ArrayList<Double> longitudeList = LifecycleAwareLocator.getLongitudeList();
            for(int i = 0; i < latitudeList.size(); i++) {
                locationViewModel.insertLocation(new Location(
                        0,
                        latitudeList.get(i),
                        longitudeList.get(i),
                        id,
                        username
                ));
            }


        Toast.makeText(mainActivity, id + " last" + w.getId(), Toast.LENGTH_SHORT).show();

        LifecycleAwareLocator.allocateLocationsList();

        stopWorkout();
    }

    private void cancelWorkout() {
        stopWorkout();
    }

    private void stopWorkout() {
        Session.setCurrentSteps(0);

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        sharedPreferences.edit().remove(CURRENT_DURATION_KEY).commit();
        sharedPreferences.edit().remove(CURRENT_START_KEY).commit();
        WorkoutService.stopTimerLocations();
        navController.navigateUp();
    }


    private Workout getLastInserted(String username) throws ExecutionException, InterruptedException {
        Callable<Workout> callable = () -> workoutViewModel.getLastInsertedFromUser();

        Future<Workout> future = Executors.newFixedThreadPool(4).submit(callable);

        return future.get();
    }
}

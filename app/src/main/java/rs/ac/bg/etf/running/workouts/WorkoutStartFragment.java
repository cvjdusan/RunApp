package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;
import rs.ac.bg.etf.running.users.Session;

@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {

    private static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    private static final String START_TIMESTAMP_KEY = "start-timestamp-key";
    private static final String CURRENT_DURATION_KEY = "current-duration-key";

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private Timer timer, songTimer;
    private boolean isSongPlaying = true;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver updateReceiver;

    static int indexSongs;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        //TODO: Ovo mozda srediti da ne mora da se klikne
                        if (isPermissionGranted) {
                            startWorkout(new Date().getTime());
                        }
                    });

    public WorkoutStartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

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


        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            if(Session.getCurrentSong() != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(CURRENT_DURATION_KEY, new Date().getTime());
                editor.commit();
            }
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
                }
                else {
                    m.start();
                    isSongPlaying = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(CURRENT_DURATION_KEY, new Date().getTime());
                    editor.commit();
                }
            }
        });

        binding.next.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();
            if(mediaPlayer != null) {
                String songs = Session.getCurrentPlaylist().getMusicListPositions();
                String[] songsSplit = songs.split("/");
                indexSongs = WorkoutService.getCurrentIndexSongs();

                if (indexSongs >= songsSplit.length) {
                    Toast.makeText(mainActivity, "There is no next song.", Toast.LENGTH_SHORT).show();
                } else {
                    WorkoutService.setCurrentIndexSongs(indexSongs + 1);
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    String song = "";
                    int num = Integer.parseInt(songsSplit[indexSongs]);
                    int index = 0;
                    for (String strFile : mainActivity.getFilesDir().list()) {
                        if (num == index)
                            song = strFile;
                        index++;
                    }
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
            }
        });

        binding.previous.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();
            if(mediaPlayer != null) {
                String songs = Session.getCurrentPlaylist().getMusicListPositions();
                String[] songsSplit = songs.split("/");
                indexSongs = WorkoutService.getCurrentIndexSongs();
                if (indexSongs <= 1) {
                    Toast.makeText(mainActivity, "This is first song.", Toast.LENGTH_SHORT).show();
                } else {
                    WorkoutService.setCurrentIndexSongs(indexSongs - 1);

                    indexSongs -= 2;

                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    String song = "";
                    int num = Integer.parseInt(songsSplit[indexSongs]);
                    int index = 0;
                    for (String strFile : mainActivity.getFilesDir().list()) {
                        if (num == index)
                            song = strFile;
                        index++;
                    }
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

        long startTimestampSong = 0;
        startTimestampSong = sharedPreferences.getLong(CURRENT_DURATION_KEY, new Date().getTime());

        if(Session.getCurrentSong() != null) {
            long finalStartTimestampSong = startTimestampSong;
            songTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isSongPlaying) {
//                        String remainingString = binding.remaining.getText().toString();
//
//                        String[] units = remainingString.split(":");
//
//                        int remainingMinutes = Integer.parseInt(units[0]);
//                        int remainingSeconds = Integer.parseInt(units[1]);
//
//                        if (!(remainingMinutes == 0 && remainingSeconds == 0) && isSongPlaying) {
//                            if (remainingSeconds == 0) {
//                                remainingMinutes--;
//                                remainingSeconds = 60;
//                            }
//                            remainingSeconds--;
//                        }
//
//                        StringBuilder remaining = new StringBuilder();
//                        remaining.append(String.format("%02d", remainingMinutes)).append(":");
//                        remaining.append(String.format("%02d", remainingSeconds));
//
//                        handler.post(() -> binding.remaining.setText(remaining));

//                        long elapsed = new Date().getTime() - sharedPreferences.getLong(CURRENT_DURATION_KEY, new Date().getTime());
//                        int seconds = (int) ((elapsed / 1000) % 60);
//                        int minutes = (int) ((elapsed / (1000 * 60)) % 60);
//                        StringBuilder remaining = new StringBuilder();
//                        remaining.append(String.format("%02d", minutes)).append(":");
//                        remaining.append(String.format("%02d", seconds));
//
//                        handler.post(() -> binding.remaining.setText(remaining));
                    }
                }
            }, 0, 1000);
        }

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        intent.setAction(WorkoutService.INTENT_ACTION_START_TRAINING);
        mainActivity.startService(intent);
    }

    private void finishWorkout() {
        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        String s = binding.steps.getText().toString();
        int steps = Integer.parseInt(s.split(":")[1].substring(1));
        workoutViewModel.insertWorkout(new Workout(
                0,
                new Date(),
                getText(R.string.workout_label).toString(),
                0.2 * minutes,
                minutes,
                steps,
                Session.getCurrentUser().getUsername()
        ));

        // add location

        stopWorkout();
    }

    private void cancelWorkout() {
        stopWorkout();
    }

    private void stopWorkout() {
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        navController.navigateUp();
    }

}

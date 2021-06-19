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

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private Timer timer;
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

        sharedPreferences = mainActivity
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();

        filter.addAction("stepCounter");

        updateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = mainActivity.findViewById(R.id.steps);
                String stepsString = textView.getText().toString();
                int stepsNum = Integer.parseInt(stepsString.split(":")[1].substring(1));
                stepsNum++;
                textView.setText("Steps: " + stepsNum);
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
            if(m.isPlaying())
                m.pause();
            else
                m.start();
        });

        binding.next.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();

            String songs = Session.getCurrentPlaylist().getMusicList();
            String[] songsSplit = songs.split("/");
            indexSongs = WorkoutService.getIndexSongs();
            if (indexSongs >= songsSplit.length) {
                Toast.makeText(mainActivity, "This is the last song!", Toast.LENGTH_SHORT).show();
                return;
            }
            WorkoutService.setIndexSongs(indexSongs + 1);

            mediaPlayer.stop();
            mediaPlayer.reset();
            String song = "";
            int num = Integer.parseInt(songsSplit[indexSongs]);
            int index = 0;
            for (String strFile : mainActivity.getFilesDir().list()) {
                if (num == index) song = strFile;
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

                int seconds = (int) ((duration / 1000) % 60);
                int minutes = (int) ((duration / (1000 * 60)) % 60);

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", minutes)).append(":");
                remaining.append(String.format("%02d", seconds));

                TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.remaining);
                tw.setText(remaining);
            });
            mediaPlayer.prepareAsync();

            TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.current_playlist);
            tw.setText(Session.getMainActivity().getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
            tw = (TextView) Session.getMainActivity().findViewById(R.id.current_song);
            tw.setText(Session.getMainActivity().getResources().getString(R.string.no_song) + " " + song);
        });

        binding.previous.setOnClickListener(view -> {
          //  if (MainActivity.getMusicPlaying() == 0) return;
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getMediaPlayer();

            String songs = Session.getCurrentPlaylist().getMusicList();
            String[] songsSplit = songs.split("/");
            indexSongs = WorkoutService.getIndexSongs();
            if (indexSongs <= 1) {
                Toast.makeText(mainActivity, "This is the first song!", Toast.LENGTH_SHORT).show();
                return;
            }
            WorkoutService.setIndexSongs(indexSongs - 1);

            indexSongs -= 2;

            mediaPlayer.stop();
            mediaPlayer.reset();
            String song = "";
            int num = Integer.parseInt(songsSplit[indexSongs]);
            int index = 0;
            for (String strFile : mainActivity.getFilesDir().list()) {
                if (num == index) song = strFile;
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

                int seconds = (int) ((duration / 1000) % 60);
                int minutes = (int) ((duration / (1000 * 60)) % 60);

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", minutes)).append(":");
                remaining.append(String.format("%02d", seconds));

                TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.remaining);
                tw.setText(remaining);
            });
            mediaPlayer.prepareAsync();

            TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.current_playlist);
            tw.setText(Session.getMainActivity().getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
            tw = (TextView) Session.getMainActivity().findViewById(R.id.current_song);
            tw.setText(Session.getMainActivity().getResources().getString(R.string.no_song) + " " + song);
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

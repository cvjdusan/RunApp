package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.users.Session;

public class LifecycleAwarePlayer implements DefaultLifecycleObserver {

    private static MediaPlayer mp;
    private MediaPlayer mediaPlayer = null;

    @Inject
    public LifecycleAwarePlayer() {

    }

    public void start(Context context) {
        if (mediaPlayer == null) {
            try {
                String musicList = Session.getCurrentPlaylist().getMusicList();
                String song = null;
                int n = musicList.charAt(0) - '0';
                int songNum = 0;
                for(String file: context.getFilesDir().list()){
                    if(n == songNum)
                        song = file;
                    songNum++;
                }
                String path = context.getFilesDir().getAbsolutePath() + File.separator + song;
                mediaPlayer = new MediaPlayer();
                mp = mediaPlayer; // static
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(m -> {
                    int dur = mediaPlayer.getDuration();
                    mediaPlayer.start();

                    int seconds = (int) ((dur / 1000) % 60);
                    int minutes = (int) ((dur / (1000 * 60)) % 60);

                    StringBuilder songTime = new StringBuilder();
                    songTime.append(String.format("%02d", minutes)).append(":");
                    songTime.append(String.format("%02d", seconds));

                    TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.remaining);
                    tw.setText(songTime);
                });
                mediaPlayer.prepareAsync();
                TextView tw = (TextView) Session.getMainActivity().findViewById(R.id.current_playlist);
                tw.setText(Session.getMainActivity().getResources().getString(R.string.no_playlist) + " " + Session.getCurrentPlaylist().getName());
                tw = (TextView) Session.getMainActivity().findViewById(R.id.current_song);
                tw.setText(Session.getMainActivity().getResources().getString(R.string.no_song) + " " + song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mp;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mp = mediaPlayer;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

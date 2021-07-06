package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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
                String musicList = Session.getCurrentPlaylist().getMusicListPositions();
                String song = null;
                int musicPosition = musicList.charAt(0) - '0';
                String path = context.getFilesDir() + File.separator + "music";
                File directory = null;
                directory = new File(path);
//                String[] names = Session.getCurrentPlaylist().getMusicListNames().split("/");
//                if(names.length == 1)
//                    song = names[0];
//                else
//                    song = names[musicPosition];
                int currentPosition = 0;
                for(String file: directory.list()){
                    if(musicPosition == currentPosition) {
                        song = file;
                        break;
                    } else
                        currentPosition++;
                }

                String path1 = context.getFilesDir().getAbsolutePath() +
                        File.separator + "music" + File.separator + song;
                mediaPlayer = new MediaPlayer();
                mp = mediaPlayer; // static
                mediaPlayer.setDataSource(path1);

                Intent local = new Intent();
                local.setAction("startMusic");

                String finalSong = song;
                mediaPlayer.setOnPreparedListener(m -> {
                    int dur = mediaPlayer.getDuration();
                    mediaPlayer.start();

                    int seconds = ((dur / 1000) % 60);
                    int minutes = ((dur / (1000 * 60)) % 60);

                    StringBuilder songTime = new StringBuilder();
                    songTime.append(String.format("%02d", minutes)).append(":");
                    songTime.append(String.format("%02d", seconds));

                    local.putExtra("songDuration", (Serializable) songTime);
                    local.putExtra("songName", finalSong);
                    Session.setCurrentSong(finalSong);

                    context.sendBroadcast(local);
                });
                mediaPlayer.prepareAsync();
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

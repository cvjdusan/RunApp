package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String username;

    private String name;
    private String musicList;

    public Playlist(long id, String username, String name, String musicList) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.musicList = musicList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusicList() {
        return musicList;
    }

    public void setMusicList(String musicList) {
        this.musicList = musicList;
    }
}

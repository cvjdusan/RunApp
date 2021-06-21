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
    private String musicListPositions;

    private String musicListNames;

    public Playlist(long id, String username, String name, String musicListPositions, String musicListNames) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.musicListPositions = musicListPositions;
        this.musicListNames = musicListNames;
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

    public String getMusicListPositions() {
        return musicListPositions;
    }

    public void setMusicList(String musicListPositions) {
        this.musicListPositions = musicListPositions;
    }

    public String getMusicListNames() {
        return musicListNames;
    }

    public void setMusicListNames(String musicListNames) {
        this.musicListNames = musicListNames;
    }
}

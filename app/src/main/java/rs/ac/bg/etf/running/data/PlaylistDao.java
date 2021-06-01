package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    long insert(Playlist playlist);

    @Query("SELECT * FROM Playlist WHERE username = :username")
    List<Playlist> getAll(String username);

    @Query("SELECT * FROM Playlist WHERE username = :username")
    LiveData<List<Playlist>> getAllLiveData(String username);
}

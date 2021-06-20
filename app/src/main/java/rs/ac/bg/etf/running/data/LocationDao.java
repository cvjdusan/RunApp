package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    long insert(Location location);

    @Query("SELECT * FROM Location WHERE username = :username")
    List<Location> getAll(String username);

    @Query("SELECT * FROM Location WHERE username = :username")
    LiveData<List<Location>> getAllLiveData(String username);
}
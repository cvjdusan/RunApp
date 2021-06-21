package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert
    long insert(Workout workout);

    @Query("SELECT * FROM Workout WHERE username = :username")
    LiveData<List<Workout>> getAllLiveData(String username);

    @Query("SELECT * FROM Workout WHERE username = :username ORDER BY distance DESC ")
    LiveData<List<Workout>> getAllSortedLiveData(String username);

    @Query("SELECT * FROM Workout WHERE username = :username")
    List<Workout> getAll(String username);
}

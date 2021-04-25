package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllLiveData();

    @Query("SELECT * FROM User WHERE email = :email")
    User findUserByEmail(String email);

    @Query("SELECT * FROM User WHERE username = :username")
    User findUserByUsername(String username);

}

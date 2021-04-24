package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class UserRepository {

    private final ExecutorService executorService;

    private final UserDao userDao;

    @Inject
    public UserRepository(
            ExecutorService executorService,
            UserDao userDao) {
        this.executorService = executorService;
        this.userDao = userDao;
    }

    public void insert(User user) {
        executorService.submit(() -> userDao.insert(user));
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public LiveData<List<User>> getAllLiveData() {
        return userDao.getAllLiveData();
    }

    public LiveData<User> findUserByEmail(String email) {return userDao.findUserByEmail(email); }

    public LiveData<User> findUserByUsername(String username) {return userDao.findUserByUsername(username); }
}

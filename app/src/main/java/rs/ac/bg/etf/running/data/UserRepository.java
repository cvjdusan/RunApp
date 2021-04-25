package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

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

    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }
}

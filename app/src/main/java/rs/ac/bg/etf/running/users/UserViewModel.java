package rs.ac.bg.etf.running.users;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.data.UserRepository;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.data.WorkoutRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";
    private boolean sorted = false;

    private final LiveData<List<User>> users;

    @ViewModelInject
    public UserViewModel(
            UserRepository userRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.userRepository = userRepository;
        this.savedStateHandle = savedStateHandle;

        users = Transformations.switchMap(
                savedStateHandle.getLiveData(SORTED_KEY, false),
                sorted -> {
                    return userRepository.getAllLiveData();
                }
        );
    }

    public void invertSorted() {
        savedStateHandle.set(SORTED_KEY, sorted = !sorted);
    }

    public void insertUser(User user) {
        userRepository.insert(user);
    }

    public LiveData<List<User>> getWorkoutList() {
        return users;
    }
}

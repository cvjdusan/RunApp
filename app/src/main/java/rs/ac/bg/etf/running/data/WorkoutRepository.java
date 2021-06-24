package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class WorkoutRepository {

    private final ExecutorService executorService;

    private final WorkoutDao workoutDao;

    @Inject
    public WorkoutRepository(
            ExecutorService executorService,
            WorkoutDao workoutDao) {
        this.executorService = executorService;
        this.workoutDao = workoutDao;
    }

    public void insert(Workout workout) {
        executorService.submit(() -> workoutDao.insert(workout));
    }

    public List<Workout> getAll(String username) {
        return workoutDao.getAll(username);
    }

    public LiveData<List<Workout>> getAllLiveData(int filter, double from, double to,
                                                  String username) {
        if(filter != -1) {

            if(filter == 0){
                return workoutDao.getAllLiveDataFilterDuration(from, to, username);
            }

            else if(filter == 1){
                return workoutDao.getAllLiveDataFilterDistance(from, to, username);
            }

        }

        return workoutDao.getAllLiveData(username);
    }

    public LiveData<List<Workout>> getAllSortedLiveData(int filter, double from, double to,
                                                        String username) {

        if(filter != -1) {

            if(filter == 0){
                return workoutDao.getAllLiveDataFilterDurationSorted(from, to, username);
            }

            else if(filter == 1){
                return workoutDao.getAllLiveDataFilterDistanceSorted(from, to, username);
            }

        }

        return workoutDao.getAllSortedLiveData(username);
    }

    public long getLastInsertedFromUser(String username){
        return workoutDao.getLastInsertedFromUser(username);
    }

}

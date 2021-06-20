package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class LocationRepository {

    private final ExecutorService executorService;

    private final LocationDao locationDao;

    @Inject
    public LocationRepository(
            ExecutorService executorService,
            LocationDao locationDao) {
        this.executorService = executorService;
        this.locationDao = locationDao;
    }

    public void insert(Location location) { executorService.submit(() -> locationDao.insert(location)); }

    public List<Location> getAll(String username) {
        return locationDao.getAll(username);
    }

    public LiveData<List<Location>> getAllLiveData(String username) {
        return locationDao.getAllLiveData(username);
    }

}
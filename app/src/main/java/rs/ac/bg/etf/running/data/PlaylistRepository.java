package rs.ac.bg.etf.running.data;


import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class PlaylistRepository {

    private final ExecutorService executorService;

    private final PlaylistDao playlistDao;

    @Inject
    public PlaylistRepository(ExecutorService executorService, PlaylistDao playlistDao) {
        this.executorService = executorService;
        this.playlistDao = playlistDao;
    }

    public void insert(Playlist playlist) { executorService.submit(() -> playlistDao.insert(playlist)); }

    public List<Playlist> getAll(String username) {
        return playlistDao.getAll(username);
    }

    public LiveData<List<Playlist>> getAllLiveData(String username) {return playlistDao.getAllLiveData(username); }

}

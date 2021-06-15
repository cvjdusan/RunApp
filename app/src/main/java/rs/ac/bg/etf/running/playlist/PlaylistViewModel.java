package rs.ac.bg.etf.running.playlist;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.PlaylistRepository;
import rs.ac.bg.etf.running.users.Session;

public class PlaylistViewModel extends ViewModel {
    private final PlaylistRepository playlistRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String PLAYLIST_KEY = "playlist-key";

    private final LiveData<List<Playlist>> playlists;


    @ViewModelInject
    public PlaylistViewModel(
            PlaylistRepository playlistRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.playlistRepository = playlistRepository;
        this.savedStateHandle = savedStateHandle;

        playlists = Transformations.switchMap(
                savedStateHandle.getLiveData(PLAYLIST_KEY, 0),
                user -> playlistRepository.getAllLiveData(Session.getCurrentUser().getUsername())
        );
    }


    public void insertPlaylist(Playlist playlist) {
        playlistRepository.insert(playlist);
        savedStateHandle.set(PLAYLIST_KEY, 1);
    }

//    public void refreshPlaylists() {
//        savedStateHandle.set(PLAYLIST_KEY, 1);
//    }

    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

}

package rs.ac.bg.etf.running.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistBinding;
import rs.ac.bg.etf.running.users.Session;


@AndroidEntryPoint
public class PlaylistFragment extends Fragment {

    private FragmentPlaylistBinding binding;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(mainActivity);
        playlistViewModel.getPlaylists().observe(
                getViewLifecycleOwner(),
                playlistAdapter::setPlaylistList);

        binding.recyclerView.setAdapter(playlistAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.inflate(R.menu.menu_playlist_fab);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.playlist_fab_create:
                    navController.navigate(
                            PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistCreateFragment());
                           // WorkoutListFragmentDirections.createWorkout());
                    return false;
            }
            return true;
        });

        if(Session.getCurrentPlaylist() != null) {
            Toast.makeText(mainActivity, "Current playlist: " + Session.getCurrentPlaylist().getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mainActivity, "Current playlist is not set", Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}
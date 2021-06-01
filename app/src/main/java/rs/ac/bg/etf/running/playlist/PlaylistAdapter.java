package rs.ac.bg.etf.running.playlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.ViewHolderPlaylistBinding;
import rs.ac.bg.etf.running.databinding.ViewHolderWorkoutBinding;
import rs.ac.bg.etf.running.workouts.DateTimeUtil;
import rs.ac.bg.etf.running.workouts.WorkoutAdapter;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistViewModel playlistViewModel;

    public PlaylistAdapter(){

    }

    public void setPlaylistList(List<Playlist> playlistList) {
        this.playlistList = playlistList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderPlaylistBinding viewHolderPlaylistBinding = ViewHolderPlaylistBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlaylistAdapter.PlaylistViewHolder(viewHolderPlaylistBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistViewHolder holder, int position) {
        holder.bind(playlistList.get(position));
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderPlaylistBinding binding;

        public PlaylistViewHolder(@NonNull ViewHolderPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Playlist p) {
            binding.playlistLabel.setText(p.getName());
            binding.buttonSelect.setOnClickListener(l -> {
                // nesto ovde
            });
        }
    }
}

package rs.ac.bg.etf.running.playlist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistCreateBinding;


public class PlaylistCreateFragment extends Fragment {

    private FragmentPlaylistCreateBinding binding;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> navController.navigateUp());

      //  TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(mainActivity);
        TextView tv0 = new TextView(mainActivity);
        tv0.setText("#");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(mainActivity);
        tv1.setText(" Naziv ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(mainActivity);
        tv2.setText(" - ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        binding.tableMain.addView(tbrow0);

        File filesDir = mainActivity.getFilesDir();
        int index = 0;
        for (String str : filesDir.list()) {
            TableRow tbrow = new TableRow(mainActivity);
            TextView t1v = new TextView(mainActivity);
            t1v.setText("" + index);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(mainActivity);
            t2v.setText(str);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            CheckBox t3v = new CheckBox(mainActivity);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            t3v.setId(index);
            tbrow.addView(t3v);
            binding.tableMain.addView(tbrow);
            index++;
        }



        binding.createPlaylistButton.setOnClickListener(view -> {

            String name = binding.playlistLabel.getEditText().getText().toString();
            String songs;

            File files = mainActivity.getFilesDir();
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (String str : files.list()) {
                CheckBox checkbox = binding.tableMain.findViewById(i);
                if (checkbox.isChecked())
                    sb.append(i).append('/');
                i++;
            }
            sb.deleteCharAt(sb.length() - 1);
            songs = sb.toString();

            if (!name.equals("") && !songs.equals("")) {
                Playlist newPlaylist = new Playlist(
                        0,
                        "dusan",
                        name,
                        songs
                );
                playlistViewModel.insertPlaylist(newPlaylist);
              //  playlistViewModel.setCurrentPlaylist(newPlaylist);
                navController.navigateUp();
            } else {
                Toast.makeText(mainActivity, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}

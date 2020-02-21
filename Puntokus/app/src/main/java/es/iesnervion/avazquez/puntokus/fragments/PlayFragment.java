package es.iesnervion.avazquez.puntokus.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.Tablero;
import es.iesnervion.avazquez.puntokus.viewModels.TableroViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {

    //cantidad de casillas segun el nivel de dificultad
    final int EASY = 5;
    final int NORMAL = 9 ;
    final int HARD = 11 ;
    final int SICK = 15 ;

    public PlayFragment() {
        // Required empty public constructor
    }

    MediaPlayer backgroundMusic;
    MediaPlayer sonidoTap;
    boolean isMusicAllowed;
    SharedPreferences sharedPreferences;
    boolean areSoundsAllowed;



    @BindView(R.id.view1)
    Button easyBtn;
    @BindView(R.id.view2)
    Button normalBtn;
    @BindView(R.id.view3)
    Button hardBtn;
    @BindView(R.id.view4)
    Button sickBtn;
    TableroViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);


        sonidoTap = MediaPlayer.create(getContext(), R.raw.mec_switch);
        ButterKnife.bind(this,view);
        easyBtn.setOnClickListener(this);
        normalBtn.setOnClickListener(this);
        hardBtn.setOnClickListener(this);
        sickBtn.setOnClickListener(this);
        viewModel = ViewModelProviders.of(getActivity()).get(TableroViewModel.class);
        return view;
    }


    @Override
    public void onClick(View v) {
        int lado = 0;

        switch (v.getId()){
            case R.id.view1:
                lado = EASY;
                break;
            case R.id.view2:
                lado = NORMAL;
                break;
            case R.id.view3:
                lado = HARD;
                break;
            case R.id.view4:
                lado = SICK;
                break;
        }
        if(areSoundsAllowed){
            sonidoTap.start();
        }
        viewModel.setLado(lado);
        viewModel.setIsGoingToPlay(true);
    }


    @Override
    public void onStart() {

        final int MAX_VOLUME = 100;
        final float volume = (float) (1 - (Math.log(MAX_VOLUME - 50) / Math.log(MAX_VOLUME)));


        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        isMusicAllowed = sharedPreferences.getBoolean("Music", true);

        backgroundMusic = MediaPlayer.create(getContext(), R.raw.background_music);
        backgroundMusic.setVolume(volume,volume);
        if(isMusicAllowed){
            backgroundMusic.start();
            backgroundMusic.setLooping(true);
        }else{
            backgroundMusic.stop();
        }
        super.onStart();
    }


    @Override
    public void onPause() {
        backgroundMusic.stop();
        super.onPause();
    }
}

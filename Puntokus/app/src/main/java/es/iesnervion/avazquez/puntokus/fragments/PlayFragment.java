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


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.fragments.customDialogs.InfoDialogFragment;
import es.iesnervion.avazquez.puntokus.viewModels.MainViewModel;

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

    MediaPlayer sonidoTap;
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
    MainViewModel viewModel;
    User user;

    @BindView(R.id.infobtn)
    FloatingActionButton infoBtn;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this,view);
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);
        sonidoTap = MediaPlayer.create(getContext(), R.raw.mec_switch);



        easyBtn.setOnClickListener(this);
        normalBtn.setOnClickListener(this);
        hardBtn.setOnClickListener(this);
        sickBtn.setOnClickListener(this);
        infoBtn.setOnClickListener(this);
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        user = new User(sharedPreferences.getString("UserID", ""),
                sharedPreferences.getString("UserNICK", ""),
                sharedPreferences.getString("UserEMAIL", ""),
                "");
        viewModel.setUsuarioActual(user);


            //https://androidexample365.com/material-intro-view-is-a-showcase-android-library/
            new MaterialIntroView.Builder(getActivity())
                    .enableDotAnimation(false) //Shows dot animation center of focus area
                    .enableIcon(false) //Turn off helper icon, default is true
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(100)
                    .enableFadeAnimation(true)
                    .performClick(true) //Trigger click operation when user click focused area.
                    .setInfoText(getResources().getString(R.string.scv_info))
                    //.setShapeType(ShapeType.CIRCLE)
                    .setTarget(infoBtn)
                    .setUsageId("scv_infoBtn") //THIS SHOULD BE UNIQUE ID -> con esto sabe si ya se ha mostrado o no
                    .show();



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
            case R.id.infobtn:
                    viewModel.setShowDialog(true);

                break;
        }
        if(areSoundsAllowed){
            sonidoTap.start();
        }
        if(lado > 0){
            viewModel.setLado(lado);
            viewModel.setIsGoingToPlay(true);
        }
    }

}

package es.iesnervion.avazquez.puntokus.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.activities.MainActivity;
import es.iesnervion.avazquez.puntokus.entities.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    public AccountFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.txtNickname_account)
    TextView nickname;
    @BindView(R.id.txtEmail_account)
    TextView email;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    @BindView(R.id.toggleSounds)
    Switch sounds;

    @BindView(R.id.toggleMusic)
    Switch music;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String idUsuarioActual;
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this,view);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        idUsuarioActual = firebaseAuth.getCurrentUser().getUid();
        //progressDialog = new ProgressDialog(getContext());
        //progressDialog.setMessage("Cargando datos");
        //progressDialog.show();

        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isMusicAllowed = sharedPreferences.getBoolean("Music", true);
        boolean areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);

        music.setChecked(isMusicAllowed);
        sounds.setChecked(areSoundsAllowed);



        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isMusicAllowed){
                music.setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                music.setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            }else{
                music.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                music.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }

            if(areSoundsAllowed){
                sounds.setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                sounds.setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            }else{
                sounds.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                sounds.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }




        databaseReference.child("Users").child(idUsuarioActual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nickname.setText(user.getNickname());
                email.setText(user.getEmail());
                //progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnLogout.setOnClickListener(this);
        sounds.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {
        //Cierra la sesión y te devuelve a la main activity
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(),MainActivity.class));
        getActivity().finish();
        //TODO cambiar y poner con viewmodel

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (buttonView.getId()){
            case R.id.toggleMusic:
                editor.putBoolean("Music",isChecked);

                break;
            case R.id.toggleSounds:
                editor.putBoolean("Sounds",isChecked);
                break;
        }
        editor.commit();

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isChecked){
                ((Switch)buttonView).setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                ((Switch)buttonView).setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            }else{
                ((Switch)buttonView).setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                ((Switch)buttonView).setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }



    }
}

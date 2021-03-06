package es.iesnervion.avazquez.puntokus.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.activities.AutenticacionActivity;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.viewModels.MainViewModel;

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
    @BindView(R.id.btn_delete)
    Button btn_delete;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String idUsuarioActual;

    MainViewModel viewModel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isMusicAllowed;
    boolean areSoundsAllowed;
    MediaPlayer sonidoTap;
    MediaPlayer alertSound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        idUsuarioActual = firebaseAuth.getCurrentUser().getUid();
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        isMusicAllowed = sharedPreferences.getBoolean("Music", true);
        areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);
        if (sonidoTap == null) {
            sonidoTap = MediaPlayer.create(getContext(), R.raw.mec_switch);
        }
        if (alertSound == null) {
            alertSound = MediaPlayer.create(getContext(), R.raw.alert);
        }

        music.setChecked(isMusicAllowed);
        sounds.setChecked(areSoundsAllowed);


        /*
         * Si la versión de Android es superior a Marshmallow
         * se aplicarán colores diferentes para los checkboxes.
         * De lo contrario, no.
         * Esto es así porque en veriones anteriores no se puede
         * usar el tracktintlist
         * */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isMusicAllowed) {
                music.setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                music.setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            } else {
                music.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                music.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }

            if (areSoundsAllowed) {
                sounds.setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                sounds.setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            } else {
                sounds.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                sounds.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }

        nickname.setText(sharedPreferences.getString("UserNICK", ""));
        email.setText(sharedPreferences.getString("UserEMAIL", ""));

        btnLogout.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        sounds.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        AlertDialog dialog;
        builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        if (sharedPreferences.getBoolean("Sounds", true)) {
            sonidoTap.start();
        }
        switch (v.getId()) {
            case R.id.btn_logout:
                //Pide confirmación, Cierra la sesión y te devuelve a la main activity
                //pongo el titulo y los botones
                builder.setTitle(R.string.logout);
                builder.setMessage(R.string.msgConfirmLogout)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Si el usuario le da a que sí desea salir
                                editor.putString("UserID", "");
                                editor.putString("UserEMAIL", "");
                                editor.putString("UserNICK", "");
                                //No hagas clear que borras lo de la música también!
                                editor.putBoolean("IsLogged", false);
                                editor.commit();
                                firebaseAuth.signOut();

                                startActivity(new Intent(getContext(), AutenticacionActivity.class));
                                getActivity().finish(); //TODO cambiar y poner con viewmodel
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);

                //lo muestro
                dialog = builder.create();
                dialog.show();
                if (areSoundsAllowed) {
                    alertSound.start();
                }

                break;
            case R.id.btn_delete:
                //Eliminar la cuenta y todos los datos de las partidas asociadas a esa cuenta
                //Pedir confirmación
                //pongo el titulo y los botones
                builder.setTitle(R.string.deleteAccount);
                builder.setMessage(R.string.msgConfirmDeleteAccount)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Hace click en que sí quiere borrar sus datos
                                //Compruebo si hay conexión con firebase
                                DatabaseReference connectedRef = FirebaseDatabase
                                        .getInstance().getReference(".info/connected");
                                connectedRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean isConnected = dataSnapshot.getValue(Boolean.class);
                                        if (isConnected) {
                                            if (sharedPreferences.getBoolean("IsLogged", false)) {
                                                eliminarTodosLosDatos();    //esto debe ir antes de borrar los datos de shared preferences
                                                editor.putString("UserID", "");
                                                editor.putString("UserEMAIL", "");
                                                editor.putString("UserNICK", "");
                                                //No hago clear porque borraría la información
                                                // referente a música y sonidos también
                                                editor.putBoolean("IsLogged", false);
                                                editor.commit();
                                                firebaseAuth.getCurrentUser()
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    if (getContext() != null) {   //por el internet
                                                                        Intent intent = new Intent(getContext(), AutenticacionActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        Toast toast1 =
                                                                                Toast.makeText(getContext(),
                                                                                        R.string.accountDeleted, Toast.LENGTH_SHORT);
                                                                        toast1.show();
                                                                        if(getActivity() != null){
                                                                            startActivity(intent);
                                                                            getActivity().finish(); //TODO cambiar y poner con viewmodel
                                                                        }
                                                                    }



                                                                } else {
                                                                    Intent intent = new Intent(getContext(), AutenticacionActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    if (getContext() != null) {
                                                                        Toast toast1 =
                                                                                Toast.makeText(getContext(),
                                                                                        R.string.error, Toast.LENGTH_SHORT);
                                                                        toast1.show();
                                                                    }
                                                                    //Lo pongo así porque a veces
                                                                    //la respuesta de firebase viaja tarde y entra aqui cuando ya he cerrado esto
                                                                    //y da un nullreference
                                                                    if(getActivity() != null){
                                                                        startActivity(intent);
                                                                        getActivity().finish(); //TODO cambiar y poner con viewmodel
                                                                    }

                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            AlertDialog.Builder builder;
                                            AlertDialog dialog;
                                            if(getContext() != null){
                                                builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                                                //pongo el titulo y los botones
                                                builder.setTitle(R.string.error);
                                                builder.setMessage(R.string.errorLoading)
                                                        .setCancelable(true);

                                                //lo muestro
                                                dialog = builder.create();

                                                if(!dialog.isShowing()){
                                                    dialog.show();
                                                }
                                            }


                                            if (areSoundsAllowed) {
                                                alertSound.start();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);

                //lo muestro
                dialog = builder.create();
                dialog.show();
                if (areSoundsAllowed) {
                    alertSound.start();
                }
                break;
        }
    }

    /**
     * Se llama cuando cambia un switch
     * @param buttonView switch que cambia
     * @param isChecked nuevo valor que adquiere
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (sharedPreferences.getBoolean("Sounds", true)) {
            sonidoTap.start();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (buttonView.getId()) {
            case R.id.toggleMusic:
                editor.putBoolean("Music", isChecked);
                break;
            case R.id.toggleSounds:
                editor.putBoolean("Sounds", isChecked);
                break;
        }
        editor.commit();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isChecked) {
                ((Switch) buttonView).setTrackTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
                ((Switch) buttonView).setThumbTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorOscuro1)));
            } else {
                ((Switch) buttonView).setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                ((Switch) buttonView).setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }
    }

    /*
     * Signatura: public void eliminarTodosLosDatos()
     * Comentario: elimina todos los elementos hijos de las colecciones Users y Games
     * de firebase referidas a un usuario concreto.
     * Precondiciones: Debe haber internet y conexión con firebase
     * Entradas:
     * Salidas:
     * Postcondiciones: los datos del usuario quedarán eliminados en firebase.
     * */
    public void eliminarTodosLosDatos() {

        databaseReference.child("Users").
                child(sharedPreferences.getString("UserID", "")).removeValue();
        databaseReference.child("Games").
                child(sharedPreferences.getString("UserID", "")).removeValue();


    }
}

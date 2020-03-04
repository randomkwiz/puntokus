package es.iesnervion.avazquez.puntokus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.Game;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.fragments.AccountFragment;
import es.iesnervion.avazquez.puntokus.fragments.GameFragment;
import es.iesnervion.avazquez.puntokus.fragments.PlayFragment;
import es.iesnervion.avazquez.puntokus.fragments.RankingFragment;
import es.iesnervion.avazquez.puntokus.fragments.customDialogs.InfoDialogFragment;
import es.iesnervion.avazquez.puntokus.viewModels.MainViewModel;

public class SecondMainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {


    @BindView(R.id.menu_nav_bottom)
    BottomNavigationView bottomNavigationView;
    AccountFragment accountFragment;
    PlayFragment playFragment;
    RankingFragment rankingFragment;
    GameFragment gameFragment;
    InfoDialogFragment infoDialogFragment;
    MainViewModel viewModel;
    MediaPlayer backgroundMusic;
    MediaPlayer sonidoTap;
    MediaPlayer alertSound;
    boolean isMusicAllowed;
    SharedPreferences sharedPreferences;
    boolean areSoundsAllowed;
    User user;

    @Override
    protected void onStart() {
        super.onStart();

        final int MAX_VOLUME = 100;
        final float volume = (float) (1 - (Math.log(MAX_VOLUME - 50) / Math.log(MAX_VOLUME)));
        sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);
        isMusicAllowed = sharedPreferences.getBoolean("Music", true);
        if(sonidoTap == null){  //Para que no cree el sonido dos veces
            sonidoTap = MediaPlayer.create(this, R.raw.mec_switch);
        }
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(this, R.raw.lounge_david_renda);
            //Lo pongo así porque si no a veces se bugea y se crea dos veces y
            //se solapan los sonidos
        }
        if(alertSound == null){
            alertSound = MediaPlayer.create(this,R.raw.alert);
        }

        backgroundMusic.setVolume(volume, volume);
        if (isMusicAllowed) {
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
                backgroundMusic.setLooping(true);
            }

        } else {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.pause();

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
        //backgroundMusic.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        accountFragment = new AccountFragment();
        playFragment = new PlayFragment();
        rankingFragment = new RankingFragment();
        gameFragment = new GameFragment();
        infoDialogFragment = new InfoDialogFragment();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //Puedo coger estos datos del sharedPreferences
        user = new User(sharedPreferences.getString("UserID", ""),
                sharedPreferences.getString("UserNICK", ""),
                sharedPreferences.getString("UserEMAIL", ""),
                "");
        viewModel.setUsuarioActual(user);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();


        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_play);
            fragmentTransaction.replace(R.id.fragmentSecondActivity, playFragment).commit();
        } else {
            //Cuando cambia la configuración debe comprobar si está en el fragment de juego -> debe deshabilitar los botones otra vez
            if (getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentSecondActivity) instanceof GameFragment) {
                bottomNavigationView.getMenu().getItem(0).setCheckable(false);
                bottomNavigationView.getMenu().getItem(1).setCheckable(false);
                bottomNavigationView.getMenu().getItem(2).setCheckable(false);
            }
        }


        Observer<Boolean> playObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    //Cambiar el fragment de play por el de game
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, gameFragment, "GAME")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(null)   //este si puede volver atras
                            .commit();

                    //Deshabilito el bottom nav menu
                    //Lo pongo así y no con setEnable porque entonces no entraria en el on nav item selected y no me deja poner el toast
                    bottomNavigationView.getMenu().getItem(0).setCheckable(false);
                    bottomNavigationView.getMenu().getItem(1).setCheckable(false);
                    bottomNavigationView.getMenu().getItem(2).setCheckable(false);
                }

            }
        };
        viewModel.getIsGoingToPlay().observe(this, playObserver);


        Observer<Boolean> showDialogObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    //Mostrar dialog
                    infoDialogFragment.show(getSupportFragmentManager(), "INFODIALOG");
                    if(areSoundsAllowed){
                        alertSound.start();
                    }
                }

            }
        };
        viewModel.getShowDialog().observe(this, showDialogObserver);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean ret = false;
        if(areSoundsAllowed){
            sonidoTap.start();
        }
        //Los menu item los pongo como no checkable en vez de
        //not enabled para que sí entren aquí y muestren el toast
        //indicando que deben pulsar atrás
        //De la otra forma directamente ni entraría aquí
        if (!menuItem.isCheckable()) {
            Toast.makeText(this,
                    getResources().getText(R.string.pressBack), Toast.LENGTH_SHORT)
                    .show();
        } else {
            switch (menuItem.getItemId()) {
                case R.id.menu_account:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, accountFragment, "ACCOUNT")
                            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                    ;
                    //  viewModel.setCurrentFragment("ACCOUNT");
                    ret = true;
                    break;
                case R.id.menu_play:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                            // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    //  viewModel.setCurrentFragment("PLAY");
                    ret = true;
                    break;
                case R.id.menu_ranking:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, rankingFragment, "RANKING")
                            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    //  viewModel.setCurrentFragment("RANKING");
                    ret = true;
                    break;

            }
        }


        return ret;
    }

    /**
     * Sobreescribo el método onBackPressed
     * para controlar a mano la navegación y poner alerts de ayuda al usuario
     */
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentSecondActivity);
        if (currentFragment instanceof GameFragment) {
            //Si el usuario pulsa el boton de ir hacia atrás estando
            //en el fragment del juego, se le mostrará dialog de confirmación
            AlertDialog.Builder builder;
            AlertDialog dialog;
            builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            //pongo el titulo y los botones
            builder.setTitle(R.string.titleConfirmExit);
            builder.setMessage(R.string.msgConfirmExit)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Si el usuario le da a que sí desea salir
                            viewModel.setUserWantToGoBack(true);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);

            //lo muestro
            dialog = builder.create();
            dialog.show();
            if(areSoundsAllowed){
                alertSound.start();
            }

            Observer<Boolean> goBackObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();

                        //   viewModel.setCurrentFragment("PLAY");

                        //Habilitamos el menu
                        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
                        bottomNavigationView.getMenu().getItem(1).setCheckable(true);
                        bottomNavigationView.getMenu().getItem(2).setCheckable(true);
                        bottomNavigationView.setSelectedItemId(R.id.menu_play);
                    }
                }
            };
            viewModel.getUserWantToGoBack().observe(this, goBackObserver);


        } else if (currentFragment instanceof PlayFragment) {
            //Si está aquí y le da hacia atrás, le preguntaremos si desea salir
            //Si el usuario pulsa el boton de ir hacia atrás estando
            //en el fragment del login, se le mostrará dialog de confirmación
            AlertDialog.Builder builder;
            AlertDialog dialog;
            builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            //pongo el titulo y los botones
            builder.setTitle(R.string.titleConfirmExit);
            builder.setMessage(R.string.msgExitLogin)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Si el usuario le da a que sí desea salir
                            viewModel.setUserWantToExit(true);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);

            //lo muestro
            dialog = builder.create();
            dialog.show();
            if(areSoundsAllowed){
                alertSound.start();
            }

            Observer<Boolean> exitObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        salir();
                    }
                }
            };
            viewModel.getUserWantToExit().observe(this, exitObserver);

        } else if (currentFragment instanceof InfoDialogFragment) {
            infoDialogFragment.dismiss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.menu_play);
            //  viewModel.setCurrentFragment("PLAY");
        }
    }


    public void salir() {
        super.onBackPressed();
    }


    /* Escucha los cambios en los archivos
     * Shared Preferences
     * Si la key del valor cambiado es "Music", pausará o empezará
     * a reproducir la música dependiendo de si el nuevo valor es true
     * o false.
     * Si la key del valor cambiado es "Sounds", el valor correspondiente
     * será asignado a la variable areSoundsAllowed
     *
     * */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Music")) {
            if (sharedPreferences.getBoolean("Music", true)) {
                if (!backgroundMusic.isPlaying()) {
                    backgroundMusic.start();
                    backgroundMusic.setLooping(true);
                }

            } else {
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();

                }
            }
        }else if(key.equals("Sounds")){
            areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);
        }
    }

}

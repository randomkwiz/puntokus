package es.iesnervion.avazquez.puntokus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.fragments.AccountFragment;
import es.iesnervion.avazquez.puntokus.fragments.GameFragment;
import es.iesnervion.avazquez.puntokus.fragments.PlayFragment;
import es.iesnervion.avazquez.puntokus.fragments.RankingFragment;
import es.iesnervion.avazquez.puntokus.viewModels.MainViewModel;

public class SecondMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.menu_nav_bottom)
    BottomNavigationView bottomNavigationView;
    AccountFragment accountFragment;
    PlayFragment playFragment;
    RankingFragment rankingFragment;
    GameFragment gameFragment;
    MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        accountFragment = new AccountFragment();
        playFragment = new PlayFragment();
        rankingFragment = new RankingFragment();
        gameFragment = new GameFragment();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getUsuarioActual().getValue().setNickname(intent.getStringExtra("nickname"));
        viewModel.getUsuarioActual().getValue().setEmail(firebaseAuth.getCurrentUser().getEmail());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        bottomNavigationView.setSelectedItemId(R.id.menu_play);
       // if(viewModel.getCurrentFragment().getValue().isEmpty()){
            fragmentTransaction.replace(R.id.fragmentSecondActivity, playFragment).commit();
        //}else{
//            Fragment destinationFragment = getSupportFragmentManager()
//                    .findFragmentByTag(viewModel.getCurrentFragment().getValue());
//            fragmentTransaction.replace(R.id.fragmentSecondActivity, destinationFragment).commit();
//
//        }


        Observer<Boolean> playObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(aBoolean){
                    //Cambiar el fragment de play por el de game
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity,gameFragment, "GAME" )
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
        viewModel.getIsGoingToPlay().observe(this,playObserver);

    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean ret = false;

        if(!menuItem.isCheckable()){
            Toast.makeText(this,
                    getResources().getText(R.string.pressBack), Toast.LENGTH_SHORT)
                    .show();

        }else{
            switch (menuItem.getItemId()){
                case R.id.menu_account:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, accountFragment, "ACCOUNT")
                            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                    ;
                    viewModel.setCurrentFragment("ACCOUNT");
                    ret = true;
                    break;
                case R.id.menu_play:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                            // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    viewModel.setCurrentFragment("PLAY");
                    ret = true;
                    break;
                case R.id.menu_ranking:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentSecondActivity, rankingFragment, "RANKING")
                            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    viewModel.setCurrentFragment("RANKING");
                    ret = true;
                    break;

            }
        }



        return ret;
    }



    @Override
    public void onBackPressed(){
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentSecondActivity);
        if(currentFragment instanceof GameFragment){
            //Si el usuario pulsa el boton de ir hacia atrás estando
            //en el fragment del juego, se le mostrará dialog de confirmación
            AlertDialog.Builder builder;
            AlertDialog dialog;
            builder = new AlertDialog.Builder(this);
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


            Observer<Boolean> goBackObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();

                        viewModel.setCurrentFragment("PLAY");

                        //Habilitamos el menu
                        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
                        bottomNavigationView.getMenu().getItem(1).setCheckable(true);
                        bottomNavigationView.getMenu().getItem(2).setCheckable(true);
                        bottomNavigationView.setSelectedItemId(R.id.menu_play);
                    }
                }
            };
            viewModel.getUserWantToGoBack().observe(this,goBackObserver);



        }else if(currentFragment instanceof PlayFragment){
            //Si está aquí y le da hacia atrás, le preguntaremos si desea salir
            //Si el usuario pulsa el boton de ir hacia atrás estando
            //en el fragment del login, se le mostrará dialog de confirmación
            AlertDialog.Builder builder;
            AlertDialog dialog;
            builder = new AlertDialog.Builder(this);
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


            Observer<Boolean> exitObserver = new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        salir();
                    }
                }
            };
            viewModel.getUserWantToExit().observe(this,exitObserver);


        }else if(currentFragment instanceof AccountFragment){

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.menu_play);
            viewModel.setCurrentFragment("PLAY");
        }else if(currentFragment instanceof RankingFragment){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentSecondActivity, playFragment, "PLAY")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.menu_play);
            viewModel.setCurrentFragment("PLAY");
        }

        else{
            salir();
        }
    }


    public void salir(){
        super.onBackPressed();
    }

}

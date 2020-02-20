package es.iesnervion.avazquez.puntokus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.fragments.GameFragment;
import es.iesnervion.avazquez.puntokus.fragments.LoginFragment;
import es.iesnervion.avazquez.puntokus.fragments.RegistrarseFragment;
import es.iesnervion.avazquez.puntokus.viewModels.ViewModelRegistro;

public class MainActivity extends AppCompatActivity {

    Fragment login;
    Fragment registro;
    ViewModelRegistro viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Esto para el app center
        AppCenter.start(getApplication(), "eab25880-dfe1-46f4-9ea7-b7b00d5d9ce9",
                Analytics.class, Crashes.class);
        login = new LoginFragment();
        registro = new RegistrarseFragment();
        Intent intent = new Intent(this, SecondMainActivity.class);
        viewModel = ViewModelProviders.of(this).get(ViewModelRegistro.class);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragment, login).commit();


        /*El observer*/
        final Observer<Boolean> signUpObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if(aBoolean){
                    ft.replace(R.id.fragment, registro)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(null)
                            .commit();   //aqui si interesa el add to back stack
                }

            }
        };
        //Observo
        viewModel.getGoToSignUp().observe(this,signUpObserver);


        /*El observer*/
        final Observer<Boolean> logInObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if(aBoolean){
                    ft.replace(R.id.fragment, login)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(null)
                            .commit();//no pongo el add to back stack porque no me interesa aqui
                }
            }
        };
        //Observo
        viewModel.getGoToLogIn().observe(this,logInObserver);


        /*El observer*/
        final Observer<Boolean> isCorrectLoginObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    //finish();
                    intent.putExtra("nickname", viewModel.getUser().getValue().getNickname());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);



                    startActivity(intent);
                   // System.exit(0);



                }

            }
        };
        //Observo
        viewModel.getIsCorrectLogin().observe(this,isCorrectLoginObserver);


    }

    @Override
    public void onBackPressed(){
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        if(currentFragment instanceof LoginFragment){
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



        }else if(currentFragment instanceof RegistrarseFragment){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, login)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

                    .commit();
        }


        else{
            salir();
        }
    }


    public void salir(){

        super.onBackPressed();

    }

}

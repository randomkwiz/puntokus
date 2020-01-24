package es.iesnervion.avazquez.puntokus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.iesnervion.avazquez.puntokus.R;
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
                    ft.replace(R.id.fragment, registro).commit();   //no pongo el add to back stack porque no me interesa aqui
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
                    ft.replace(R.id.fragment, login).commit();//no pongo el add to back stack porque no me interesa aqui
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
                    startActivity(intent);
                }

            }
        };
        //Observo
        viewModel.getIsCorrectLogin().observe(this,isCorrectLoginObserver);


    }
}

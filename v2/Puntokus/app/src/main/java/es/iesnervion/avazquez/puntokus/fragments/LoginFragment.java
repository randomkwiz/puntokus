package es.iesnervion.avazquez.puntokus.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.activities.MainActivity;
import es.iesnervion.avazquez.puntokus.activities.SecondMainActivity;
import es.iesnervion.avazquez.puntokus.viewModels.ViewModelRegistro;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public LoginFragment() {
        // Required empty public constructor
    }




    //Hago los binding con butter knife
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.link_signup)
    TextView linkSignUp;
    private FirebaseAuth firebaseAuth;
    ViewModelRegistro viewModel;
    ProgressDialog progressDialog;


    @Override
    public void onPause() { //para que se borren las credenciales
        super.onPause();
        email.setText("");
        password.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(ViewModelRegistro.class);
        ButterKnife.bind(this,view); //le mandas la view con la que realizará el binding

        //nota importante: si es en fragment se pone ButterKnife.bind(this,view)
        //si es en activity se pone Butterknife.bind(this)

        // Inicializa el Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(this);
        linkSignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {

        viewModel.setEmail(email.getText().toString().trim());
        viewModel.setPassword(password.getText().toString().trim());
        switch (v.getId()){
            case R.id.btn_login:
                if(!viewModel.getEmail().equals("") && !viewModel.getPassword().equals("") ){
                    iniciarSesion(email.getText().toString().trim(), password.getText().toString().trim());
                }else{
                    Toast.makeText(getContext(), R.string.fillFields, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.link_signup:
                registrarse();
                break;
        }





    }

    /*
    * Registra un usuario
    * */
    private void registrarse() {

        viewModel.setGoToSignUp(true);
    }

    /*
    * Inicia sesión en firebase con un email y contraseña
    * */
    private void iniciarSesion(String email, String password) {
        progressDialog.setMessage("Iniciando sesión");
        progressDialog.show();

        //loguear usuario
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                            //TODO aquí haces que se vaya a la siguiente pantalla.
                            //TODO por aquí y sólo por aquí puede avanzar a la siguiente pantalla!!

                            viewModel.setIsCorrectLogin(true);
                        } else {
                            Toast.makeText(getContext(), "Esa combinación no existe", Toast.LENGTH_LONG).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }
}

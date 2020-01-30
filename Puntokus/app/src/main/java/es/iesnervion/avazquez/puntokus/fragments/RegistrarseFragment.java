package es.iesnervion.avazquez.puntokus.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.viewModels.ViewModelRegistro;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrarseFragment extends Fragment implements View.OnClickListener {


    public RegistrarseFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_nickname)
    EditText nickname;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.link_login)
    TextView linkLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    ViewModelRegistro viewModel;
    ProgressDialog progressDialog;

    @Override
    public void onPause() { //para que se borren las credenciales
        super.onPause();
        nickname.setText("");
        password.setText("");
        email.setText("");
        viewModel.getUser().getValue().setEmail("");
        viewModel.getUser().getValue().setPassword("");
        viewModel.getUser().getValue().setNickname("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registrarse, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(ViewModelRegistro.class);
        ButterKnife.bind(this,view); //le mandas la view con la que realizará el binding

        //nota importante: si es en fragment se pone ButterKnife.bind(this,view)
        //si es en activity se pone Butterknife.bind(this)

        // Inicializa el Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Así hacemos referencia al nodo principal de la DB

        btnSignup.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext());


        return view;
    }

    @Override
    public void onClick(View v) {
        viewModel.getUser().getValue().setEmail(email.getText().toString().trim());
        viewModel.getUser().getValue().setPassword(password.getText().toString().trim());
        viewModel.getUser().getValue().setNickname(nickname.getText().toString().trim());

        switch (v.getId()){
            case R.id.btn_signup:
                if(!viewModel.getUser().getValue().getEmail().isEmpty() || !viewModel.getUser().getValue().getPassword().isEmpty()
                || !viewModel.getUser().getValue().getNickname().isEmpty()
                ){
                    if(viewModel.getUser().getValue().getPassword().length() < 6){   //lo pide firebase
                        Toast.makeText(getContext(), R.string.invalidPassword, Toast.LENGTH_SHORT).show();

                    }else{
                        //TODO por aqué aquí envío los datos tal cual si los tengo en el view model!!
                        registrarse(viewModel.getUser().getValue().getEmail(), viewModel.getUser().getValue().getPassword());
                    }

                }else{
                    Toast.makeText(getContext(), R.string.fillFields, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.link_login:
                iniciarSesion();
                break;
        }






    }

    /*
     * Metodo que pone el atributo de go to login a true
     *  */
    private void iniciarSesion() {
        viewModel.setGoToLogIn(true);
    }

    /*Metodo para registrarse*/
    private void registrarse(String email, String password) {

        progressDialog.setMessage("Registrando usuario");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {    //pa saber cuando acaba la tarea e informar al usuario
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

//                            Toast.makeText(getContext(),
//                                    "Se ha registrado el usuario con el email: "
//                                            + viewModel.getUser().getValue().getEmail(),
//                                    Toast.LENGTH_LONG).show();

                            //Aqui vamos a guardar los datos del usuario en la BBDD Realtime DB
                            //TODO esto

                            //Debemos obtener el ID que nos proporciona Firebase
                            User usuarioActual = new User(firebaseAuth.getCurrentUser().getUid(),
                                    viewModel.getUser().getValue().getNickname(),
                                    viewModel.getUser().getValue().getEmail(),
                                    viewModel.getUser().getValue().getPassword());

                            viewModel.setUser(usuarioActual);

                            Map<String, Object> map = new HashMap<>();
                            map.put("id", viewModel.getUser().getValue().getId());
                            map.put("nickname", viewModel.getUser().getValue().getNickname());
                            map.put("email", viewModel.getUser().getValue().getEmail());
                            //map.put("password", viewModel.getUser().getValue().getPassword()); //no debe guardarse la password


                            databaseReference.child("Users").
                                    child(usuarioActual.getId()).
                                    setValue(map).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if(task2.isSuccessful()){
                                        Toast.makeText(getContext(),
                                                "Se han registrado los datos",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                            //https://www.youtube.com/watch?v=xwhEHb_AZ6k&t=171s
                            //mas info aqui: https://firebase.google.com/docs/database/admin/save-data


                        }else{

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si ya existe
                                Toast.makeText(getContext(), "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "No se pudo registrar el usuario ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });


    }
}

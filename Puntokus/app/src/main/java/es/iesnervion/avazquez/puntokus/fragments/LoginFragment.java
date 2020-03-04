package es.iesnervion.avazquez.puntokus.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.activities.SecondMainActivity;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.viewModels.AutenticacionViewModel;

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
    @BindView(R.id.rememberPassword)
    TextView rememberPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    AutenticacionViewModel viewModel;
    ProgressDialog progressDialog;


    @Override
    public void onPause() { //para que se borren las credenciales
        super.onPause();
        viewModel.getUser().getValue().setEmail(email.getText().toString());
        viewModel.getUser().getValue().setPassword(password.getText().toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(AutenticacionViewModel.class);
        ButterKnife.bind(this, view); //le mandas la view con la que realizará el binding
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //nota importante: si es en fragment se pone ButterKnife.bind(this,view)
        //si es en activity se pone Butterknife.bind(this)
        // Inicializa el Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(this);
        linkSignUp.setOnClickListener(this);
        rememberPassword.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialogStyle);

        //https://stackoverflow.com/questions/13303469/edittext-settext-not-working-with-fragment -> parece ser que hay un problema
        //con set text de edit text en onCreateView -> al parecer a mi no me da problemas
        email.setText(viewModel.getUser().getValue().getEmail());
        password.setText(viewModel.getUser().getValue().getPassword());


        /*El observer*/
        final Observer<Boolean> hayQueBorrarCredencialesObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    viewModel.setUser(new User());
                    email.setText(viewModel.getUser().getValue().getEmail());
                    password.setText(viewModel.getUser().getValue().getPassword());

                }
            }
        };
        //Observo
        //Esto es así no lo toques más
        //viewModel.getGoToLogIn().observe(this, hayQueBorrarCredencialesObserver);
        viewModel.getGoToSignUp().observe(this, hayQueBorrarCredencialesObserver);


        return view;
    }

    @Override
    public void onClick(View v) {

        viewModel.getUser().getValue().setEmail(email.getText().toString().trim());
        viewModel.getUser().getValue().setPassword(password.getText().toString().trim());
        switch (v.getId()) {
            case R.id.btn_login:
                if (!viewModel.getUser().getValue().getEmail().equals("") && !viewModel.getUser().getValue().getPassword().equals("")) {
                    iniciarSesion(viewModel.getUser().getValue().getEmail(), viewModel.getUser().getValue().getPassword());
                } else {
                    Toast.makeText(getContext(), R.string.fillFields, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.link_signup:
                viewModel.setGoToSignUp(true);
                break;
            case R.id.rememberPassword:
                recordarPassword();
                break;
        }
    }

    //Manda correo para recordar la contraseña
    private void recordarPassword() {
        String email = this.email.getText().toString().trim();

        if (!email.isEmpty()) {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(),
                                            getResources().getText(R.string.emailSent), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(),
                                            getResources().getText(R.string.error_emailSent), Toast.LENGTH_LONG).show();
                                }


                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(),
                    getResources().getText(R.string.emailNeeded), Toast.LENGTH_LONG).show();
        }
    }


    /*
     * Inicia sesión en firebase con un email y contraseña
     * */
    private void iniciarSesion(String email, String password) {
        progressDialog.setMessage(getResources().getString(R.string.loggingInWait));
        progressDialog.show();

        //loguear usuario
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {

                            //viewModel.getUser().getValue().setEmail(email);
                            viewModel.getUser().getValue().setId(firebaseAuth.getCurrentUser().getUid());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Users").child(viewModel.getUser().getValue().getId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (user != null) {
                                                viewModel.setUser(user);
                                                editor.putString("UserID", firebaseAuth.getCurrentUser().getUid());
                                                editor.putString("UserEMAIL", firebaseAuth.getCurrentUser().getEmail());
                                                editor.putString("UserNICK", user.getNickname());
                                                editor.putBoolean("IsLogged", true);
                                                editor.commit();
                                                //Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                                                viewModel.setIsCorrectLogin(true);
                                                progressDialog.dismiss();
                                            } else {
                                                if (getContext() != null) {
                                                    Toast.makeText(getContext(), getResources().getText(R.string.errorLogin), Toast.LENGTH_SHORT).show();
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), getResources().getText(R.string.errorLogin), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isLogged = sharedPreferences.getBoolean("IsLogged", false);

        if (isLogged) {
            Intent intent = new Intent(getContext(), SecondMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
}


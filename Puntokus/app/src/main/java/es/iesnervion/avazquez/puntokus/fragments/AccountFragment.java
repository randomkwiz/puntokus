package es.iesnervion.avazquez.puntokus.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
public class AccountFragment extends Fragment implements View.OnClickListener {


    public AccountFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.txtNickname_account)
    TextView nickname;
    @BindView(R.id.txtEmail_account)
    TextView email;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String idUsuarioActual;
    ProgressDialog progressDialog;
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
}

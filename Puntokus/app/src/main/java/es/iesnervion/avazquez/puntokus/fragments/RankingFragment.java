package es.iesnervion.avazquez.puntokus.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.adapter.GamesAdapter;
import es.iesnervion.avazquez.puntokus.entities.Game;
import es.iesnervion.avazquez.puntokus.entities.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {


    public RankingFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.listView_ranking)
    ListView listViewRanking;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ArrayList<Game> listaPartidas;
    Game partida;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        ButterKnife.bind(this,view);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listaPartidas = new ArrayList<>();
       GamesAdapter adapter =
                new GamesAdapter(listaPartidas,getActivity());

        databaseReference.child("Games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        for(DataSnapshot hijo: ds.getChildren()){
                            partida = new Game();
                            partida.setNickname(hijo.child("nickname").getValue().toString());
                            partida.setEmail(hijo.child("email").getValue().toString());
                            partida.setLevel(hijo.child("level").getValue().toString());
                            partida.setTimeInMilis(Long.parseLong(hijo.child("timeInMilis").getValue().toString()));
//                            switch (hijo.getValue().toString().trim()){
//                                case "email":
//
//                                    break;
//                                case "level":
//
//                                    break;
//                                case "timeInMilis":
//
//                                    break;
//
//                            }
                            listaPartidas.add(partida);
                        }

                    }

                    Collections.sort(listaPartidas);
                    if(listaPartidas.size() >= 150){
                        listaPartidas.subList(149, listaPartidas.size()).clear();   //elimina los elementos a partir de la posicion 149
                        //Es para que el ranking tenga max 150 partidas
                    }
                listViewRanking.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

}

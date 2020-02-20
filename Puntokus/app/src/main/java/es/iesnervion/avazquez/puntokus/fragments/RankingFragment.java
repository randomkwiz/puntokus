package es.iesnervion.avazquez.puntokus.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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


    @BindView(R.id.RGfiltrar)
    RadioGroup radioGroup;
    @BindView(R.id.listView_ranking)
    ListView listViewRanking;
    @BindView(R.id.animationLoadRanking)
    LottieAnimationView animationView;
    //TODO hacer el filtro del ranking

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    MutableLiveData<ArrayList<Game>> listaPartidas;
    MutableLiveData<ArrayList<Game>> listaPartidasAMostrar;
    Game partida;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        ButterKnife.bind(this,view);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listaPartidas = new MutableLiveData<ArrayList<Game>>();
        listaPartidasAMostrar = new MutableLiveData<ArrayList<Game>>();
        radioGroup.check(R.id.RBAll);
        listaPartidas.setValue(new ArrayList<>());
        listaPartidasAMostrar.setValue(new ArrayList<>());
       GamesAdapter adapter =
                new GamesAdapter(listaPartidasAMostrar.getValue(),getActivity());


        databaseReference.child("Games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){



                        if(ds != null){
                            for(DataSnapshot hijo: ds.getChildren()){

                                if(hijo != null){
                                    partida = new Game();
                                    partida = hijo.getValue(Game.class);    //esta es la forma correcta de hacerlo

//                                    partida.setNickname(hijo.child("nickname").getValue().toString());
//                                    partida.setEmail(hijo.child("email").getValue().toString());
//                                    partida.setLevel(hijo.child("level").getValue().toString());
//                                    partida.setTimeInMilis(Long.parseLong(hijo.child("timeInMilis").getValue().toString()));

                                    listaPartidas.getValue().add(partida);
                                }
                            }
                        }


                    }

                    Collections.sort(listaPartidas.getValue());
                    if(listaPartidas.getValue().size() >= 150){
                        listaPartidas.getValue().subList(149, listaPartidas.getValue().size()).clear();   //elimina los elementos a partir de la posicion 149
                        //Es para que el ranking tenga max 150 partidas
                    }
                //listViewRanking.setAdapter(adapter);
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.RBAll:
                            listaPartidasAMostrar.setValue(listaPartidas.getValue());
                            break;
                        case R.id.RBeasy:
                            filtrarListado("EASY"); //no puedo ponerlo con el R.string.easy porque en firebase siempre es EASY y nunca FACIL (en español)
                            break;
                        case R.id.RBnormal:
                            filtrarListado("NORMAL");
                            break;
                        case R.id.RBhard:
                            filtrarListado("HARD");
                            break;
                        case R.id.RBsick:
                            filtrarListado("SICK");
                            break;
                    }
                    animationView.setVisibility(View.GONE);
                    listViewRanking.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //El observer de la lista
        final Observer<ArrayList<Game>> listObserver =
                new Observer<ArrayList<Game>>() {
                    @Override
                    public void onChanged(ArrayList<Game> list) {
                        //Actualizar la UI
                        listViewRanking.invalidate(); //Se tiene que poner esto
                        GamesAdapter gamesAdapter2 = new GamesAdapter(list, getActivity().getBaseContext());
                        listViewRanking.setAdapter(gamesAdapter2);

                    }
                };

        //lista.setAdapter(productosAdapter);
        //Observo el LiveData con ese observer que acabo de crear
        listaPartidasAMostrar.observe(this, listObserver);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.RBAll:
                        listaPartidasAMostrar.setValue(listaPartidas.getValue());
                        break;
                    case R.id.RBeasy:
                        filtrarListado("EASY"); //no puedo ponerlo con el R.string.easy porque en firebase siempre es EASY y nunca FACIL (en español)
                        break;
                    case R.id.RBnormal:
                        filtrarListado("NORMAL");
                        break;
                    case R.id.RBhard:
                        filtrarListado("HARD");
                        break;
                    case R.id.RBsick:
                        filtrarListado("SICK");
                        break;
                }
            }
        });


        return view;
    }

    public void filtrarListado(String dificultadPorLaQueFiltro){
        ArrayList<Game> nuevaListaFiltrada = new ArrayList<>();
        if(dificultadPorLaQueFiltro.equals("EASY") ||
                dificultadPorLaQueFiltro.equals("NORMAL") ||
                dificultadPorLaQueFiltro.equals("HARD") ||
                dificultadPorLaQueFiltro.equals("SICK")
        ){
            for(int i = 0; i < listaPartidas.getValue().size(); i ++){

                if(listaPartidas.getValue().get(i).getLevel().trim().contains(dificultadPorLaQueFiltro)){
                    nuevaListaFiltrada.add(listaPartidas.getValue().get(i));
                }
            }
            this.listaPartidasAMostrar.setValue(nuevaListaFiltrada);
            //copiarListaEnOtraLista(nuevaListaFiltrada, this.listaPartidas.getValue());
        }

    }

    public List<Game> copiarListaEnOtraLista(List<Game> listaACopiar, ArrayList<Game> nuevaLista){
       nuevaLista = new ArrayList<>();
        for(int i = 0; i < listaACopiar.size(); i ++){
            nuevaLista.add(listaACopiar.get(i));
        }
        return nuevaLista;
    }

}

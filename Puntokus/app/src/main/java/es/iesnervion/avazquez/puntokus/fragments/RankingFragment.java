package es.iesnervion.avazquez.puntokus.fragments;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    @BindView(R.id.imgErrorLoadingData)
    ImageView imgErrorLoadingData;
    boolean areSoundsAllowed;
    SharedPreferences sharedPreferences;
    MediaPlayer sonidoTap;
    MediaPlayer alertSound;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    MutableLiveData<ArrayList<Game>> listaPartidas;
    MutableLiveData<ArrayList<Game>> listaPartidasAMostrar;
    Game partida;
    boolean isConnected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        ButterKnife.bind(this, view);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        areSoundsAllowed = sharedPreferences.getBoolean("Sounds", true);
        animationView.setVisibility(View.VISIBLE);
        listViewRanking.setVisibility(View.GONE);
        imgErrorLoadingData.setVisibility(View.GONE);
        if (alertSound == null) {
            alertSound = MediaPlayer.create(getContext(), R.raw.alert);
        }
        listaPartidas = new MutableLiveData<ArrayList<Game>>();
        listaPartidasAMostrar = new MutableLiveData<ArrayList<Game>>();

        listaPartidas.setValue(new ArrayList<>());
        listaPartidasAMostrar.setValue(new ArrayList<>());
        GamesAdapter adapter =
                new GamesAdapter(listaPartidasAMostrar.getValue(), getActivity());


//        DatabaseReference connectedRef = FirebaseDatabase
//                .getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                isConnected = snapshot.getValue(Boolean.class);
//                listaPartidas.getValue().clear();
//                listaPartidasAMostrar.getValue().clear();
//                if(isConnected){
//                    imgErrorLoadingData.setVisibility(View.GONE);
//                    databaseReference.child("Games")
//                            //De esta forma solo llama a firebase una vez -> te quitas del lío de valores duplicados por actualización en tiempo real
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                            if (ds != null) {
//                                                for (DataSnapshot hijo : ds.getChildren()) {
//                                                    if (hijo != null) {
//                                                        partida = new Game();
//                                                        partida = hijo.getValue(Game.class);    //esta es la forma correcta de hacerlo
//                                                        listaPartidas.getValue().add(partida);
//                                                        //adapter.notifyDataSetChanged(); //esto no hace que no se duplique la lista xd
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        Collections.sort(listaPartidas.getValue());
//                                        if (listaPartidas.getValue().size() >= 150) {
//                                            listaPartidas.getValue().subList(149, listaPartidas.getValue().size()).clear();   //elimina los elementos a partir de la posicion 149
//                                            //Es para que el ranking tenga max 150 partidas
//                                        }
//                                        //listViewRanking.setAdapter(adapter);
//
//                                        //Esta comprobación debe ir aquí, si la pongo antes
//                                        //no pilla el checked
//                                        if (radioGroup.getCheckedRadioButtonId() == -1) { //ninguno está seleccionado
//                                            radioGroup.check(R.id.RBAll);
//                                        }
//                                        switch (radioGroup.getCheckedRadioButtonId()) {
//                                            case R.id.RBAll:
//                                                listaPartidasAMostrar.setValue(listaPartidas.getValue());
//                                                break;
//                                            case R.id.RBeasy:
//                                                filtrarListado("EASY"); //no puedo ponerlo con el R.string.easy porque en firebase siempre es EASY y nunca FACIL (en español)
//                                                break;
//                                            case R.id.RBnormal:
//                                                filtrarListado("NORMAL");
//                                                break;
//                                            case R.id.RBhard:
//                                                filtrarListado("HARD");
//                                                break;
//                                            case R.id.RBsick:
//                                                filtrarListado("SICK");
//                                                break;
//                                        }
//                                        animationView.setVisibility(View.GONE);
//                                        listViewRanking.setVisibility(View.VISIBLE);
//                                        imgErrorLoadingData.setVisibility(View.GONE);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    //Entra aquí cuando hay error interno del servidor
//                                    AlertDialog.Builder builder;
//                                    AlertDialog dialog;
//                                    builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
//                                    //pongo el titulo y los botones
//                                    builder.setTitle(R.string.error);
//                                    builder.setMessage(R.string.errorLoading)
//                                            .setCancelable(false)
//                                            .setPositiveButton(R.string.ok, null);
//
//                                    //lo muestro
//                                    dialog = builder.create();
//                                    dialog.show();
//                                    if(areSoundsAllowed){
//                                        alertSound.start();
//                                    }
//
//                                    listViewRanking.setVisibility(View.GONE);
//                                    animationView.setVisibility(View.GONE);
//                                    imgErrorLoadingData.setVisibility(View.VISIBLE);
//                                }
//                            });
//                    //El observer de la lista
//                    final Observer<ArrayList<Game>> listObserver =
//                            new Observer<ArrayList<Game>>() {
//                                @Override
//                                public void onChanged(ArrayList<Game> list) {
//                                    //Actualizar la UI
//                                    listViewRanking.invalidate(); //Se tiene que poner esto
//                                    if(getActivity() != null){
//                                        GamesAdapter gamesAdapter2 = new GamesAdapter(list, getActivity().getBaseContext());
//                                        listViewRanking.setAdapter(gamesAdapter2);
//                                    }
//
//
//                                }
//                            };
//
//                    //lista.setAdapter(productosAdapter);
//                    //Observo el LiveData con ese observer que acabo de crear
//                    if(getActivity() != null){
//                        listaPartidasAMostrar.observe(getActivity(), listObserver);
//                    }
//
//
//                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(RadioGroup group, int checkedId) {
//                            // checkedId is the RadioButton selected
//
//                            if (areSoundsAllowed) {
//                                if(sonidoTap == null){
//                                    sonidoTap = MediaPlayer.create(getContext(), R.raw.tap);
//                                }
//
//                                sonidoTap.start();
//                            }
//                            switch (radioGroup.getCheckedRadioButtonId()) {
//                                case R.id.RBAll:
//                                    listaPartidasAMostrar.setValue(listaPartidas.getValue());
//                                    break;
//                                case R.id.RBeasy:
//                                    filtrarListado("EASY"); //no puedo ponerlo con el R.string.easy porque en firebase siempre es EASY y nunca FACIL (en español)
//                                    break;
//                                case R.id.RBnormal:
//                                    filtrarListado("NORMAL");
//                                    break;
//                                case R.id.RBhard:
//                                    filtrarListado("HARD");
//                                    break;
//                                case R.id.RBsick:
//                                    filtrarListado("SICK");
//                                    break;
//                            }
//                        }
//                    });
//                }else{
//                    AlertDialog.Builder builder;
//                    AlertDialog dialog = null;
//                    if(getContext() != null ){
//                        builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
//                        //pongo el titulo y los botones
//                        builder.setTitle(R.string.error);
//                        builder.setMessage(R.string.errorLoading)
//                                .setCancelable(true);
//
//                        //lo muestro
//                        dialog = builder.create();
//                        if(!dialog.isShowing()){
//                            dialog.show();
//                        }
//
//                    }
//
//                    if(areSoundsAllowed){
//                        alertSound.start();
//                    }
//
//                    listViewRanking.setVisibility(View.GONE);
//                    animationView.setVisibility(View.GONE);
//                    imgErrorLoadingData.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                isConnected = false;
//                listViewRanking.setVisibility(View.GONE);
//                animationView.setVisibility(View.GONE);
//                imgErrorLoadingData.setVisibility(View.VISIBLE);
//            }
//        });
        databaseReference.child("Games")
                //De esta forma solo llama a firebase una vez -> te quitas del lío de valores duplicados por actualización en tiempo real
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds != null) {
                                    for (DataSnapshot hijo : ds.getChildren()) {
                                        if (hijo != null) {
                                            partida = new Game();
                                            partida = hijo.getValue(Game.class);    //esta es la forma correcta de hacerlo
                                            if(listaPartidas.getValue() == null) {
                                                listaPartidas.setValue(new ArrayList<>());
                                            }else{
                                                listaPartidas.getValue().add(partida);
                                            }
                                            //adapter.notifyDataSetChanged(); //esto no hace que no se duplique la lista xd
                                        }
                                    }
                                }
                            }
                            if(listaPartidas.getValue() != null){
                                Collections.sort(listaPartidas.getValue());
                            }
                            if (listaPartidas.getValue().size() >= 150) {
                                listaPartidas.getValue().subList(149, listaPartidas.getValue().size()).clear();   //elimina los elementos a partir de la posicion 149
                                //Es para que el ranking tenga max 150 partidas
                            }
                            //listViewRanking.setAdapter(adapter);

                            //Esta comprobación debe ir aquí, si la pongo antes
                            //no pilla el checked
                            if (radioGroup.getCheckedRadioButtonId() == -1) { //ninguno está seleccionado
                                radioGroup.check(R.id.RBAll);
                            }
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
                            imgErrorLoadingData.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Entra aquí cuando hay error interno del servidor
                        AlertDialog.Builder builder;
                        AlertDialog dialog;
                        if(getContext() != null){
                            builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                            //pongo el titulo y los botones
                            builder.setTitle(R.string.error);
                            builder.setMessage(R.string.errorLoading)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.ok, null);

                            //lo muestro
                            dialog = builder.create();
                            if(!dialog.isShowing()) {
                                dialog.show();
                            }
                        }

                        if (areSoundsAllowed) {
                            alertSound.start();
                        }

                        listViewRanking.setVisibility(View.GONE);
                        animationView.setVisibility(View.GONE);
                        imgErrorLoadingData.setVisibility(View.VISIBLE);
                    }
                });

        //El observer de la lista
        final Observer<ArrayList<Game>> listObserver =
                new Observer<ArrayList<Game>>() {
                    @Override
                    public void onChanged(ArrayList<Game> list) {
                        //Actualizar la UI
                        listViewRanking.invalidate(); //Se tiene que poner esto
                        if(getActivity() != null){
                            GamesAdapter gamesAdapter2 = new GamesAdapter(list, getActivity().getBaseContext());
                            listViewRanking.setAdapter(gamesAdapter2);
                        }


                    }
                };

        //lista.setAdapter(productosAdapter);
        //Observo el LiveData con ese observer que acabo de crear
        if(getActivity() != null){
            listaPartidasAMostrar.observe(getActivity(), listObserver);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                if (areSoundsAllowed) {
                    if(sonidoTap == null){
                        sonidoTap = MediaPlayer.create(getContext(), R.raw.tap);
                    }

                    sonidoTap.start();
                }
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


    /**
     * Filtra el listado por la dificultad dada
     *
     * @param dificultadPorLaQueFiltro
     */
    public void filtrarListado(String dificultadPorLaQueFiltro) {
        ArrayList<Game> nuevaListaFiltrada = new ArrayList<>();
        if (dificultadPorLaQueFiltro.equals("EASY") ||
                dificultadPorLaQueFiltro.equals("NORMAL") ||
                dificultadPorLaQueFiltro.equals("HARD") ||
                dificultadPorLaQueFiltro.equals("SICK")
        ) {
            for (int i = 0; i < listaPartidas.getValue().size(); i++) {

                if (listaPartidas.getValue().get(i).getLevel().trim().contains(dificultadPorLaQueFiltro)) {
                    nuevaListaFiltrada.add(listaPartidas.getValue().get(i));
                }
            }
            this.listaPartidasAMostrar.setValue(nuevaListaFiltrada);
            //copiarListaEnOtraLista(nuevaListaFiltrada, this.listaPartidas.getValue());
        }

    }
}

package es.iesnervion.avazquez.puntokus.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.SystemClock;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.Casilla;
import es.iesnervion.avazquez.puntokus.entities.Tablero;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.util.Utilidad;
import es.iesnervion.avazquez.puntokus.viewModels.TableroViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements View.OnClickListener {


    public GameFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.evaluateBtn)
    Button evaluateBtn;
    @BindView(R.id.refreshBtn)
    Button refreshBtn;
    @BindView(R.id.newGameBtn)
    Button newGameBtn;
    @BindView(R.id.layout)
    ConstraintLayout layout;
    @BindView(R.id.cronoTime)
    Chronometer crono;
    TableroViewModel viewModel;
    User usuarioActual;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ButterKnife.bind(this,view);
        viewModel = ViewModelProviders.of(getActivity()).get(TableroViewModel.class);
        viewModel.inicializarPartida();

        establecerTablero(layout,view.getContext(),viewModel.getMapeo(),viewModel.getTablero());
        colocarListeners(layout, viewModel.getTablero(), viewModel.getMapeo());
        crono.start();
        evaluateBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        newGameBtn.setOnClickListener(this);
        evaluateBtn.setEnabled(true);
        // Inicializa el Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usuarioActual = new User();
        usuarioActual.setId(firebaseAuth.getCurrentUser().getUid());
        usuarioActual.setEmail(firebaseAuth.getCurrentUser().getEmail());
        return view;
    }
    public void colocarListeners(ConstraintLayout layout, Tablero tablero, SparseIntArray mapeo){
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {

                if(tablero.getCasillas()[iRow][iCol].isJugable() )
                {
                    //Poner Listener
                    View view = layout.getViewById(tablero.getCasillas()[iRow][iCol].getId());
                    view.setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v instanceof ImageView){
            ImageView casilla = (ImageView) v;
            Utilidad utilidad = new Utilidad();

            //Obtengo el objeto casilla correspondiente a la vista pulsada
            Casilla objetoCasilla = utilidad.obtenerCasillaPorID(viewModel.getTablero(),v.getId());


            if(objetoCasilla.getImgSrc() == R.drawable.selecteditem){
                casilla.setImageResource(R.drawable.nonselecteditem);
                casilla.setTag(R.drawable.nonselecteditem);

                objetoCasilla.setMarcada(false);
                objetoCasilla.setImgSrc(R.drawable.nonselecteditem);

            }else{
                casilla.setImageResource(R.drawable.selecteditem);
                casilla.setTag(R.drawable.selecteditem);
                objetoCasilla.setMarcada(true);
                objetoCasilla.setImgSrc(R.drawable.selecteditem);
            }
        }else if(v instanceof Button){
            Utilidad utilidad = new Utilidad();
            AlertDialog.Builder builder;
            AlertDialog dialog;
            switch (v.getId()){
                case R.id.evaluateBtn:
                    if(utilidad.comprobarSiLaSolucionEsCorrecta(viewModel.getTablero())){

                        //HA GANADO
                        utilidad.mostrarToast(getString((R.string.isCorrect)), getContext());
                        //Para el cronometro
                        crono.stop();
                        //Guarda la partida en la BBDD
                        long milisegundosQueHanPasado = SystemClock.elapsedRealtime() - crono.getBase();
                        Map<String, Object> map = new HashMap<>();
                        map.put("idUser", usuarioActual.getId());
                        map.put("nickname", viewModel.getUsuarioActual().getValue().getNickname());
                        map.put("email", viewModel.getUsuarioActual().getValue().getEmail());
                        map.put("timeInMilis", milisegundosQueHanPasado);
                        map.put("level", utilidad.getLevelName(viewModel.getLado().getValue()));
                        evaluateBtn.setEnabled(false);


                        databaseReference.child("Games").
                                child(usuarioActual.getId()).   //lo anidara en las partidas del usuario
                                push(). //le pondra una id a la partida
                                setValue(map).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task2) {
                                        if(task2.isSuccessful()){
                                            Toast.makeText(getContext(),
                                                    "Se han registrado los datos de la partida",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    }else{
                        utilidad.mostrarToast(getString((R.string.isWrong)), getContext());
                    }

                    break;
                case R.id.refreshBtn:

                    builder = new AlertDialog.Builder(getContext());
                    //pongo el titulo y los botones
                    builder.setTitle(R.string.refresh);
                    builder.setMessage(R.string.dialog_clean)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Clean !
                                    Utilidad util = new Utilidad();
                                    util.limpiarPuntosNegrosVisualesDeLaVista(viewModel.getTablero(), layout);
                                    util.mostrarToast(getString(R.string.refresh), getContext());
                                }
                            })
                            .setNegativeButton(R.string.cancel, null);

                    //lo muestro
                    dialog = builder.create();
                    dialog.show();



                    break;
                case R.id.newGameBtn:
                    builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.newGame);
                    builder.setMessage(R.string.dialog_newGame)
                            .setPositiveButton(R.string.dialog_newGame_start, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Start new game!
                                    Utilidad util = new Utilidad();
                                    util.partidaNueva(viewModel.getTablero(), layout);
                                    util.mostrarToast(getString(R.string.newGame), getContext());
                                    crono.stop();
                                    crono.setBase(SystemClock.elapsedRealtime());   //lo pone a 0
                                    crono.start();
                                    evaluateBtn.setEnabled(true);

                                }
                            })
                            .setNegativeButton(R.string.cancel, null);
                    //lo muestro
                    dialog = builder.create();
                    dialog.show();
                    break;

            }
        }

    }

    /*Este método lo que hace es establecer el tablero en el XML
     * O sea pinta las casillas con los numeritos correspondientes
     * y la marca negra si la casilla la tiene o no (esto sirve
     * para cuando cambia la configuracion)
     * */
    public void establecerTablero(ConstraintLayout layout, Context context, SparseIntArray mapeo, Tablero tablero){

        //lo he tenido que poner como view porque van a ser ImageView o TextView dependiendo de donde esten
        View view;
        ConstraintLayout.LayoutParams lp;
        int id ;
        int contador = 0;
        int contadorCol = 0;
        int contadorRow = 0;
        int[][] idArray = new int[tablero.getLado()][tablero.getLado()];    //lo necesito abajo
        ConstraintSet cs = new ConstraintSet();

        // Add our views to the ConstraintLayout.
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {

                lp = new ConstraintLayout.LayoutParams(ConstraintSet.MATCH_CONSTRAINT,
                        ConstraintSet.MATCH_CONSTRAINT);

                id = View.generateViewId();     //es unico por eso tenias el fallo de que se acumulaban (y es necesario tb)

                mapeo.append(contador, id); //sistema de clave- valor solo que ambos son int (pa este caso el SparseIntArray es mejor que el hash map)
                contador ++;

                tablero.getCasillas()[iRow][iCol].setId(id);
                idArray[iRow][iCol] = id;   //lo necesito abajo


                if(iRow != 0 || iCol != 0)
                {
                    if(contador <= tablero.getLado() ){
                        view = new TextView(context);
                        //((TextView) view).setText(("H"));

                        ((TextView)view).setText(String.valueOf(tablero.getMarcasHorizontales()[contadorCol]));
                        contadorCol++;
                        ((TextView) view).setGravity(Gravity.CENTER);
                        ((TextView) view).setTextColor(Color.BLACK);

                        //Estas casillas son donde se ponen los numeros
                        tablero.getCasillas()[iRow][iCol].setJugable(false);

                    }else if( (contador-1) % tablero.getLado() == 0){ //er gitaneo weno pa que me pille la primera casilla de cada fila
                        view = new TextView(context);
                        //((TextView)view).setText(("V"));

                        ((TextView)view).setText(String.valueOf(tablero.getMarcasVerticales()[contadorRow]));
                        contadorRow++;

                        ((TextView) view).setGravity(Gravity.CENTER);
                        ((TextView) view).setTextColor(Color.BLACK);
                        //Estas casillas son donde se ponen los numeros
                        tablero.getCasillas()[iRow][iCol].setJugable(false);
                    }else{
                        view = new ImageView(context);
                        view.setPaddingRelative(10,10,10,10);
                        //Esta parte la querria hacer sin esto, solo usando los atributos isMarcada
                        //-> Ver metodo onClick para saber por que no lo hice asi

                        ((ImageView)view).setImageResource(tablero.getCasillas()[iRow][iCol].getImgSrc());



                        //view.setTag(R.drawable.nonselecteditem);

                        //Estas casillas son donde se ponen las marcas
                        tablero.getCasillas()[iRow][iCol].setJugable(true);

                    }
                    view.setId(id);
                    view.setBackgroundResource(R.drawable.border_shape);

                    layout.addView(view, lp);
                }else{
                    view = new View(context);
                    view.setId(id);
                    view.setBackgroundResource(R.drawable.border_shape);

                    layout.addView(view, lp);
                }


            }
        }

        // Create horizontal chain for each row and set the 1:1 dimensions.
        // but first make sure the layout frame has the right ratio set.
        cs.clone(layout);
        cs.setDimensionRatio(R.id.gridFrame, tablero.getLado() + ":" + tablero.getLado());
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {
                id = tablero.getCasillas()[iRow][iCol].getId();
                cs.setDimensionRatio(id, "1:1");
                if (iRow == 0) {
                    // Connect the top row to the top of the frame.
                    cs.connect(id, ConstraintSet.TOP, R.id.gridFrame, ConstraintSet.TOP);
                } else {
                    // Connect top to bottom of row above.
                    cs.connect(id, ConstraintSet.TOP, tablero.getCasillas()[iRow - 1][0].getId(), ConstraintSet.BOTTOM);
                }
            }
            // Create a horiontal chain that will determine the dimensions of our squares.
            // Could also be createHorizontalChainRtl() with START/END.
            cs.createHorizontalChain(R.id.gridFrame, ConstraintSet.LEFT,
                    R.id.gridFrame, ConstraintSet.RIGHT,
                    idArray[iRow], null, ConstraintSet.CHAIN_PACKED);
        }

        cs.applyTo(layout);

    }

}

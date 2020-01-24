package es.iesnervion.avazquez.juegocasillasynumeros.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProviders;

import es.iesnervion.avazquez.juegocasillasynumeros.Clases.Casilla;
import es.iesnervion.avazquez.juegocasillasynumeros.Clases.Tablero;
import es.iesnervion.avazquez.juegocasillasynumeros.MyViewModelFactory;
import es.iesnervion.avazquez.juegocasillasynumeros.R;
import es.iesnervion.avazquez.juegocasillasynumeros.Utilidad.Utilidad;
import es.iesnervion.avazquez.juegocasillasynumeros.ViewModel.TableroViewModel;

public class TableroActivity extends AppCompatActivity implements View.OnClickListener {

    TableroViewModel viewModel;
    Button evaluateBtn;
    Button refreshBtn;
    Button newGameBtn;
    ConstraintLayout layout;
    int lado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablero);

        Intent mIntent = getIntent();
        lado = mIntent.getIntExtra("lado",0);

        layout = findViewById(R.id.layout);
        evaluateBtn = findViewById(R.id.evaluateBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        newGameBtn = findViewById(R.id.newGameBtn);
        //He tenido que hacer mi propio VM Factory pa pode pasarle parametros al VM
        viewModel = ViewModelProviders.of(this, new MyViewModelFactory(this.getApplication(), lado)).
                get(TableroViewModel.class);


        establecerTablero(layout,this,viewModel.getMapeo(),viewModel.getTablero());
        colocarListeners(layout, viewModel.getTablero(), viewModel.getMapeo());
        evaluateBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        newGameBtn.setOnClickListener(this);

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
                        utilidad.mostrarToast(getString((R.string.isCorrect)), this);
                    }else{
                        utilidad.mostrarToast(getString((R.string.isWrong)), this);
                    }

                    break;
                case R.id.refreshBtn:

                    builder = new AlertDialog.Builder(this);
                    //pongo el titulo y los botones
                    builder.setTitle(R.string.refresh);
                    builder.setMessage(R.string.dialog_clean)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Clean !
                                    Utilidad util = new Utilidad();
                                    util.limpiarPuntosNegrosVisualesDeLaVista(viewModel.getTablero(), layout);
                                    util.mostrarToast(getString(R.string.refresh), getApplicationContext());
                                }
                            })
                            .setNegativeButton(R.string.cancel, null);

                    //lo muestro
                    dialog = builder.create();
                    dialog.show();



                    break;
                case R.id.newGameBtn:
                    builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.newGame);
                    builder.setMessage(R.string.dialog_newGame)
                            .setPositiveButton(R.string.dialog_newGame_start, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // Start new game!
                                    Utilidad util = new Utilidad();
                                    util.partidaNueva(viewModel.getTablero(), layout);
                                    util.mostrarToast(getString(R.string.newGame), getApplicationContext());

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

package es.iesnervion.avazquez.juegocasillasynumeros.ViewModel;


import android.app.Application;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModel;

import es.iesnervion.avazquez.juegocasillasynumeros.Clases.Tablero;
import es.iesnervion.avazquez.juegocasillasynumeros.R;
import es.iesnervion.avazquez.juegocasillasynumeros.Utilidad.Utilidad;


public class TableroViewModel extends ViewModel {

    private Tablero tablero;
    private SparseIntArray mapeo = new SparseIntArray();
    private Context context;

    public TableroViewModel(Application application,int lado) {
        this.context = application.getBaseContext();
        this.tablero = new Tablero(lado);
        Utilidad utilidad = new Utilidad();
        //establezco cuales son las casillas jugables
        utilidad.establecerCasillasJugablesTablero(tablero);
        //pone las marcas (esto el usuario no lo ve, es mi forma de hacer los numeritos)
        utilidad.establecerMarcasEnTablero(tablero);
        //se cuentan los numeritos xD
        utilidad.establecerNumeroDeMarcasHorizontalesYVerticales(tablero);



    }





    public SparseIntArray getMapeo() {
        return mapeo;
    }

    public void setMapeo(SparseIntArray mapeo) {
        this.mapeo = mapeo;
    }

    public Tablero getTablero() {
        return tablero;
    }

}

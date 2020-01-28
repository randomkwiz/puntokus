package es.iesnervion.avazquez.puntokus.viewModels;

import android.app.Application;
import android.content.Context;
import android.util.SparseIntArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import es.iesnervion.avazquez.puntokus.entities.Tablero;
import es.iesnervion.avazquez.puntokus.util.Utilidad;


//Nota para mi misma:
/*
* A ver, que necesitas tocar el factory porque tienes que pasarle
* por parametro el lado
* y como tienes que pasarle parametro, no puedes extender
* de AndroidViewModel
* por eso le pasas tb el contexto
* No lo cambies mas
* */

public class TableroViewModel extends ViewModel {
    private Tablero tablero;
    private SparseIntArray mapeo = new SparseIntArray();
    MutableLiveData<Boolean> isGoingToPlay;
    MutableLiveData<Integer> lado;


    public TableroViewModel() {

        this.isGoingToPlay = new MutableLiveData<>();

        this.lado = new MutableLiveData<>();
    }

    public void inicializarPartida(){
        if(lado.getValue()!=null ){
            this.tablero = new Tablero(lado.getValue());
            Utilidad utilidad = new Utilidad();
            //establezco cuales son las casillas jugables
            utilidad.establecerCasillasJugablesTablero(tablero);
            //pone las marcas (esto el usuario no lo ve, es mi forma de hacer los numeritos)
            utilidad.establecerMarcasEnTablero(tablero);
            //se cuentan los numeritos xD
            utilidad.establecerNumeroDeMarcasHorizontalesYVerticales(tablero);
        }

    }


    public LiveData<Integer> getLado() {
        return lado;
    }

    public void setLado(int lado) {
        this.lado.setValue(lado);
    }

    public LiveData<Boolean> getIsGoingToPlay() {
        return isGoingToPlay;
    }

    public void setIsGoingToPlay(Boolean isGoingToPlay) {
        if(isGoingToPlay){
            this.isGoingToPlay.setValue(isGoingToPlay);
            this.isGoingToPlay.setValue(false);
        }

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

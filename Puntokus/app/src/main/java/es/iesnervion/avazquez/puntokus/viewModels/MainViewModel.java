package es.iesnervion.avazquez.puntokus.viewModels;

import android.app.Application;
import android.content.Context;
import android.util.SparseIntArray;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import es.iesnervion.avazquez.puntokus.entities.Tablero;
import es.iesnervion.avazquez.puntokus.entities.User;
import es.iesnervion.avazquez.puntokus.util.Utilidad;


public class MainViewModel extends ViewModel {
    private Tablero tablero;
    private SparseIntArray mapeo = new SparseIntArray();
    private MutableLiveData<Boolean> isGoingToPlay;
    private MutableLiveData<Integer> lado;
    private MutableLiveData<User> usuarioActual;
    private MutableLiveData<Boolean> userWantToExit;
    private MutableLiveData<Boolean> userWantToGoBack;
    //MutableLiveData<String> currentFragment;
    private MutableLiveData<Boolean> showDialog;
    private MutableLiveData<Long> timeInMilis;
    private MutableLiveData<Boolean> isCronoStopped;

    public MainViewModel() {

        this.isGoingToPlay = new MutableLiveData<>();
        this.userWantToExit = new MutableLiveData<>();
        this.userWantToGoBack = new MutableLiveData<>();
        this.showDialog = new MutableLiveData<>();
        this.lado = new MutableLiveData<>();
        this.usuarioActual = new MutableLiveData<>();
        this.timeInMilis = new MutableLiveData<>();
        this.isCronoStopped = new MutableLiveData<>();

        this.timeInMilis.setValue((long)0);
       // this.currentFragment = new MutableLiveData<>();
        this.usuarioActual.setValue(new User());
        this.userWantToExit.setValue(false);
        this.userWantToGoBack.setValue(false);
       // this.currentFragment.setValue("");
        this.showDialog.setValue(false);
        this.isCronoStopped.setValue(false);
    }


    public void inicializarPartida() {
        if (lado.getValue() != null) {
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

    public LiveData<Boolean> getIsCronoStopped() {
        return isCronoStopped;
    }

    public void setIsCronoStopped(Boolean isCronoStopped) {
        this.isCronoStopped.setValue(isCronoStopped);
    }

    public LiveData<Long> getTimeInMilis() {
        return timeInMilis;
    }

    public void setTimeInMilis(Long timeInMilis) {
        this.timeInMilis.setValue(timeInMilis);
    }

    public LiveData<Boolean> getShowDialog() {
        return showDialog;
    }

    public void setShowDialog(Boolean showDialog) {
        if(showDialog){
            this.showDialog.setValue(showDialog);
            this.showDialog.setValue(false);
        }

    }
//
//    public LiveData<String> getCurrentFragment() {
//        return currentFragment;
//    }
//
//    public void setCurrentFragment(String currentFragment) {
//        this.currentFragment.setValue(currentFragment);
//    }

    public void resetTime(){
        this.setTimeInMilis((long)0);
    }
    public LiveData<Boolean> getUserWantToGoBack() {
        return userWantToGoBack;
    }

    public void setUserWantToGoBack(Boolean userWantToGoBack) {
        if (userWantToGoBack) {
            this.userWantToGoBack.setValue(userWantToGoBack);
            this.userWantToGoBack.setValue(false);
        }
    }

    public LiveData<Boolean> getUserWantToExit() {
        return userWantToExit;
    }

    public void setUserWantToExit(Boolean userWantToExit) {
        if (userWantToExit) {
            this.userWantToExit.setValue(userWantToExit);
            this.userWantToExit.setValue(false);
        }

    }

    public LiveData<User> getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(User usuarioActual) {
        this.usuarioActual.setValue(usuarioActual);
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
        if (isGoingToPlay) {
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

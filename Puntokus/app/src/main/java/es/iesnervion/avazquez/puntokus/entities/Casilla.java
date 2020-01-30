package es.iesnervion.avazquez.puntokus.entities;


import es.iesnervion.avazquez.puntokus.R;

public class Casilla {
    private boolean isJugable;
    private boolean isMarcada;
    private int id;
    private int numeroAMostrar;
    private int imgSrc;

    public Casilla(boolean isMarcada, int id) {
        this.isMarcada = isMarcada;
        this.id = id;
        this.isJugable = false;
        this.numeroAMostrar = 0;
        this.imgSrc = R.drawable.nonselecteditem;
    }

    public Casilla(boolean isMarcada) {
        this.isMarcada = isMarcada;
        this.id = -1;
        this.isJugable = false;
        this.numeroAMostrar = 0;
        this.imgSrc = R.drawable.nonselecteditem;
    }

    public Casilla() {
        this.isMarcada = false;
        this.id = -1;
        this.isJugable = false;
        this.numeroAMostrar = 0;
        this.imgSrc = R.drawable.nonselecteditem;
    }

    public boolean isMarcada() {
        return isMarcada;
    }

    public void setMarcada(boolean marcada) {
        isMarcada = marcada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isJugable() {
        return isJugable;
    }

    public void setJugable(boolean jugable) {
        isJugable = jugable;
    }

    public int getNumeroAMostrar() {
        return numeroAMostrar;
    }

    public void setNumeroAMostrar(int numeroAMostrar) {
        this.numeroAMostrar = numeroAMostrar;
    }

    public int getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.imgSrc = imgSrc;
    }
}

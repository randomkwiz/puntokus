package es.iesnervion.avazquez.puntokus.adapter.viewHolder;

import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder {

    private ImageView imgMedalla;
    private TextView nickname;
    private TextView dificultad;
    private TextView tiempo;

    public MyViewHolder(ImageView imgMedalla, TextView nickname, TextView dificultad, TextView tiempo) {
        this.imgMedalla = imgMedalla;
        this.nickname = nickname;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
    }

    public ImageView getImgMedalla() {
        return imgMedalla;
    }

    public void setImgMedalla(ImageView imgMedalla) {
        this.imgMedalla = imgMedalla;
    }

    public TextView getNickname() {
        return nickname;
    }

    public void setNickname(TextView nickname) {
        this.nickname = nickname;
    }

    public TextView getDificultad() {
        return dificultad;
    }

    public void setDificultad(TextView dificultad) {
        this.dificultad = dificultad;
    }

    public TextView getTiempo() {
        return tiempo;
    }

    public void setTiempo(TextView tiempo) {
        this.tiempo = tiempo;
    }
}

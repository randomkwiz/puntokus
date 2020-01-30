package es.iesnervion.avazquez.puntokus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.adapter.viewHolder.MyViewHolder;
import es.iesnervion.avazquez.puntokus.entities.Game;

public class GamesAdapter extends BaseAdapter {

    private ArrayList<Game> listaPartidas;
    private Context context;

    public GamesAdapter(ArrayList<Game> listaPartidas, Context context) {
        this.listaPartidas = listaPartidas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaPartidas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Game partida = listaPartidas.get(position);
        MyViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.ranking_game_row, parent,false);

            //referencias

            ImageView imgMedalla = (ImageView) convertView.findViewById(R.id.lblImgPrize);

            TextView nickname = (TextView) convertView.findViewById(R.id.lblNickname);
            TextView dificultad = (TextView) convertView.findViewById(R.id.lblDificultad);
            TextView tiempo = (TextView) convertView.findViewById(R.id.lblTime);

            nickname.setText(partida.getNickname());
            dificultad.setText(partida.getLevel());
            tiempo.setText(partida.getTiempoFormateado());

            if(position >= 0 && position <= 2 ){
                imgMedalla.setVisibility(View.VISIBLE);
                switch (position){
                    case 0:
                        imgMedalla.setImageResource(R.drawable.ic_first);
                        break;
                    case 1:
                        imgMedalla.setImageResource(R.drawable.ic_second);
                        break;
                    case 2:
                        imgMedalla.setImageResource(R.drawable.ic_third);
                        break;
                }
            }else{
                imgMedalla.setVisibility(View.GONE);
            }

            viewHolder = new MyViewHolder(imgMedalla, nickname, dificultad, tiempo);
            convertView.setTag(viewHolder );
        }else{
            viewHolder = (MyViewHolder) convertView.getTag();
        }


        if(position >= 0 && position <= 2 ){
            viewHolder.getImgMedalla().setVisibility(View.VISIBLE);
            switch (position){
                case 0:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_first);
                    break;
                case 1:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_second);
                    break;
                case 2:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_third);
                    break;
            }
        }else{
            viewHolder.getImgMedalla().setVisibility(View.GONE);
        }

        viewHolder.getDificultad().setText(partida.getLevel());
        viewHolder.getNickname().setText(partida.getNickname());
        viewHolder.getTiempo().setText(partida.getTiempoFormateado());


        return convertView;
    }
}

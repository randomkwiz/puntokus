package es.iesnervion.avazquez.puntokus.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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
            //TextView dificultad = (TextView) convertView.findViewById(R.id.lblDificultad);
            TextView tiempo = (TextView) convertView.findViewById(R.id.lblTime);

            nickname.setText(partida.getNickname());
            //dificultad.setText(partida.getLevel());
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

            viewHolder = new MyViewHolder(imgMedalla, nickname, tiempo);
            convertView.setTag(viewHolder );
        }else{
            viewHolder = (MyViewHolder) convertView.getTag();
        }


        if(position >= 0 && position <= 2 ){
            viewHolder.getImgMedalla().setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.lblNickname)).setTextColor(Color.parseColor("#000000"));
            ((TextView) convertView.findViewById(R.id.lblTime)).setTextColor(Color.parseColor("#000000"));
            switch (position){
                case 0:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_first);

                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        convertView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.gold)));
                    }else{
                        convertView.setBackgroundColor(Color.parseColor("#D4AF37"));

                    }

                     break;
                case 1:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_second);

                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        convertView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.silver)));
                    }else{
                        convertView.setBackgroundColor(Color.parseColor("#C0C0C0"));

                    }


                    break;
                case 2:
                    viewHolder.getImgMedalla().setImageResource(R.drawable.ic_third);

                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        convertView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.bronze)));
                    }else{
                        convertView.setBackgroundColor(Color.parseColor("#cd7f32"));
                    }
                    break;
            }
        }else{
        viewHolder.getImgMedalla().setVisibility(View.GONE);
            if(position%2 == 0){


                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    convertView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.sombra)));
                }else{
                    convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                ((TextView) convertView.findViewById(R.id.lblNickname)).setTextColor(Color.parseColor("#666666"));
                ((TextView) convertView.findViewById(R.id.lblTime)).setTextColor(Color.parseColor("#666666"));
            }else{

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    convertView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.grease)));
                }else{
                    convertView.setBackgroundColor(Color.parseColor("#666666"));
                }


                ((TextView) convertView.findViewById(R.id.lblNickname)).setTextColor(Color.parseColor("#FFFFFF"));
                ((TextView) convertView.findViewById(R.id.lblTime)).setTextColor(Color.parseColor("#FFFFFF"));
            }
        }



        //viewHolder.getDificultad().setText(partida.getLevel());
        viewHolder.getNickname().setText(partida.getNickname());
        viewHolder.getTiempo().setText(partida.getTiempoFormateado());


        return convertView;
    }
}

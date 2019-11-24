package es.iesnervion.avazquez.juegocasillasynumeros.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.iesnervion.avazquez.juegocasillasynumeros.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//cantidad de casillas segun el nivel de dificultad
    final int EASY = 5;
    final int NORMAL = 9 ;
    final int HARD = 11 ;
    final int SICK = 15 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button easyBtn = findViewById(R.id.view1);
        Button normalBtn = findViewById(R.id.view2);
        Button hardBtn = findViewById(R.id.view3);
        Button customBtn = findViewById(R.id.view4);

        easyBtn.setOnClickListener(this);
        normalBtn.setOnClickListener(this);
        hardBtn.setOnClickListener(this);
        customBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {


        Intent i = new Intent(this, TableroActivity.class);
        int lado = 0;
        switch (v.getId()){
            case R.id.view1:
                lado = EASY;
                break;
            case R.id.view2:
                lado = NORMAL;
                break;
            case R.id.view3:
                lado = HARD;
                break;
            case R.id.view4:
                lado = SICK;
                break;
        }
        i.putExtra("lado", lado);
        startActivity(i);

    }


}
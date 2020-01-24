package es.iesnervion.avazquez.puntokus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.fragments.AccountFragment;
import es.iesnervion.avazquez.puntokus.fragments.PlayFragment;
import es.iesnervion.avazquez.puntokus.fragments.RankingFragment;

public class SecondMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.menu_nav_bottom)
    BottomNavigationView bottomNavigationView;
    AccountFragment accountFragment;
    PlayFragment playFragment;
    RankingFragment rankingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        accountFragment = new AccountFragment();
        playFragment = new PlayFragment();
        rankingFragment = new RankingFragment();


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        bottomNavigationView.setSelectedItemId(R.id.menu_play);
        fragmentTransaction.replace(R.id.fragmentSecondActivity, playFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.menu_account:
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSecondActivity, accountFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                break;
            case R.id.menu_play:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentSecondActivity, playFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case R.id.menu_ranking:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentSecondActivity, rankingFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

        }

        return true;
    }
}

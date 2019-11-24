package es.iesnervion.avazquez.juegocasillasynumeros;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import es.iesnervion.avazquez.juegocasillasynumeros.ViewModel.TableroViewModel;

public class MyViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;

    private int lado;


    public MyViewModelFactory(Application mApplication,  int lado) {
        this.mApplication = mApplication;

        this.lado = lado;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        return (T) new TableroViewModel(mApplication, lado);
    }
}

package es.iesnervion.avazquez.puntokus.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import es.iesnervion.avazquez.puntokus.entities.User;

public class AutenticacionViewModel extends ViewModel {

    private MutableLiveData<Boolean> goToSignUp;
    private MutableLiveData<Boolean> goToLogIn;
    private MutableLiveData<Boolean> isCorrectLogin;
    private MutableLiveData<User> user;
    private MutableLiveData<Boolean> userWantToExit;

    public AutenticacionViewModel() {

        this.goToSignUp = new MutableLiveData<>();
        this.goToLogIn = new MutableLiveData<>();
        this.isCorrectLogin = new MutableLiveData<>();
        this.userWantToExit = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
        this.user.setValue(new User());
        this.userWantToExit.setValue(false);
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

    public LiveData<Boolean> getIsCorrectLogin() {
        return isCorrectLogin;
    }

    public void setIsCorrectLogin(boolean isCorrectLogin) {
        if (isCorrectLogin) {
            this.isCorrectLogin.setValue(isCorrectLogin);
            this.isCorrectLogin.setValue(false);
        }

    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public LiveData<Boolean> getGoToLogIn() {
        return goToLogIn;
    }

    public void setGoToLogIn(boolean goToLogIn) {
        if (goToLogIn) {
            this.goToLogIn.setValue(goToLogIn);
            this.goToLogIn.setValue(false);
        }
    }

    public LiveData<Boolean> getGoToSignUp() {
        return goToSignUp;
    }

    public void setGoToSignUp(boolean goToSignUp) {
        if (goToSignUp) {
            this.goToSignUp.setValue(goToSignUp);
            this.goToSignUp.setValue(false);
        }

    }
}

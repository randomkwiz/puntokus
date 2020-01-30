package es.iesnervion.avazquez.puntokus.entities;

import java.util.concurrent.TimeUnit;

public class Game implements Comparable<Game>{
    private String email;
    private String nickname;
    private String idUser;
    private String level;
    private long timeInMilis;

    public Game(String email, String nickname, String idUser, String level, long timeInMilis) {
        this.email = email;
        this.nickname = nickname;
        this.idUser = idUser;
        this.level = level;
        this.timeInMilis = timeInMilis;
    }

    public Game() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getTimeInMilis() {
        return timeInMilis;
    }

    public void setTimeInMilis(long timeInMilis) {
        this.timeInMilis = timeInMilis;
    }

    public String getTiempoFormateado(){
        return String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(getTimeInMilis()),
                TimeUnit.MILLISECONDS.toSeconds(getTimeInMilis()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTimeInMilis()))
        );
    }


    @Override
    public String toString() {
        return getEmail()+", "+getLevel() + ": "+String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(getTimeInMilis()),
                TimeUnit.MILLISECONDS.toSeconds(getTimeInMilis()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTimeInMilis()))
        );
    }


    /*
    * -1 si es menor
    * 0 si es igual
    * 1 si es mayor
    *
    * */
    @Override
    public int compareTo(Game o) {
        int ret = 0;
        if(this.getTimeInMilis() > o.getTimeInMilis()){
            ret = 1;
        }else if(this.getTimeInMilis() < o.getTimeInMilis()){
            ret = -1;
        }

        return ret;
    }
}

package es.iesnervion.avazquez.puntokus.entities;

public class User {
    private String id;
    private String nickname;
    private String email;
    private String password;

    public User(String id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public User(String nickname, String email, String password) {
        this.id  = "";
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public User() {
        this.id  = "";
        this.nickname = "";
        this.email = "";
        this.password = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

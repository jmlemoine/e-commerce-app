package pucmm.temas.especiales.e_commerce_app.entities;

public class Token {
    private String email;
    private String  uuid;
    private String token;

    public Token() { }

    public Token(String email, String uuid, String token) {
        this.email = email;
        this.uuid = uuid;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

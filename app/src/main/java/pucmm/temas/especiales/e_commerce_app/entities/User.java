package pucmm.temas.especiales.e_commerce_app.entities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

public class User implements Serializable{
    private int id;
    private String name;
    private String user;
    private String password;
    private String email;
    private boolean isProvider;
    private String contact;
    private long createdAt;
    private long updatedAt;
    private String photo;
    private int token;

    public User() {
    }

    public User(String name, String user, String email, String password, String contact, String photo, boolean isProvider){
        this.name = name;
        this.user = user;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.photo = photo;
        this.isProvider = isProvider;
    }

    public User(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public User(String name, String user, String password, String email, boolean isProvider, String contact) {
        this.name = name;
        this.user = user;
        this.password = password;
        this.email = email;
        this.isProvider = isProvider;
        this.contact = contact;
    }

    public User(JSONObject json) {
        UserConverter.fromJson(json, this);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            UserConverter.toJson(this, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getUser() {
        return user;
    }

    public User setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isIsProvider() {
        return isProvider;
    }

    public User setIsProvider(boolean isProvider) {
        this.isProvider = isProvider;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public User setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public User setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public int getToken() {
        return token;
    }

    public User setToken(int token) {
        this.token = token;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    static class UserConverter {

        public static void fromJson(JSONObject json, User obj) {
            Iterator<String> keys = json.keys();
            try {
                while (keys.hasNext()) {
                    String key = keys.next();

                    switch (key) {
                        case "contact":
                            if (json.get(key) instanceof String) {
                                obj.setContact((String) json.get(key));
                            }
                            break;
                        case "createdAt":
                            if (json.get(key) instanceof Number) {
                                obj.setCreatedAt(((Number) json.get(key)).longValue());
                            }
                            break;
                        case "email":
                            if (json.get(key) instanceof String) {
                                obj.setEmail((String) json.get(key));
                            }
                            break;
                        case "id":
                            if (json.get(key) instanceof Number) {
                                obj.setId(((Number) json.get(key)).intValue());
                            }
                            break;
                        case "isProvider":
                            if (json.get(key) instanceof Boolean) {
                                obj.setIsProvider((Boolean) json.get(key));
                            }
                            break;
                        case "name":
                            if (json.get(key) instanceof String) {
                                obj.setName((String) json.get(key));
                            }
                            break;
                        case "password":
                            if (json.get(key) instanceof String) {
                                obj.setPassword((String) json.get(key));
                            }
                            break;
                        case "photo":
                            if (json.get(key) instanceof String) {
                                obj.setPhoto((String) json.get(key));
                            }
                            break;
                        case "token":
                            if (json.get(key) instanceof Number) {
                                obj.setToken(((Number) json.get(key)).intValue());
                            }
                            break;
                        case "updatedAt":
                            if (json.get(key) instanceof Number) {
                                obj.setUpdatedAt(((Number) json.get(key)).longValue());
                            }
                            break;
                        case "user":
                            if (json.get(key) instanceof String) {
                                obj.setUser((String) json.get(key));
                            }
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void toJson(User obj, JSONObject json) throws JSONException {
            if (obj.getContact() != null) {
                json.put("contact", obj.getContact());
            }
            json.put("createdAt", obj.getCreatedAt());
            if (obj.getEmail() != null) {
                json.put("email", obj.getEmail());
            }
            json.put("id", obj.getId());
            json.put("isProvider", obj.isIsProvider());
            if (obj.getName() != null) {
                json.put("name", obj.getName());
            }
            if (obj.getPassword() != null) {
                json.put("password", obj.getPassword());
            }
            if (obj.getPhoto() != null) {
                json.put("photo", obj.getPhoto());
            }
            json.put("token", obj.getToken());
            json.put("updatedAt", obj.getUpdatedAt());
            if (obj.getUser() != null) {
                json.put("user", obj.getUser());
            }
        }
    }
}

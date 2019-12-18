package pucmm.temas.especiales.e_commerce_app.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

public class Category implements Serializable {
    private int id;
    private int userId;
    private String name;
    private long createdAt;
    private long updatedAt;
    private boolean active;
    private String photo;
    private int token;

    public Category() {
    }

    public Category(int id) {
        this.id = id;
    }

    public Category(JSONObject json) {
        CategoryConverter.fromJson(json, this);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            CategoryConverter.toJson(this, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category user = (Category) o;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return name;
    }

    static class CategoryConverter {

        public static void fromJson(JSONObject json, Category obj) {

            Iterator<String> keys = json.keys();
            try {
                while (keys.hasNext()) {
                    String key = keys.next();

                    switch (key) {
                        case "id":
                            if (json.get(key) instanceof Number) {
                                obj.setId(((Number) json.get(key)).intValue());
                            }
                            break;
                        case "userId":
                            if (json.get(key) instanceof Number) {
                                obj.setUserId(((Number) json.get(key)).intValue());
                            }
                            break;
                        case "createdAt":
                            if (json.get(key) instanceof Number) {
                                obj.setCreatedAt(((Number) json.get(key)).longValue());
                            }
                            break;
                        case "updatedAt":
                            if (json.get(key) instanceof Number) {
                                obj.setUpdatedAt(((Number) json.get(key)).longValue());
                            }
                            break;
                        case "name":
                            if (json.get(key) instanceof String) {
                                obj.setName((String) json.get(key));
                            }
                            break;

                        case "active":
                            if (json.get(key) instanceof Boolean) {
                                obj.setActive((Boolean) json.get(key));
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
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
            private String photo;
            private int token;
        * */
        public static void toJson(Category obj, JSONObject json) throws JSONException {
            json.put("id", obj.getId());
            json.put("userId", obj.getUserId());
            json.put("createdAt", obj.getCreatedAt());
            json.put("updatedAt", obj.getUpdatedAt());
            json.put("active", obj.isActive());
            json.put("token", obj.getToken());

            if (obj.getName() != null) {
                json.put("name", obj.getName());
            }
            if (obj.getName() != null) {
                json.put("photo", obj.getPhoto());
            }
        }
    }
}

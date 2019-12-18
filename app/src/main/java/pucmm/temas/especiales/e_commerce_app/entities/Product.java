package pucmm.temas.especiales.e_commerce_app.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;

public class Product  implements Serializable {
    private String itemCode;
    private String itemName;
    private int categoryId;
    private int userId;
    private double price;
    private long createdAt;
    private long updatedAt;
    private boolean active;
    private String photo;
    private int token;

    public Product() {
    }

    public Product(JSONObject json) {
        ProductConverter.fromJson(json, this);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            ProductConverter.toJson(this, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return getItemCode().equals(product.getItemCode());
    }

    @Override
    public int hashCode() {
        return getItemCode().hashCode();
    }

    static class ProductConverter {

        public static void fromJson(JSONObject json, Product obj) {

            Iterator<String> keys = json.keys();
            try {
                while (keys.hasNext()) {
                    String key = keys.next();
                    switch (key) {
                        case "itemCode":
                            if (json.get(key) instanceof String) {
                                obj.setItemCode((String) json.get(key));
                            }
                            break;
                        case "itemName":
                            if (json.get(key) instanceof String) {
                                obj.setItemName((String) json.get(key));
                            }
                            break;
                        case "price":
                            if (json.get(key) instanceof Number) {
                                obj.setPrice(((Number) json.get(key)).intValue());
                            }
                            break;
                        case "categoryId":
                            if (json.get(key) instanceof Number) {
                                obj.setCategoryId(((Number) json.get(key)).intValue());
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

        public static void toJson(Product obj, JSONObject json) throws JSONException {
            json.put("categoryId", obj.getCategoryId());
            json.put("userId", obj.getUserId());
            json.put("price", obj.getPrice());
            json.put("createdAt", obj.getCreatedAt());
            json.put("updatedAt", obj.getCreatedAt());
            json.put("active", obj.isActive());
            json.put("token", obj.getToken());

            if (obj.getItemCode() != null) {
                json.put("itemCode", obj.getItemCode());
            }
            if (obj.getItemName() != null) {
                json.put("itemName", obj.getItemName());
            }
            if (obj.getPhoto() != null) {
                json.put("photo", obj.getPhoto());
            }
        }
    }
}

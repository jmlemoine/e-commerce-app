package pucmm.temas.especiales.e_commerce_app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pucmm.temas.especiales.e_commerce_app.LoginActivity;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;

public class UserSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Context context;

    public static final String CARTS = "carts";
    public static final String KEY_QTY = "qty";

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserSessionPref";

    public UserSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

//    create login session
    public void createLoginSession(long id, String email, String user, String name, long token, boolean isProvider, String photo, String contact){
        editor.putBoolean("isLoggedIn", true);
        editor.putLong("id", id);
        editor.putString("email", email);
        editor.putString("user", user);
        editor.putString("name", name);
        editor.putLong("token", token);
        editor.putBoolean("isProvider", isProvider);
        editor.putString("photo", photo);
        editor.putString("contact", contact);
        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent intent = new Intent(context, LoginActivity.class);
            //close all activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //add new flag to start a new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //starts login activity
            context.startActivity(intent);
        }
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void logoutUser(){
        //clear all data from sharedPreferences
        editor.putBoolean("isLoggedIn", false);
        editor.commit();

        // After logout redirect user to Login Activity
        Intent intent = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(intent);
    }

    public User getUserInformation(){
        User user = new User();
        user.setId((int) sharedPreferences.getLong("id", -1));
        user.setEmail(sharedPreferences.getString("email", null));
        user.setUser(sharedPreferences.getString("user", null));
        user.setName(sharedPreferences.getString("name", null));
        user.setToken((int) sharedPreferences.getLong("token", -1));
        user.setIsProvider(sharedPreferences.getBoolean("isProvider", false));
        user.setPhoto(sharedPreferences.getString("photo", null));
        user.setContact(sharedPreferences.getString("contact", null));
        return user;
    }

    public int getCartCount() {

        JSONArray jsonArr = getCart();
        int count = 0;
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                count += jsonObject.getInt(KEY_QTY);
            }
            return count;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getCartTotal() {
        JSONArray jsonArr = getCart();

        double total = 0;
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                int count = jsonObject.getInt(KEY_QTY);
                Product product = new Product(jsonObject);

                total += (count * product.getPrice());

            }
            return total;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeCart(final int position) {
        JSONArray array = getCart();
        array.remove(position);

        editor.putString(CARTS, array.toString());
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addToCart(final Product product, final int qty) {
        try {
            JSONArray array = getCart();
            JSONObject jsonObject;
            boolean exist = false;

            for (int i = 0; i < array.length(); i++) {
                jsonObject = array.getJSONObject(i);

                Product tmp = new Product(jsonObject);

                if (tmp.getItemCode().equals(product.getItemCode())) {
                    if ((qty + jsonObject.getInt(KEY_QTY)) < 1) {
                        return;
                    }

                    jsonObject.put(KEY_QTY, (qty + jsonObject.getInt(KEY_QTY)));
                    array.remove(i);
                    array.put(jsonObject);
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                jsonObject = product.toJson();
                jsonObject.put(KEY_QTY, qty);
                array.put(jsonObject);
            }

            editor.putString(CARTS, array.toString());
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getCart() {
        try {
            return sortedCart(sharedPreferences.getString(CARTS, "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }


    private JSONArray sortedCart(String jsonArrStr) throws JSONException {

        JSONArray jsonArr = new JSONArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "itemCode";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                } catch (JSONException e) {
                    //do something
                }

                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;

    }
}


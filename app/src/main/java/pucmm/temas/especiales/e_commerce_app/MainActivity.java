package pucmm.temas.especiales.e_commerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.fragments.FragmentNavigationManager;
import pucmm.temas.especiales.e_commerce_app.listener.ReloadBadgeInterface;
import pucmm.temas.especiales.e_commerce_app.receiver.BadgeReceiver;
import pucmm.temas.especiales.e_commerce_app.receiver.BroadcastReceiverManager;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.UserSession;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ReloadBadgeInterface {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private BroadcastReceiver receiver;

    private UserSession session;
    private User user;

    private CircularImageView profile;
    private TextView textCartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();
    }

    private void updateUI() {
//        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        String email = "maximo1225@gmail.com";
//        String password = "!@#$mastermind12";
//
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        Log.e("SplashActivity", "signInWithEmailAndPassword:success");
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("SplashActivity", "signInWithEmailAndPassword:failure");
//                    }
//                });

        FragmentNavigationManager.newInstance(this);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        receiver = new BadgeReceiver(this);
        BroadcastReceiverManager.obtain().setReceiverBadge(receiver);
        this.mToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout,R.string.open, R.string.close);

        navigationView = (NavigationView) findViewById(R.id.nav_viewe);
        navigationView.setCheckedItem(R.id.menu_home);
        navigationView.setNavigationItemSelectedListener(this);

        this.mDrawerLayout.addDrawerListener(this.mToggle);
        this.mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //If there is a session then load the data
        retrieveSession();
    }

    private void retrieveSession() {
        session = new UserSession(this);
        session.checkLogin();
        user = session.getUserInformation();

        //set user information to the drawer header
        View headerView = (View) navigationView.getHeaderView(0);
        profile = headerView.findViewById(R.id.profileMain);
        profile.setOnClickListener(v -> {
            FragmentNavigationManager.obtain().showProfileFragment(user);
            @SuppressLint("WrongViewCast") DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        });
        TextView txtUserInfo = (TextView) headerView.findViewById(R.id.txtUserInfo);
        txtUserInfo.setText(user.getName());
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txtUserEmail);
        txtEmail.setText(user.getEmail());
        FirebaseNetwork.obtain().download(user.getPhoto(),
                (Response.Listener<Bitmap>) response -> profile.setImageBitmap(response),
                error -> Log.e(TAG, error.getMessage()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_home:
                break;
            case R.id.menu_products:
                FragmentNavigationManager.obtain().showProductFragmentList(user, null);
                break;
            case R.id.menu_category:
                FragmentNavigationManager.obtain().showCategoryFragmentList(user);
                break;
            case R.id.menu_tracking:
                FragmentNavigationManager.obtain().showCartFragment(user);
                break;
            case R.id.menu_profile:
                FragmentNavigationManager.obtain().showProfileFragment(user);
                break;
            case R.id.menu_signout:
                session.logoutUser();
                break;
            default:
                navigationView.setCheckedItem(R.id.menu_home);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.nav_cart){
            FragmentNavigationManager.obtain().showCartFragment(user);
            return true;
        }
        if(this.mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        @SuppressLint("WrongViewCast") DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.right_options, menu);

        final MenuItem menuItem = menu.findItem(R.id.nav_cart);
        View actionView = menuItem.getActionView();
//        textCartItemCount = actionView.findViewById(R.id.cart_badge);
//        textCartItemCount = menuItem.getActionView().findViewById(R.id.cart_badge);
//        Log.e("VALOR CARRITO: ", textCartItemCount.getText().toString());

//        textCartItemCount.setOnClickListener(v -> FragmentNavigationManager.obtain().showCartFragment(user));

        reloadBadge();

//        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    @Override
    public void reloadBadge() {
        if (textCartItemCount != null) {

            if (session.getCartCount() == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(session.getCartCount()));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}

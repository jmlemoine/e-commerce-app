package pucmm.temas.especiales.e_commerce_app.fragments;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import pucmm.temas.especiales.e_commerce_app.BuildConfig;
import pucmm.temas.especiales.e_commerce_app.MainActivity;
import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.entities.Category;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;

public final class FragmentNavigationManager {
    private static FragmentNavigationManager sInstance;
    private static MainActivity mActivity;
    private Fragment fragment;
    private FragmentManager mFragmentManager;

    public static FragmentNavigationManager obtain() {
        if (sInstance == null) {
            try {
                throw new Exception("You must create an instance of fragment navigation manager");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sInstance.configure(mActivity);
        return sInstance;
    }

    public static FragmentNavigationManager newInstance(final MainActivity activity) {
        if (sInstance == null) {
            sInstance = new FragmentNavigationManager();
        }
        sInstance.configure(activity);
        return sInstance;
    }

    private void configure(MainActivity activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
    }

    public void showCategoryFragmentList(User user) {
        showFragment(CategoryFragmentList.newInstance(user), false);
    }


    public View showCategoryManagerManager(Category category, User user) {
        showFragment(CategoryFragmentManager.newInstance(category, user), false);
        return null;
    }


    public void showProductFragmentList(User user, Category category) {
        showFragment(ProductFragmentList.newInstance(user, category), false);
    }


    public void showProductFragmentManager(Product product, User user) {
        showFragment(ProductFragmentManager.newInstance(product, user), false);
    }

    public void showProductDetailsFragment(User user, Product product) {
        showFragment(ProductDetailsFragment.newInstance(user, product), false);
    }

    public void showCartFragment(User user) {
        showFragment(CartFragment.newInstance(user), false);
    }


    public void showProfileFragment(User user) {
        showFragment(ProfileFragment.newInstance(user), false);
    }

    private void showFragment(Fragment fragment, boolean allowStateLoss) {
        FragmentManager fm = mFragmentManager;
        this.fragment = fragment;

        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.fragment_container, this.fragment);

        ft.addToBackStack(null);

        if (allowStateLoss || !BuildConfig.DEBUG) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }

        fm.executePendingTransactions();
    }
}

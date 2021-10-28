package com.example.duken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.duken.dialog.EnterCountDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, AddProductFragment.AddProductFragmentListener, EnterCountDialog.EnterCountDialogListener, ShoppingCartFragment.ShoppingCartFragmentListener{

    private BottomNavigationView bottomNavigationView;
    private Fragment addProductFragment, myProfileFragment, activeFragment;
    private SearchFragment searchFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFragment = new SearchFragment();
        addProductFragment = new AddProductFragment();
        shoppingCartFragment = new ShoppingCartFragment();
        myProfileFragment = new MyProfileFragment();

        activeFragment = addProductFragment;

        fragmentManager = getSupportFragmentManager();
        loadFragments();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.add);
    }

    private void loadFragments() {

        fragmentManager.beginTransaction().add(R.id.container, searchFragment).hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, shoppingCartFragment).hide(shoppingCartFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, myProfileFragment).hide(myProfileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, addProductFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                fragmentManager.beginTransaction().hide(activeFragment).show(searchFragment).commit();
                activeFragment = searchFragment;
                return true;

            case R.id.add:
                fragmentManager.beginTransaction().hide(activeFragment).show(addProductFragment).commit();
                activeFragment = addProductFragment;
                return true;

            case R.id.shopping_cart:
                fragmentManager.beginTransaction().hide(activeFragment).show(shoppingCartFragment).commit();
                activeFragment = shoppingCartFragment;
                return true;

            case R.id.my_profile:
                fragmentManager.beginTransaction().hide(activeFragment).show(myProfileFragment).commit();
                activeFragment = myProfileFragment;
                return true;
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void productsUpdated() {
        fragmentManager.beginTransaction().remove(searchFragment).add(R.id.container, searchFragment).hide(searchFragment).commit();
        searchFragment.loadData();
    }

    @Override
    public void setCount(String pieces) {
        shoppingCartFragment.setCount(pieces);
    }
}
package com.example.duken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.duken.data.AddProfileData;
import com.example.duken.data.SharedData;
import com.example.duken.dialog.EditProfileDataDialog;
import com.example.duken.dialog.EnterCountDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, AddProductFragment.AddProductFragmentListener, EnterCountDialog.EnterCountDialogListener, ShoppingCartFragment.ShoppingCartFragmentListener, EditProfileDataDialog.EditProfileDataDialogListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment addProductFragment, activeFragment;
    private SearchFragment searchFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private MyProfileFragment myProfileFragment;
    private FragmentManager fragmentManager;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("users").child(SharedData.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProfileData addProfileData = snapshot.getValue(AddProfileData.class);
                SharedData.setStateId(addProfileData.getmStateID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    @Override
    public void completeEdition(String name, String surname) {
        myProfileFragment.completeEdition(name, surname);
    }
}
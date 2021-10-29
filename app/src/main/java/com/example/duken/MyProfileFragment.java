package com.example.duken;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duken.data.AddProfileData;
import com.example.duken.data.SharedData;
import com.example.duken.dialog.EditProfileDataDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileFragment extends Fragment implements EditProfileDataDialog.EditProfileDataDialogListener {

    private TextView nameTextView, surnameTextView, phoneNumberTextView;
    private ImageButton logOutImageButton, editProfileImageButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFirebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        surnameTextView = view.findViewById(R.id.surnameTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);

        logOutImageButton = view.findViewById(R.id.logoutImageButton);
        editProfileImageButton = view.findViewById(R.id.editImageButton);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        loadUserData();

        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    Toast.makeText(view.getContext(), "You have successfully signed out", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        };

        editProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        logOutImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
            }
        });

        return view;
    }

    private void openDialog() {
        EditProfileDataDialog editProfileDataDialog = new EditProfileDataDialog();
        editProfileDataDialog.show(getChildFragmentManager(), "Change profile data");
    }

    private void loadUserData() {
        phoneNumberTextView.setText(SharedData.getPhoneNumber());

        mDatabaseReference.child("users").child(SharedData.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProfileData addProfileData =snapshot.getValue(AddProfileData.class);
                nameTextView.setText(addProfileData.getmName());
                surnameTextView.setText(addProfileData.getmSurname());
                SharedData.setName(addProfileData.getmName());
                SharedData.setSurname(addProfileData.getmSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void completeEdition(String name, String surname) {
        nameTextView.setText(name);
        surnameTextView.setText(surname);
    }
}
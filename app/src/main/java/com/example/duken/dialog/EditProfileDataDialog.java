package com.example.duken.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.duken.R;
import com.example.duken.data.AddProfileData;
import com.example.duken.data.SharedData;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileDataDialog extends AppCompatDialogFragment {

    private TextInputLayout nameTextInputLayout, surnameTextInputLayout;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditProfileDataDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.edit_profile_layout, null);

        builder.setView(view)
                .setTitle("Edit profile data")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String name = nameTextInputLayout.getEditText().getText().toString();
                        String surname = surnameTextInputLayout.getEditText().getText().toString();

                        AddProfileData addProfileData = new AddProfileData(name, surname, SharedData.getStateId());

                        mDatabaseReference.child(SharedData.getPhoneNumber()).setValue(addProfileData);

                        Toast.makeText(getActivity(), "Data have been saved successfully!", Toast.LENGTH_SHORT).show();

                        listener.completeEdition(name, surname);
                    }
                });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users");

        nameTextInputLayout = view.findViewById(R.id.nameTextInputLayout);
        surnameTextInputLayout = view.findViewById(R.id.surnameTextInputLayout);

        nameTextInputLayout.getEditText().setText(SharedData.getName());
        surnameTextInputLayout.getEditText().setText(SharedData.getSurname());

        return builder.create();
    }

    public interface EditProfileDataDialogListener{
        void completeEdition(String name, String surname);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EditProfileDataDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement interface first");
        }
    }
}

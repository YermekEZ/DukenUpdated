package com.example.duken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.duken.data.AddProfileData;
import com.example.duken.data.SharedData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserInformationActivity extends AppCompatActivity {

    Button continueButton;
    TextInputLayout mName, mSurname, mStateId;
    TextInputEditText name, surname, stateId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_information);

        phoneNumber = SharedData.getPhoneNumber();

        continueButton = findViewById(R.id.buttonContinue);
        mName = findViewById(R.id.name);
        mSurname = findViewById(R.id.surname);
        mStateId = findViewById(R.id.stateId);
        name = findViewById(R.id.nameEditText);
        surname = findViewById(R.id.surnameEditText);
        stateId = findViewById(R.id.stateIdEditText);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users");
        continueButton.setEnabled(false);

        name.addTextChangedListener(profileData);
        surname.addTextChangedListener(profileData);
        stateId.addTextChangedListener(profileData);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getEditText().getText().toString();
                String surname = mSurname.getEditText().getText().toString();
                String stateID = mStateId.getEditText().getText().toString();

                AddProfileData addDataOfUser = new AddProfileData(name, surname, stateID);

                mDatabaseReference.child(phoneNumber).setValue(addDataOfUser);

                Intent intent = new Intent(AddUserInformationActivity.this, MainActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });
    }

    private TextWatcher profileData = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nameInput = name.getText().toString().trim();
            String surnameInput = surname.getText().toString().trim();
            String stateIDInput = stateId.getText().toString().trim();

            if(!nameInput.isEmpty() && !surnameInput.isEmpty() && !stateIDInput.isEmpty()) {

                continueButton.setBackgroundResource(R.color.activeButton);
                continueButton.setTextColor(Color.parseColor("#FFFFFF"));
                continueButton.setEnabled(true);

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
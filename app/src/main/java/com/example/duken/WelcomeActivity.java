package com.example.duken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.duken.data.SharedData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    EditText phoneNumberEditText;
    TextView continueTextView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        continueTextView = findViewById(R.id.continueButton);

        phoneNumberEditText.setText("+7");
        phoneNumberEditText.setSelection(2);
        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNumber = phoneNumberEditText.getText().toString().trim();
                SharedData.setPhoneNumber(phoneNumber);
                mDatabaseReference = mFirebaseDatabase.getReference("users/" + phoneNumber);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            SharedData.setRegistered(true);
                        } else {
                            SharedData.setRegistered(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(WelcomeActivity.this, CodeVerificationActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                Log.v("Check", phoneNumber);
            }
        });
    }
}
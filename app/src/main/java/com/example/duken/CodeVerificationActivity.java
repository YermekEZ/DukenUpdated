package com.example.duken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duken.data.SharedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CodeVerificationActivity extends AppCompatActivity {

    TextView infoTextView, continueButton, resendCodeTextView;
    EditText enterCodeEditText;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mAuth = FirebaseAuth.getInstance();

        infoTextView = findViewById(R.id.infoTextView);
        continueButton = findViewById(R.id.continueButton);
        resendCodeTextView = findViewById(R.id.resendCodeTextView);
        enterCodeEditText = findViewById(R.id.enterCodeEditText);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        infoTextView.append(" " + phoneNumber);

        sendVerificationCode(phoneNumber);
        resendCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Check", "Success");
                resendVerificationCode(phoneNumber, mResendToken);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = enterCodeEditText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    enterCodeEditText.setError("Invalid code");
                    enterCodeEditText.requestFocus();
                    return;
                }

                verifyVerificationCode(code);
            }
        });
    }

    private void startTimer() {
        new CountDownTimer(60000, 1000){

            @Override
            public void onTick(long l) {
                resendCodeTextView.setText("You can resend code in " + l / 1000 + " seconds");
                resendCodeTextView.setClickable(false);
            }

            @Override
            public void onFinish() {
                resendCodeTextView.setText(R.string.resendCode);
                resendCodeTextView.setTextColor(Color.parseColor("#356CFA"));
                resendCodeTextView.setClickable(true);
            }
        }.start();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationID = s;

            mResendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String smsCode = phoneAuthCredential.getSmsCode();
            if(smsCode != null){
                verifyVerificationCode(smsCode);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(CodeVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        startTimer();
    }

    private void verifyVerificationCode(String codeByUser) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, codeByUser);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(CodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent;
                            if(SharedData.isRegistered() == true){
                                intent = new Intent(CodeVerificationActivity.this, MainActivity.class);
                            } else {
                                intent = new Intent(CodeVerificationActivity.this, AddUserInformationActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message
                            Toast.makeText(CodeVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        startTimer();
    }
}
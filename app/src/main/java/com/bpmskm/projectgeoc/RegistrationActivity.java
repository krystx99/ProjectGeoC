package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etRegEmail, etRegPassword, etRegConfirmPassword;
    private Button btnRegister, btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(view -> {
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPassword = etRegConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, R.string.registration_missingDetails, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegistrationActivity.this, R.string.registration_passwordsNotMatch, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(RegistrationActivity.this, R.string.registration_passwordNotValid, Toast.LENGTH_SHORT).show();
                return;
            }

            String[] emailParts = email.split("@", 2);
            String username = emailParts[0];

            AuthenticationManager.registerUser( email, password, username, new AuthenticationManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(RegistrationActivity.this, R.string.registration_success, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finishAffinity();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.registration_failed) + " " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });

        });

        btnGoToLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&~`\\\\=_^{}:£.;'\\+,\\[|><\\-\\\"])[A-Za-z\\d@$!%*#?&~`\\\\=_^{}:£.;'\\+,\\[|><\\-\\\"]{8,}$";
        return password.matches(passwordRegex);
    }
}

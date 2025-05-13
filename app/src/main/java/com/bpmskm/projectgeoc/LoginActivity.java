package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (AuthenticationManager.isLoggedIn(this)) {
            if (currentUser != null) {
                AuthenticationManager.fetchUserData(this);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            else {
                AuthenticationManager.setLoggedInFlag(this, false);
            }
        }

        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.login_missingDetails, Toast.LENGTH_SHORT).show();
                return;
            }

            AuthenticationManager.loginUser(LoginActivity.this, email, password, new AuthenticationManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed) + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        btnGoToRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });

        tvForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
        });
    }
}

package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText etResetEmail;
    private Button btnResetPassword, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        etResetEmail = findViewById(R.id.etResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etResetEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(PasswordResetActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }
                AuthenticationManager.resetPassword(email, new AuthenticationManager.ResetCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PasswordResetActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(PasswordResetActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

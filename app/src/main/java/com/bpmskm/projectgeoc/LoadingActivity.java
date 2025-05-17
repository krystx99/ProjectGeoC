package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        AuthenticationManager.fetchCurrentUserData(this, new AuthenticationManager.UserDataFetchCallback() {
            @Override
            public void onSuccess() {
                AuthenticationManager.fetchTopTenUsers(LoadingActivity.this, new AuthenticationManager.TopTenUsersCallback() {
                    @Override
                    public void onSuccess(List<User> topUsers) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(LoadingActivity.this, getString(R.string.login_failed) + ": " + errorMessage, Toast.LENGTH_LONG).show();
                        AuthenticationManager.signOut(LoadingActivity.this);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(LoadingActivity.this, getString(R.string.login_failed) + ": " + errorMessage, Toast.LENGTH_LONG).show();
                AuthenticationManager.signOut(LoadingActivity.this);
            }
        });
    }
}
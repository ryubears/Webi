package com.yehyunryu.android.webi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Copyright 2018 Yehyun Ryu, Nikhil Shahi, Issac Gullickson, Joshua Palm

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private LoginButton mLoginButton;
    private ProgressBar mLoadingButton;

    private CallbackManager mCallbackManager;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (LoginButton) findViewById(R.id.login_facebook);
        mLoadingButton = (ProgressBar) findViewById(R.id.login_progress_bar);

        mCallbackManager = CallbackManager.Factory.create();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoginButton.setReadPermissions("public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                mLoadingButton.setVisibility(View.GONE);
                mLoginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(LOG_TAG, "Facebook Login: " + error.toString());
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                mLoadingButton.setVisibility(View.GONE);
                mLoginButton.setEnabled(true);
            }
        });
    }

    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mLoginButton.setEnabled(false);
        mLoadingButton.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Firebase Auth: signInWithCredential successful");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            launchMainActivity();
                        } else {
                            Log.e(LOG_TAG, "Firebase Auth: signInWithCredential failure: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                        mLoadingButton.setVisibility(View.GONE);
                        mLoginButton.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mFirebaseAuth.getCurrentUser() != null) launchMainActivity();
    }
}

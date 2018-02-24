package com.yehyunryu.android.webi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

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

/*
 App Icon(Wave): Made by Freepik(http://www.freepik.com) from Flaticon(www.flaticon.com). This icon is licensed by Creative Commons BY 3.0(http://creativecommons.org/licenses/by/3.0).
 User Icon: Made by Smashicons(https://www.flaticon.com/authors/smashicons) from Flaticon(www.flaticon.com). This icon is licensed by Creative Commons BY 3.0(http://creativecommons.org/licenses/by/3.0).
 Chat Icon: Made by Google(https://material.io/icons/#ic_chat).
 Send Icon: Made by Google(https://material.io/icons/#ic_send).
 Report Icon: Made by Google(https://material.io/icons/#ic_report).
*/
public class MainActivity extends AppCompatActivity {
    public static final String SAVED_URL_KEY = "saved_url_key";

    private DrawerLayout mDrawerLayout;
    private ImageButton mProfileButton;
    private EditText mUrlEditText;
    private ImageButton mChatButton;
    private WebView mWebView;
    private ImageButton mReportButton;
    private RecyclerView mChatRecyclerView;
    private EditText mChatEditText;
    private ImageButton mSendButton;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;
    private AccessToken mAccessToken;
    private Profile mProfile;
    private ActionBarDrawerToggle mDrawerToggle;
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;
    private LoginManager mLoginManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUrlReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mCurrentUserReference;
    private String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_main);

        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mProfileButton = (ImageButton) findViewById(R.id.main_profile_button);
        mUrlEditText = (EditText) findViewById(R.id.main_url_edittext);
        mChatButton = (ImageButton) findViewById(R.id.main_chat_button);
        mWebView = (WebView) findViewById(R.id.main_webview);
        mReportButton = (ImageButton) findViewById(R.id.main_chat_report_button);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.main_chat_recyclerview);
        mChatEditText = (EditText) findViewById(R.id.main_chat_edittext);
        mSendButton = (ImageButton) findViewById(R.id.main_send_button);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mPreferencesEditor = mSharedPreferences.edit();

        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null) {
            new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    mProfile = currentProfile;
                    if(mProfile != null) {
                        displayProfilePicture(mProfile);
                    } else {
                        mProfileButton.setImageResource(R.drawable.user_black);
                    }
                }
            };
            mProfile = Profile.getCurrentProfile();
            if(mProfile != null) {
                displayProfilePicture(mProfile);
            } else {
                Profile.fetchProfileForCurrentAccessToken();
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.chat_drawer_open, R.string.chat_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //moves focus from edittext to webview
        mWebView.requestFocus();

        //set progressbar
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
                if(progress == 100) {
                    setProgressBarIndeterminateVisibility(false);
                    setProgressBarIndeterminate(false);
                }
            }
        });

        //handle web intents using the webview
        mWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(false);

        //load webview
        String url = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getString(SAVED_URL_KEY, "https://google.com/");
        mWebView.loadUrl(url);

        mChatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
         mChatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatLayoutManager.setStackFromEnd(true);
        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.setLayoutManager(mChatLayoutManager);

        mLoginManager = LoginManager.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mCurrentUserReference = mFirebaseDatabase.getReference().child("users").child(mAccessToken.getUserId());
        mCurrentUserReference.child("name").setValue(mProfile.getName());
        mCurrentUserReference.child("profileUrl").setValue(mProfile.getProfilePictureUri(200,200).toString());

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileDialog dialog = new ProfileDialog();
                dialog.showDialog(MainActivity.this, mProfile, mLoginManager, mFirebaseAuth, mPreferencesEditor);
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mChatEditText.getText().toString();
                if(!text.isEmpty()) {
                    long currentTime = System.currentTimeMillis();
                    String userId = mAccessToken.getUserId();
                    String name = mProfile.getName();
                    String profileUrl = mProfile.getProfilePictureUri(200,200).toString();
                    ChatMessage chatMessage = new ChatMessage(currentTime, userId, name, text, profileUrl);
                    mCurrentUrlReference.push().setValue(chatMessage);
                    mCurrentUserReference.child("history").child(mCurrentUrl).push().setValue(new ChatMessage(currentTime, text));
                }
                mChatEditText.setText("");
                mChatEditText.clearFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mChatEditText.getWindowToken(), 0);
                mChatLayoutManager.scrollToPosition(0);
                mChatRecyclerView.requestFocus();
            }
        });

        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        //load webview with input
        mUrlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_GO) {
                    //determine if input is a search or a valid url
                    String urlInput = mUrlEditText.getText().toString();
                    String[] splitInput = urlInput.split("\\.");
                    if(splitInput.length < 2) {
                        mWebView.loadUrl("https://www.google.com/search?q=" + urlInput);
                        mPreferencesEditor.putString(SAVED_URL_KEY, "https://www.google.com/search?q=" + urlInput).apply();
                    } else {
                        int last = splitInput.length - 1;
                        if(splitInput[last].equals("com") || splitInput[last].equals("net") || splitInput[last].equals("gov") || splitInput[last].equals("edu")
                                || splitInput[last].equals("org") || splitInput[last].equals("tv") || splitInput[last].equals("io")) {
                            if(URLUtil.isHttpsUrl(urlInput) || URLUtil.isHttpUrl(urlInput)) {
                                mWebView.loadUrl(urlInput);
                                mPreferencesEditor.putString(SAVED_URL_KEY, urlInput).apply();
                            } else {
                                mWebView.loadUrl("http://" + urlInput);
                                mPreferencesEditor.putString(SAVED_URL_KEY, "https://" + urlInput).apply();
                            }
                        } else {
                            mWebView.loadUrl("https://www.google.com/search?q=" + urlInput);
                            mPreferencesEditor.putString(SAVED_URL_KEY, "https://www.google.com/search?q=" + urlInput).apply();
                        }
                    }
                }
                //hides keyboard after user searches
                mUrlEditText.clearFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
                mWebView.requestFocus();
                return false;
            }
        });


    }

    private void displayProfilePicture(Profile profile) {
        Uri profileUri = profile.getProfilePictureUri(80,80);
        Transformation profilePicTransformation = new RoundedTransformationBuilder()
                .cornerRadius(40)
                .oval(false)
                .build();

        Picasso.with(this)
                .load(profileUri)
                .transform(profilePicTransformation)
                .into(mProfileButton);
    }

    private void attachDatabase() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                mChatAdapter.add(chatMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mCurrentUrl = mWebView.getUrl();
        if(mCurrentUrl.charAt(mCurrentUrl.length() - 1) == '/') mCurrentUrl = mCurrentUrl.substring(0, mCurrentUrl.length() - 1);
        mCurrentUrl = mCurrentUrl.replace("https://", "");
        mCurrentUrl = mCurrentUrl.replace("http://", "");
        mCurrentUrl = mCurrentUrl.replace(".", "_");
        mCurrentUrl = mCurrentUrl.replace("/", "`");
        mCurrentUrl = mCurrentUrl.replace("#", "*");
        mCurrentUrlReference = mFirebaseDatabase.getReference().child("urls").child(mCurrentUrl);
        mCurrentUrlReference.addChildEventListener(mChildEventListener);
    }

    private void detachDatabase() {
        mChatAdapter.clear();
        if(mCurrentUrlReference != null) {
            if(mChildEventListener != null) mCurrentUrlReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return to previous webpage if available
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
    }

    @Override
    protected void onResume() {
        if(mChildEventListener == null) attachDatabase();
        super.onResume();
    }

    @Override
    protected void onPause() {
        detachDatabase();
        super.onPause();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //update url edittext
            mUrlEditText.setText(url);
            detachDatabase();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mPreferencesEditor.putString(SAVED_URL_KEY, view.getUrl()).apply();
            attachDatabase();
            super.onPageFinished(view, url);
        }
    }
}

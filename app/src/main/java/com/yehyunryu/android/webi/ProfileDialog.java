package com.yehyunryu.android.webi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Yehyun Ryu on 1/28/2018.
 */

public class ProfileDialog {
    private ImageView mProfilePicture;
    private TextView mProfileName;
    private TextView mLogOutTextView;
    private TextView mCloseTextView;

    public void showDialog(final Activity activity, Profile profile, final LoginManager loginManager, final FirebaseAuth firebaseAuth, final SharedPreferences.Editor sharedPreferencesEditor) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.profile_dialog);

        mProfilePicture = (ImageView) dialog.findViewById(R.id.profile_picture);
        mProfileName = (TextView) dialog.findViewById(R.id.profile_name);
        mLogOutTextView = (TextView) dialog.findViewById(R.id.profile_logout);
        mCloseTextView = (TextView) dialog.findViewById(R.id.profile_close);

        mProfileName.setText(profile.getName());

        Uri profileUrl = profile.getProfilePictureUri(400,400);
        if(profileUrl == null) {
            mProfilePicture.setImageResource(R.drawable.user_black);
        } else {
            displayProfilePicture(dialog.getContext(), profileUrl);
        }

        mLogOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activity.finish();
                sharedPreferencesEditor.putString(MainActivity.SAVED_URL_KEY, "https://google.com/").apply();
                firebaseAuth.signOut();
                loginManager.logOut();
            }
        });

        mCloseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void displayProfilePicture(Context context, Uri profileUrl) {
        Transformation profilePicTransformation = new RoundedTransformationBuilder()
                .cornerRadius(250)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(profileUrl)
                .transform(profilePicTransformation)
                .into(mProfilePicture);
    }
}


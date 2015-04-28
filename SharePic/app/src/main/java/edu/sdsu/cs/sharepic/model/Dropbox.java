package edu.sdsu.cs.sharepic.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import edu.sdsu.cs.sharepic.Constants;
import edu.sdsu.cs.sharepic.Utils;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
public class Dropbox extends Account {
    public static final String TAG = "Dropbox";
    private static final String APP_KEY = "l00snssp2vqeghc";
    private static final String APP_SECRET = "b1wyb12i1pbk2cl";
    private static final String OAUTH2 = "oauth2:";
    private static Dropbox mInstance;
    private Context mContext;


    private DropboxAPI<AndroidAuthSession> mDBApi;

    private Dropbox(Context context) {
        mContext = context;
        init();
    }

    public static synchronized Dropbox getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Dropbox(context);
        }

        return mInstance;
    }

    private void init() {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;
        String savedAccessToken = "";
        if (mContext != null) {
            savedAccessToken = Utils.getFromSharedPreferences(mContext, Constants.KEY_DROPBOX_ACCESS_SECRET_NAME);
        } else {
            Log.e(TAG, "Error in login : Context is null");
        }

        if (!savedAccessToken.isEmpty()) {
            AccessTokenPair accessToken = new AccessTokenPair(OAUTH2,savedAccessToken);
            session = new AndroidAuthSession(appKeys, accessToken);
        } else {
            session = new AndroidAuthSession(appKeys);
        }

        mDBApi = new DropboxAPI<>(session);
    }

    @Override
    public void login() {
        if (mContext != null) {
            mDBApi.getSession().startOAuth2Authentication(mContext);
        } else {
            Log.e(TAG, "Error in login : Context is null");
        }
    }

    public void finishLogin() {
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                if (mContext != null) {
                    Utils.storeInSharedPreferences(mContext, Constants.KEY_DROPBOX_ACCESS_SECRET_NAME, accessToken);
                } else {
                    Log.e(TAG, "Error in login : Context is null");
                }
                Log.i(TAG, "Logged In");
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    public void logout() {
        mDBApi.getSession().unlink();
        if (mContext != null) {
            Utils.storeInSharedPreferences(mContext, Constants.KEY_DROPBOX_ACCESS_KEY_NAME, "");
        } else {
            Log.i(TAG, "Cannot clear contents in preferences : Context is null");
        }
    }

    @Override
    public boolean isLoggedIn() {
        return mDBApi.getSession().isLinked();
    }
}

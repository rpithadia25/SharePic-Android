package edu.sdsu.cs.sharepic.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
            session = new AndroidAuthSession(appKeys, savedAccessToken);
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

        if (!isLoggedIn() && mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                mDBApi.getSession().getAccessTokenPair();
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

    public void upload(Bitmap imageBitmap) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            final File file = new File(directory, "abc.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close();
            Thread upload = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fIn = new FileInputStream(file);
                        mDBApi.putFile("/abc.jpg", fIn, file.length(), null, null);
                        Log.i(TAG, "Uploaded");
                    } catch (DropboxException | FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

            upload.start();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());

        }
    }
}
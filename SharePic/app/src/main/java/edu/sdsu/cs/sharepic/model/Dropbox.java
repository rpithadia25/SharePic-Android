package edu.sdsu.cs.sharepic.model;

import android.app.Activity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.Utils;
import edu.sdsu.cs.sharepic.classes.Constants;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
class Dropbox extends Account {
    public static final String TAG = "Dropbox";
    private static final String APP_KEY = "l00snssp2vqeghc";
    private static final String APP_SECRET = "b1wyb12i1pbk2cl";
    private static Dropbox mInstance;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    private Dropbox() {
        init();
    }

    public static synchronized Dropbox getInstance(Activity activity) {
        mActivity = activity;
        if (mInstance == null) {
            mInstance = new Dropbox();
        }
        return mInstance;
    }

    @Override
    public String toString() {
        return Constants.DROPBOX;
    }

    private void init() {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;
        String savedAccessToken = "";
        if (mActivity != null) {
            savedAccessToken = Utils.getFromSharedPreferences(mActivity, Constants.KEY_DROPBOX_ACCESS_SECRET_NAME);
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
    public void login(LoginListener loginListener) {
        if (mActivity != null) {
            mDBApi.getSession().startOAuth2Authentication(mActivity);
        } else {
            Log.e(TAG, "Error in login : Context is null");
        }
    }

    @Override
    public void finishLogin() {
        if (!isLoggedIn() && mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                mDBApi.getSession().getAccessTokenPair();
                if (mActivity != null) {
                    Utils.storeInSharedPreferences(mActivity, Constants.KEY_DROPBOX_ACCESS_SECRET_NAME, accessToken);
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
        if (mActivity != null) {
            Utils.storeInSharedPreferences(mActivity, Constants.KEY_DROPBOX_ACCESS_KEY_NAME, "");
        } else {
            Log.i(TAG, "Cannot clear contents in preferences : Context is null");
        }
    }

    @Override
    public boolean isLoggedIn() {
        return mDBApi.getSession().isLinked();
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_dropbox;
    }

    @Override
    public void upload(ArrayList<Bitmap> bitmap) {
        final int numberOfImages = bitmap.size();
        ContextWrapper cw = new ContextWrapper(mActivity);
        File directory = cw.getDir(Constants.IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        try {
            for (int i = 0; i < numberOfImages; i++) {
                String date = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT).format(new Date());
                final int currentImageNumber = i;
                final String fileName = date + Constants._IMAGE + currentImageNumber + Constants.JPEG_EXTENSION;
                final File file = new File(directory, fileName);
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.get(i).compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESSION_QUALITY, fOut);
                fOut.close();
                Thread upload = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileInputStream fIn = new FileInputStream(file);
                            mDBApi.putFile(fileName, fIn, file.length(), null, null);

                            if ((currentImageNumber + 1) == numberOfImages) {
                                // Show Completion Toast
                            }
                            Log.i(TAG, "Uploaded : " + fileName);
                        } catch (DropboxException | FileNotFoundException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
                upload.start();
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
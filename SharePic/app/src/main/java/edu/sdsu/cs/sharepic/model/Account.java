package edu.sdsu.cs.sharepic.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
public abstract class Account {

    protected static Activity mActivity;
    protected boolean mFromSettings = false;

    public abstract void login(LoginListener loginListener);
    public abstract void logout();
    public abstract boolean isLoggedIn();
    public abstract void upload(ArrayList<Bitmap> bitmap);
    public abstract String toString();
    public abstract int getImageResource();
    public void finishLogin(int requestCode, int resultCode) { }
    public void finishLogin() { }
    public void setFromSettings(boolean fromSettings) {
        mFromSettings = fromSettings;
    }


    // WARNING: Never change the order of accounts in accounts array
    public static Account[] supportedAccounts(Activity activity) {
         return (new Account[]{Dropbox.getInstance(activity), GoogleDrive.getInstance(activity)});
    }
}

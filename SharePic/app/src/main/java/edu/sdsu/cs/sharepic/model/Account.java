package edu.sdsu.cs.sharepic.model;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
public abstract class Account {

    private static Account[] accounts = null;
    public abstract void login();
    public abstract void logout();
    public abstract boolean isLoggedIn();
    public abstract void upload(Bitmap[] bitmap);

    public static Account[] supportedAccounts(Context context) {
        if (accounts == null) {
            accounts = new Account[]{Dropbox.getInstance(context)};
        }
        return accounts;
    }

}

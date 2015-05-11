package edu.sdsu.cs.sharepic.model;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
public abstract class Account {

    private static Account[] accounts = null;
    public abstract void login();
    public abstract void logout();
    public abstract boolean isLoggedIn();

    // WARNING: Never change the order of accounts in accounts array
    public static Account[] supportedAccounts(Context context) {
        if (accounts == null) {
            accounts = new Account[]{FlickrAccount.getInstance(context), Dropbox.getInstance(context)};
        }

        return accounts;
    }

    public HashMap toHashmap() {
        HashMap map = new HashMap();
        map.put("accountName", this.toString());
        return map;
    }

    public abstract int getImageResource();
}

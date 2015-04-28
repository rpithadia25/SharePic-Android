package edu.sdsu.cs.sharepic.model;

import android.app.Activity;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */
public abstract class Account {

    public abstract void login();
    public abstract void logout();
    public abstract boolean isLoggedIn();

}

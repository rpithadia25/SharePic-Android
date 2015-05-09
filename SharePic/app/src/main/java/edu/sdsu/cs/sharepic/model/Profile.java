package edu.sdsu.cs.sharepic.model;

import java.util.ArrayList;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */

public class Profile {
    private String profileName;
    private ArrayList<AbstractAccount> accounts;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public ArrayList<AbstractAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<AbstractAccount> accounts) {
        this.accounts = accounts;
    }
}

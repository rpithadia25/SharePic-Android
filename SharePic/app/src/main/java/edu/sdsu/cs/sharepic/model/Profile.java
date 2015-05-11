package edu.sdsu.cs.sharepic.model;

import java.util.ArrayList;

/**
 * Created by Rakshit Pithadia on 4/15/15.
 * Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
 */

public class Profile {
    private String profileName;

    private ArrayList<Integer> accounts; // holds positions of Accounts in current Profile from supportedAccounts array

    public Profile () {
        accounts = new ArrayList<>();
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public ArrayList<Integer> getAccountsPositions() {
        return accounts;
    }

    public void addAccountPosition(int index) {
        accounts.add(index);
    }
}

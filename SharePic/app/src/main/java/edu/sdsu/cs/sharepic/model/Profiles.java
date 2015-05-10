package edu.sdsu.cs.sharepic.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Horsie on 5/9/15.
 */
public class Profiles {
    public static final String TAG = "Profiles";
    private ArrayList<Profile> profiles;
    private static Profiles mInstance = null;

    private Profiles() {
       profiles = new ArrayList<>();
    }

    public static synchronized Profiles getInstance() {
        if (mInstance == null) {
            mInstance = new Profiles();
        }

        return mInstance;
    }

    public Iterator<Profile> iterator() {
        return profiles.iterator();
    }

    public void add(Profile profile) {
        profiles.add(profile);
    }

    public Profile getProfile(int index) {
        Iterator <Profile> iterator = iterator();
        while (iterator.hasNext()) {
            Profile profile = iterator.next();
            if (index == 0) {
                return profile;
            }
            Log.i(TAG, String.valueOf(index));
            index--;
        }

        return null;
    }
}
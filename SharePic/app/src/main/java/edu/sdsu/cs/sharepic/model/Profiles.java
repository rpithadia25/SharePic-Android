package edu.sdsu.cs.sharepic.model;

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

        if (index <= profiles.size()) {
            return profiles.get(index);
        }
        return null;
    }

}

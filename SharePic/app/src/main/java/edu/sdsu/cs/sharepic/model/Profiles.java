package edu.sdsu.cs.sharepic.model;

import android.content.Context;

/**
 * Created by Rakshit Pithadia on 5/9/15.
 */
public class Profiles {

    private static Profile[] profiles = null;

    public static Profile[] supportedAccounts(Context context) {
        if (profiles == null) {
            //profiles =
            //TODO: fetch from shared preferences
        }

        return profiles;
    }

}

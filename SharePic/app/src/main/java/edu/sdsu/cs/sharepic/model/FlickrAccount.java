package edu.sdsu.cs.sharepic.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import edu.sdsu.cs.sharepic.Constants;
import edu.sdsu.cs.sharepic.Utils;

/**
* Created by Rakshit Pithadia on 4/15/15.
* Copyright (c) 2015 Harsh Shah, Rakshit Pithadia. All rights reserved.
*/

//TODO: Think of what happens when app gets unauthorized from web
public class FlickrAccount extends Account {

    public static final String TAG = "Flickr";
    private static FlickrAccount mInstance;
    private Flickr flickr = null;

    private static final String API_KEY = "500422a15f9a413791a73c123d219b2a";
    private static final String API_SECRET_KEY = "4144818e19bd4ce4";

    public static final Uri OAUTH_CALLBACK_URI = Uri.parse(Constants.FLICKR_CALLBACK_SCHEME + "://oauth");
    private Activity mContext;

    private FlickrAccount(Activity context) {
        mContext = context;
    }

    public static FlickrAccount getInstance(Activity callingActivity) {
        if (mInstance == null) {
            mInstance = new FlickrAccount(callingActivity);
        }

        return mInstance;
    }

    public Flickr getFlickr() {
        if (flickr == null) {
            flickr = new Flickr(API_KEY, API_SECRET_KEY);
        }

        return flickr;
    }

    private void init() {

    }

    @Override
    public void login() {
        FlickrOAuthTask oAuthTask = new FlickrOAuthTask();
        oAuthTask.execute();
    }

    @Override
    public void logout() {
        if (mContext != null) {
            Utils.storeInSharedPreferences(mContext, Constants.KEY_FLICKR_OAUTH_TOKEN, "");
            Utils.storeInSharedPreferences(mContext, Constants.KEY_FLICKR_SECRET_TOKEN, "");
        } else {
            Log.i(TAG, "Cannot clear contents in preferences : Context is null");
        }
    }

    @Override
    public boolean isLoggedIn() {
        OAuth oAuth = getOAuthToken();
        return (oAuth != null);
    }

    public void finishLogin(Intent intent) {
        String scheme = intent.getScheme();
        OAuth savedToken = getOAuthToken();
        if (Constants.FLICKR_CALLBACK_SCHEME.equals(scheme) && savedToken == null) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            String[] data = query.split("&");
            if (data.length == 2) {
                String oAuthToken = data[0].substring(data[0].indexOf(Constants.EQUALS) + 1);
                String oAuthVerifier = data[1].substring(data[1].indexOf(Constants.EQUALS) + 1);
                OAuth oAuth = getOAuthToken();
                if (oAuth != null && oAuth.getToken() != null && oAuth.getToken().getOauthTokenSecret() != null) {
                    String oAuthTokenSecret = oAuth.getToken().getOauthTokenSecret();
                    if (mInstance != null) {
                        Flickr flickr = mInstance.getFlickr();
                        getAccessToken(flickr.getOAuthInterface(), oAuthToken, oAuthTokenSecret, oAuthVerifier);
                    }
                }
            }
        }
    }

    private void getAccessToken(final OAuthInterface oAuthApi, final String oAuthToken, final String oAuthTokenSecret, final String oAuthVerifier) {
        Thread getOAuthTokenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OAuth oAuth = oAuthApi.getAccessToken(oAuthToken, oAuthTokenSecret, oAuthVerifier);
                    saveOAuthToken(oAuth.getToken().getOauthToken(), oAuth.getToken().getOauthTokenSecret());
                } catch (Exception e) {
                    Log.i(TAG, "Error : " + e.getMessage());
                }
            }
        });

        getOAuthTokenThread.start();
    }

    private OAuth getOAuthToken() {
        String oAuthTokenString = Utils.getFromSharedPreferences(mContext, Constants.KEY_FLICKR_OAUTH_TOKEN);
        String secretTokenString = Utils.getFromSharedPreferences(mContext, Constants.KEY_FLICKR_SECRET_TOKEN);
        if (oAuthTokenString.isEmpty() || secretTokenString.isEmpty()) {
            Log.i(TAG, "OAuth tokens empty");
            return null;
        }
        OAuth oAuth = new OAuth();
        OAuthToken oAuthToken = new OAuthToken();
        oAuth.setToken(oAuthToken);
        oAuthToken.setOauthToken(oAuthTokenString);
        oAuthToken.setOauthTokenSecret(secretTokenString);
        return oAuth;
    }

    private void saveOAuthToken(String oAuthToken, String secretToken) {
        Utils.storeInSharedPreferences(mContext, Constants.KEY_FLICKR_OAUTH_TOKEN, oAuthToken);
        Utils.storeInSharedPreferences(mContext, Constants.KEY_FLICKR_SECRET_TOKEN, secretToken);
    }


    private class FlickrOAuthTask extends AsyncTask<Void, Integer, String> {
        public static final String TAG = "FlickrOAuthTask";
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(mContext, "", "Generating the authorization request...");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    FlickrOAuthTask.this.cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Flickr flickr = mInstance.getFlickr();
                OAuthToken oauthToken = flickr.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
                saveOAuthToken(oauthToken.getOauthToken(), oauthToken.getOauthTokenSecret());
                URL oauthUrl = flickr.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, oauthToken);
                return oauthUrl.toString();
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String url) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (!url.isEmpty() && !url.startsWith("error")) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        }
    }
}

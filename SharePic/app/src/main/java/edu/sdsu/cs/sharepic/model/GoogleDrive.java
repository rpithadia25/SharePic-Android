package edu.sdsu.cs.sharepic.model;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.classes.Constants;

/**
 * Created by Horsie on 5/12/15.
 */
public class GoogleDrive extends Account implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = "GoogleDrive";

    public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LoginListener mLoginListener;

    private static GoogleDrive mInstance;

    private GoogleDrive() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
                .addApi(Drive.API)
                .addApi(Plus.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);
        mGoogleApiClient = builder.build();
        mGoogleApiClient.connect();
    }

    public static synchronized GoogleDrive getInstance(Activity activity) {
        mActivity = activity;
        if (mInstance == null) {
            mInstance = new GoogleDrive();
        }

        return mInstance;
    }

    @Override
    public void login(LoginListener loginListener) {
        mLoginListener = loginListener;
        mGoogleApiClient.connect();
    }

    @Override
    public void logout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Log.i(TAG, "Logged Out");
        }
    }

    @Override
    public void finishLogin(int requestCode, int resultCode){
        if (requestCode == GoogleDrive.RESOLVE_CONNECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean isLoggedIn() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void upload(ArrayList<Bitmap> bitmap) {

    }

    @Override
    public String toString() {
        return Constants.GOOGLE_DRIVE;
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_google_drive;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Failed");
        if (mFromSettings && !result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), mActivity, 0).show();
            return;
        }
        try {
            if (mFromSettings) {
                result.startResolutionForResult(mActivity, RESOLVE_CONNECTION_REQUEST_CODE);
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        if (mLoginListener != null) {
            mLoginListener.loggedIn(toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }
}

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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.classes.Constants;

/**
 * Created by Horsie on 5/12/15.
 */
public class GoogleDrive extends Account implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

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
    public void finishLogin(int requestCode, int resultCode) {
        if (requestCode == GoogleDrive.RESOLVE_CONNECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean isLoggedIn() {
        return mGoogleApiClient.isConnected();
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Error while trying to create the file");
                        return;
                    }
                    Log.i(TAG, "Created a file in App Folder: " + result.getDriveFile().getDriveId());
                }
            };

    @Override
    public void upload(final ArrayList<Bitmap> bitmap) {

        final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
                new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Error while trying to create new file contents");
                            return;
                        }

                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                String date = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT).format(new Date());
                                final String fileName = date + Constants._IMAGE + 0 + Constants.JPEG_EXTENSION;

                                // write content to DriveContents
                                OutputStream outputStream = driveContents.getOutputStream();
                                try {
                                    bitmap.get(0).compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESSION_QUALITY, outputStream);
                                    outputStream.close();
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(fileName)
                                        .setMimeType("image/jpeg")
                                        .setStarred(true).build();

                                // create a file on root folder
                                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                        .createFile(mGoogleApiClient, changeSet, driveContents)
                                        .setResultCallback(fileCallback);
                            }
                        }.start();
                    }
                };

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
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
        DriveId folderId = Drive.DriveApi.getAppFolder(mGoogleApiClient).getDriveId();
        DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, folderId);
        folder.getMetadata(mGoogleApiClient).setResultCallback(metadataRetrievedCallback);
        if (mLoginListener != null) {
            mLoginListener.loggedIn(toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }


    final private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new
            ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.v(TAG, "Problem while trying to fetch metadata.");
                        return;
                    }

                    Metadata metadata = result.getMetadata();
                    if(metadata.isTrashed()){
                        Log.v(TAG, "Folder is trashed");
                    }else{
                        Log.v(TAG, "Folder is not trashed");
                    }

                }
            };
}

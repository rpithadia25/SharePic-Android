package edu.sdsu.cs.sharepic.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.classes.Constants;
import edu.sdsu.cs.sharepic.model.Account;
import edu.sdsu.cs.sharepic.model.Dropbox;
import edu.sdsu.cs.sharepic.model.FlickrAccount;
import edu.sdsu.cs.sharepic.model.Profile;
import edu.sdsu.cs.sharepic.model.Profiles;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.utils.ImageInternalFetcher;

public class ProfileDetailActivity extends ActionBarActivity {

    private static int INTENT_REQUEST_GET_IMAGES = 111;
    private ViewGroup mSelectedImagesContainer;
    HashSet<Uri> mMedia = new HashSet<Uri>();
    LinearLayout accountsIconView;
    private Profile currentProfile;
    private Bitmap[] selectedImages = null;
    Dropbox dropboxInstance;
    FlickrAccount flickrInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        init();
        currentProfile = fetchCurrentProfile();
        addAccountIcons(accountsIconView);
        View imagesPreview = findViewById(R.id.images_preview);

        imagesPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });

        ImageView imageView = new ImageView(this);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(imageView)
                .setBackgroundDrawable(R.drawable.ic_upload)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Upload images here
            }
        });
    }

    private void init() {
        dropboxInstance = Dropbox.getInstance(getApplicationContext());
        flickrInstance = FlickrAccount.getInstance(getApplicationContext());
        selectedImages = new Bitmap[Constants.MAX_IMAGE_COUNT];
        accountsIconView = (LinearLayout) findViewById(R.id.accounts_logo_container);
        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_images_container);
    }

    private Profile fetchCurrentProfile() {
        Bundle data = getIntent().getExtras();
        int index = data.getInt(Constants.PROFILE_INDEX_KEY);
        return Profiles.getInstance().getProfile(index);
    }

    private void addAccountIcons(LinearLayout layout){

        ArrayList<Integer> profileAccounts = currentProfile.getAccountsPositions();
        Account[] accounts = Account.supportedAccounts(getApplicationContext());
        for (int i = 0; i < profileAccounts.size(); i++) {
            ImageView imageView = new ImageView(this);
            int imageResource = accounts[profileAccounts.get(i)].getImageResource();
            imageView.setImageResource(imageResource);
            layout.addView(imageView);
        }
    }

    private void getImages() {
        Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
        //Set Image Picker Limit
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, Constants.MAX_IMAGE_COUNT);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        mMedia.add(uri);
                    }
                    populateBitmaps(parcelableUris);
                    showMedia();
                }
            }
        }
    }

    private void populateBitmaps(Parcelable[] selection) {

        for(int i = 0; i < selection.length; i++) {
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(selection[i].toString());
            selectedImages[i] = yourSelectedImage;
        }
    }

    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        Iterator<Uri> iterator = mMedia.iterator();
        ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
        while(iterator.hasNext()) {
            Uri uri = iterator.next();

            if(mMedia.size() >= 1) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
            }

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.media_layout, null);

            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            if(!uri.toString().contains("content://")) {
                uri = Uri.fromFile(new File(uri.toString()));
            }

            imageFetcher.loadImage(uri, thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            // set the dimension to correctly
            // show the image thumbnail.
            int widthPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int heightPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixels, heightPixels));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds profileNames to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

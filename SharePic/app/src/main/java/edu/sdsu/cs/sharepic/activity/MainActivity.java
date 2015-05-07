package edu.sdsu.cs.sharepic.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sdsu.cs.sharepic.Constants;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.model.Dropbox;
import edu.sdsu.cs.sharepic.model.FlickrAccount;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private ListView listView;
    private Button button;
    private Dropbox dropbox;
    private FlickrAccount flickr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(Constants.MAIN_TITLE);

//        listView = (ListView) findViewById(R.id.listView);
//
//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X"};
//
//        final ArrayList<String> list = new ArrayList<>();
//        for (String value : values) {
//            list.add(value);
//        }
//
//        final StableArrayAdapter adapter = new StableArrayAdapter(this,
//                android.R.layout.simple_list_item_1, list);
//        listView.setAdapter(adapter);
//
        dropbox = Dropbox.getInstance(getApplicationContext());
        if (!dropbox.isLoggedIn()) {
            dropbox.login();
        }
//        flickr = FlickrAccount.getInstance(this);
//
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!flickr.isLoggedIn()) {
//                    flickr.login();
//                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123);
            }
        });

//        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
//        recList.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recList.setLayoutManager(llm);
//
//        ContactAdapter ca = new ContactAdapter(createList(30));
//        recList.setAdapter(ca);
    }

    private List<ContactInfo> createList(int size) {

        List<ContactInfo> result = new ArrayList<ContactInfo>();
        for (int i=1; i <= size; i++) {
            ContactInfo ci = new ContactInfo();
            ci.name = ContactInfo.NAME_PREFIX + i;
            ci.surname = ContactInfo.SURNAME_PREFIX + i;
            ci.email = ContactInfo.EMAIL_PREFIX + i + "@test.com";

            result.add(ci);

        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        try {
            Uri imageUri = data.getData();
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(selectedImage);

            dropbox.upload(selectedImage);

        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //this is very important, otherwise you would get a null Scheme in the onResume later on.
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dropbox != null) {
            dropbox.finishLogin();
        }

        if (flickr != null) {
            flickr.finishLogin(getIntent());
        }
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

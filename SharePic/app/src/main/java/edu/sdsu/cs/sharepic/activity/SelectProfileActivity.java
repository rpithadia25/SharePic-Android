package edu.sdsu.cs.sharepic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import java.util.ArrayList;
import java.util.Iterator;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.classes.Constants;
import edu.sdsu.cs.sharepic.model.Account;
import edu.sdsu.cs.sharepic.model.Profile;
import edu.sdsu.cs.sharepic.model.Profiles;

public class SelectProfileActivity extends ActionBarActivity {

    ListView profileListView;
    ArrayList<String> profileNames = new ArrayList<>();
    ArrayAdapter<String> profileNamesAdapter;
    private static final int INTENT_REQUEST_CODE = 1;
    Account[] supportedAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
        setTitle(Constants.MAIN_TITLE);

        supportedAccounts = Account.supportedAccounts(getApplicationContext());

        profileListView = (ListView) findViewById(R.id.listView);
        profileNames = new ArrayList<>();
        profileNamesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, profileNames);
        profileListView.setAdapter(profileNamesAdapter);
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context mainActivity = getApplicationContext();
                Intent profileDetailActivityIntent = new Intent(mainActivity, ProfileDetailActivity.class);
                profileDetailActivityIntent.putExtra(Constants.PROFILE_INDEX_KEY, position);
                startActivity(profileDetailActivityIntent);
            }
        });

        ImageView floatingButtonImageView = new ImageView(this);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(floatingButtonImageView)
                .setBackgroundDrawable(R.drawable.ic_plus)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mainActivity = getApplicationContext();
                Intent createProfileActivityIntent = new Intent(mainActivity, CreateProfileActivity.class);
                startActivityForResult(createProfileActivityIntent, INTENT_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                profileNames.clear();
                Iterator<Profile> iterator = Profiles.getInstance().iterator();
                while (iterator.hasNext()) {
                    Profile profile = iterator.next();
                    profileNames.add(profile.getProfileName());
                }
                profileNamesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds profileNames to the action bar if it is present.
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
            Context mainActivity = getApplicationContext();
            Intent settingsActivityIntent = new Intent(mainActivity, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

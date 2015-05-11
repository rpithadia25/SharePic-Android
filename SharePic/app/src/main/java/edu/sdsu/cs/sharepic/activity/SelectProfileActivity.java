package edu.sdsu.cs.sharepic.activity;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.Utils;
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

        loadProfiles();
        init();

        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent profileDetailActivityIntent = new Intent(getApplicationContext(), ProfileDetailActivity.class);
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
                Intent createProfileActivityIntent = new Intent(getApplicationContext(), CreateProfileActivity.class);
                startActivityForResult(createProfileActivityIntent, INTENT_REQUEST_CODE);
            }
        });
    }

    private void init() {
        supportedAccounts = Account.supportedAccounts(getApplicationContext());
        profileListView = (ListView) findViewById(R.id.listView);
        profileNames = new ArrayList<>();
        profileNamesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, profileNames);
        profileListView.setAdapter(profileNamesAdapter);
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
                saveSharedPreferences();
        }
    }

    public void saveSharedPreferences() {
        ArrayList jsonArray = new ArrayList();
        Iterator profileIterator = Profiles.getInstance().iterator();
        HashMap dictionary = new HashMap();
        Profile currentProfile;
        while (profileIterator.hasNext()) {
            currentProfile = (Profile) profileIterator.next();
            dictionary.put(Constants.PROFILE_NAME,currentProfile.getProfileName());
            ArrayList accountsJSONArray = new ArrayList();
            for (Integer accountPosition: currentProfile.getAccountsPositions()) {
                accountsJSONArray.add(accountPosition);
            }
            dictionary.put(Constants.ACCOUNTS,accountsJSONArray);
            jsonArray.add(dictionary);
        }
        Utils.storeInSharedPreferences(getApplicationContext(), Constants.PROFILES, jsonArray.toString());
    }

    public void loadProfiles() {
        String sharedData = Utils.getFromSharedPreferences(getApplicationContext(), Constants.PROFILES);
        if (sharedData != null) {
            try {
                JSONArray jsonArray = new JSONArray(sharedData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    profileNames.add((String)jsonObject.get(Constants.PROFILE_NAME));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            Intent settingsActivityIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

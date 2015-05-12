package edu.sdsu.cs.sharepic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.model.Dropbox;
import edu.sdsu.cs.sharepic.model.FlickrAccount;

public class SettingsActivity extends ActionBarActivity {

    Switch dropboxSwitch;
    Switch flickrSwitch;
    Dropbox dropboxInstance ;
    FlickrAccount flickrInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        dropboxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !dropboxInstance.isLoggedIn()) {
                    dropboxInstance.login();
                }

                if (!isChecked) {
                    dropboxInstance.logout();
                }
            }
        });

        flickrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !flickrInstance.isLoggedIn()) {
                    flickrInstance.login();
                }

                if (!isChecked) {
                    flickrInstance.logout();
                }
            }
        });
    }

    private void init() {
        dropboxInstance = Dropbox.getInstance(getApplicationContext());
        flickrInstance = FlickrAccount.getInstance(getApplicationContext());
        dropboxSwitch = (Switch) findViewById(R.id.dropbox_login_switch);
        flickrSwitch = (Switch) findViewById(R.id.flickr_login_switch);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dropboxInstance != null) {
            dropboxInstance.finishLogin();
        }

        if (flickrInstance != null) {
            flickrInstance.finishLogin(getIntent());
        }

        if (dropboxInstance.isLoggedIn()) {
            dropboxSwitch.setChecked(true);
        } else {
            dropboxSwitch.setChecked(false);
        }

        if (flickrInstance.isLoggedIn()) {
            flickrSwitch.setChecked(true);
        } else {
            flickrSwitch.setChecked(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //this is very important, otherwise you would get a null Scheme in the onResume later on.
        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds profileNames to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

package edu.sdsu.cs.sharepic.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.googlecode.flickrjandroid.Flickr;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.model.Dropbox;
import edu.sdsu.cs.sharepic.model.FlickrAccount;

public class SettingsActivity extends AppCompatActivity {

    Switch dropboxSwitch;
    Switch flickrSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Dropbox dropboxInstance = Dropbox.getInstance(getApplicationContext());
        final FlickrAccount flickrInstance = FlickrAccount.getInstance(getApplicationContext());

        dropboxSwitch = (Switch) findViewById(R.id.dropbox_login_switch);
        if (dropboxInstance.isLoggedIn()) {
            dropboxSwitch.setChecked(true);
        } else {
            dropboxSwitch.setChecked(false);
        }
        dropboxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    dropboxInstance.login();
                    dropboxInstance.finishLogin();
                } else {
                    dropboxInstance.logout();
                }
            }
        });

        flickrSwitch = (Switch) findViewById(R.id.flickr_login_switch);
        flickrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flickrInstance.login();
                } else {
                    flickrInstance.logout();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

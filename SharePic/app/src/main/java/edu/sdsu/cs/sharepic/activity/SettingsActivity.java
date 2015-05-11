package edu.sdsu.cs.sharepic.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.io.IOException;
import java.io.InputStream;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.model.Account;
import edu.sdsu.cs.sharepic.model.Dropbox;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "SettingsActivity";
    Switch dropboxSwitch;
    Switch flickrSwitch;
    Account[] accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        accounts = Account.supportedAccounts(getApplicationContext());

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_account_settings);
        float scale = getResources().getDisplayMetrics().density;
        for (int i=0; i < accounts.length; i++) {
            Switch accountSwitch = new Switch(this);
            accountSwitch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            accountSwitch.setText(accounts[i].toString());
            accountSwitch.setPadding(0, 0, 0, (int) (10 * scale + 0.5f));
            accountSwitch.setTag(i);
            accountSwitch.setOnCheckedChangeListener(this);
            linearLayout.addView(accountSwitch);
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //this is very important, otherwise you would get a null Scheme in the onResume later on.
        setIntent(intent);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Account account = accounts[Integer.valueOf(buttonView.getTag().toString())];
        Log.i(TAG, account.toString());
    }
}

package edu.sdsu.cs.sharepic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.ArrayList;

import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.model.Account;
import edu.sdsu.cs.sharepic.model.LoginListener;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, LoginListener{

    private static final String TAG = "SettingsActivity";
    Account[] accounts;
    private ArrayList<Switch> switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        accounts = Account.supportedAccounts(this);
        initLayout();
    }

    @Override
    public void onBackPressed() {
        for (Account account : accounts) {
            account.setFromSettings(false);
        }
        super.onBackPressed();
    }

    private void initLayout() {
        switches = new ArrayList<>();
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
            if (accounts[i].isLoggedIn()) {
                accountSwitch.setChecked(true);
            }
            accounts[i].setFromSettings(true);
            switches.add(accountSwitch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Account account : accounts) {
            account.finishLogin(requestCode, resultCode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (Account account : accounts) {
            account.finishLogin();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Account account = accounts[Integer.valueOf(buttonView.getTag().toString())];
        if (isChecked && !account.isLoggedIn()) {
            account.login(this);
        }

        if (!isChecked) {
            account.logout();
        }
    }

    @Override
    public void loggedIn(String loggedInAccountName) {
        for (int i=0; i < switches.size(); i++) {
            Switch aSwitch = switches.get(i);
            Account account = accounts[i];

            if (account.toString().equalsIgnoreCase(loggedInAccountName)) {
                aSwitch.setChecked(true);
            }
        }
    }
}

package edu.sdsu.cs.sharepic.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.sdsu.cs.sharepic.R;
import edu.sdsu.cs.sharepic.classes.Constants;
import edu.sdsu.cs.sharepic.model.Account;
import edu.sdsu.cs.sharepic.model.Profile;
import edu.sdsu.cs.sharepic.model.Profiles;

public class CreateProfileActivity extends ActionBarActivity implements View.OnClickListener {

    private Button saveButton;
    private ListView supportedAccountsListView;
    private ArrayAdapter<String> accountsAdapter;
    private EditText profileName;
    private Profile profile;
    private Account[] supportedAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        init();
        supportedAccountsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        supportedAccountsListView.setAdapter(accountsAdapter);
        saveButton.setOnClickListener(this);
    }

    private void init() {
        supportedAccounts = Account.supportedAccounts(this);
        supportedAccountsListView = (ListView) findViewById(R.id.accountsList);
        saveButton = (Button) findViewById(R.id.saveButton);
        profileName = (EditText) findViewById(R.id.profileName);
        profile = new Profile();
        accountsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, supportedAccounts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_profile, menu);
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
    public void onClick(View v) {
        if (profileName.length() != 0 && supportedAccountsListView.getCheckedItemCount() != 0) {
            SparseBooleanArray checked = supportedAccountsListView.getCheckedItemPositions();

            profile.setProfileName(profileName.getText().toString());
            for (int i = 0; i < checked.size(); i++) {
                int position = checked.keyAt(i);
                if (checked.valueAt(i)) {
                    profile.addAccountPosition(position);
                }
            }

            Profiles.getInstance().add(profile);
            setResult(RESULT_OK);
            finish();
        } else if (profileName.length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle(Constants.ALERT_TITLE)
                    .setMessage(Constants.PROFILE_NAME_EMPTY_MESSAGE)
                    .setNeutralButton(Constants.OKAY, null).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(Constants.ALERT_TITLE)
                    .setMessage(Constants.NO_ACCOUNT_SELECTED_MESSAGE)
                    .setNeutralButton(Constants.OKAY, null).show();
        }
    }
}

package sim.ami.com.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Setting extends PreferenceActivity {


  private final String PRIVACY_LINK = "http://gremaapps.blogspot.com.ng/p/privacy-policy.html?m=1";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();


  }

  @SuppressLint("ValidFragment")
  public class MyPreferenceFragment extends PreferenceFragment {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences);
      /*
      TextView privacy;
      privacy = (TextView) findViewById(R.id.privacy_policy);
      privacy.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_LINK));
            startActivity(myIntent);
          } catch (ActivityNotFoundException e) {

          }
        }
      });
      */
      Preference preference = findPreference("privacy_policy");
      preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_LINK));
            startActivity(myIntent);
          } catch (ActivityNotFoundException e) {

          }
          return false;
        }
      });

    }
  }

  @Override
  public void onBackPressed() {
    //ChatHeadService.visiblePopupView();
    sendCommandToService();
    super.onBackPressed();
  }

  private void sendCommandToService() {
    Intent intent = new Intent(Setting.this, ChatHeadService.class);
    intent.putExtra(Constant.COMMAND, Constant.CMD_UPDATE_CONFIG);
    startService(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //getMenuInflater().inflate(R.menu.home,menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      default:
        break;
    }
    return true;
  }
}

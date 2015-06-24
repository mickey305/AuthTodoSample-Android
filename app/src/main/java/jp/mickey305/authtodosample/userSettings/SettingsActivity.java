package jp.mickey305.authtodosample.userSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import jp.mickey305.authtodosample.R;
import jp.mickey305.authtodosample.util.SharedPreferencesObject;
import jp.mickey305.authtodosample.TodoApplication;

public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        final TodoApplication app = (TodoApplication) getApplication();
        final TodoPreferenceFragment todoPreferenceFragment = new TodoPreferenceFragment();
        todoPreferenceFragment.setCallback(new TodoPreferenceFragment.Callback() {
            @Override
            public void onAPIRegisterSucceeded() {
                app.setFaceId(todoPreferenceFragment.getFaceId());
            }
            @Override
            public void onAPIDeleteSucceeded() {
                app.setFaceId(0);
            }
        });
        replaceFragment(todoPreferenceFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferencesObject sp;
        TodoApplication app = (TodoApplication) getApplication();
        sp = new SharedPreferencesObject(this);
        sp.write(getResources().getString(R.string.setting_key_login_status), app.isStatusLogin());
        sp.write(getResources().getString(R.string.setting_key_face_id_num), app.getFaceId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_setting, fragment)
                .commit();
    }
}

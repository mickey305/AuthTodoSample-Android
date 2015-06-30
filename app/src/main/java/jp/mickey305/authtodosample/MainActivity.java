package jp.mickey305.authtodosample;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import jp.mickey305.authtodosample.userSettings.SettingsActivity;
import jp.mickey305.authtodosample.util.SharedPreferencesObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        NavigationDrawerItemTypeCode
{
    private static final String TAG = "MainActivity";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new NavigationDrawerAdapter(this, createNavigationDrawerItemList());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        mNavigationDrawerFragment.setup(
                R.id.fragment_drawer,
                (DrawerLayout) findViewById(R.id.drawer),
                mAdapter
        );

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mAdapter.getItem(position).getType() == TYPE_SEPARATOR) { return; }

        TodoApplication app = (TodoApplication) getApplication();

        switch (position) {
        case 0:
            Fragment fg =(app.isStatusLogin())? createTodoListFragment(): createAuthFragment();
            replaceFragment(fg);
            break;
        case 1:
            break;
        case 2:
            openUri("https://github.com/mickey305/AuthTodoSample-Android/");
            break;
        case 3:
            openUri("https://github.com/mickey305/AuthTodoSample-Android/issues");
            break;
        case 4:
            break;
        case 5:
            startActivity(SettingsActivity.class);
            break;
        default:
            break;
        }
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
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private TodoListFragment createTodoListFragment() {
        final TodoApplication app = (TodoApplication) getApplication();
        final TodoListFragment todoFragment = new TodoListFragment();
        todoFragment.setCallback(new TodoListFragment.Callback() {
            @Override
            public void onLogoutSucceeded() {
                app.setStatusLogin(false);
                replaceFragment(createAuthFragment());
            }
        });
        return todoFragment;
    }

    private AuthFragment createAuthFragment() {
        final TodoApplication app = (TodoApplication) getApplication();
        final AuthFragment authFragment = new AuthFragment();
        authFragment.setAccessCallback(new AuthFragment.AccessCallback() {
            @Override
            public void onLoginSucceeded() {
                app.setStatusLogin(true);
                replaceFragment(createTodoListFragment());
            }
        });
        return authFragment;
    }

    private void openUri(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment) {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit();
    }

    private List<NavigationDrawerItem> createNavigationDrawerItemList() {
        List<NavigationDrawerItem> list = new ArrayList<>();
        list.add(new NavigationDrawerItem(R.drawable.ic_inbox, getResources().getString(R.string.drawer_item_inbox), TYPE_CHECKABLE_ITEM));
        list.add(new NavigationDrawerItem(TYPE_SEPARATOR));
        list.add(new NavigationDrawerItem(R.drawable.ic_github, getResources().getString(R.string.drawer_item_github), TYPE_ITEM));
        list.add(new NavigationDrawerItem(R.drawable.ic_help, getResources().getString(R.string.drawer_item_help), TYPE_ITEM));
        list.add(new NavigationDrawerItem(TYPE_SEPARATOR));
        list.add(new NavigationDrawerItem(R.drawable.ic_settings, getResources().getString(R.string.drawer_item_settings), TYPE_ITEM));
        return list;
    }

    private void showToast(String msg) {
        Toast.makeText(this, "#"+TAG+" " +msg, Toast.LENGTH_LONG).show();
    }
}

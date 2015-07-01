package jp.mickey305.authtodosample;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import jp.mickey305.authtodosample.model.Todo;
import jp.mickey305.authtodosample.model.TodoManager;
import jp.mickey305.authtodosample.util.CustomDialog;
import jp.mickey305.authtodosample.util.CustomQuickLookDialog;

public class TodoListFragment extends Fragment implements TodoListAdapter.Callback, ResultCode{
    private final static String TAG = "TodoListFragment";
    private TodoManager mTodoManager;
    private TodoListAdapter mAdapter;
    private Callback callback;

    @InjectView(R.id.todo_list_lv) ListView mListView;
    @InjectView(R.id.todo_list_toolbar) Toolbar mToolbar;
    @InjectView(R.id.todo_list_empty_tv) TextView mEmptyTextView;

    @OnClick(R.id.todo_list_add_ib)
    public void onClickInsertButton() {
        startActivityForResult(TodoCreateActivity.createIntent(getActivity()), TODO_CREATE);
    }

    @OnItemClick(R.id.todo_list_lv)
    public void onItemClick(int position) {
        Todo todo = mAdapter.getItem(position);
        CustomQuickLookDialog dialog = new CustomQuickLookDialog();
        dialog.setTitle(todo.getName());
        dialog.setBody(todo.getSentence());
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onClickCheckBox(int position) {
        Todo todo = mAdapter.getItem(position);
        mTodoManager.update(todo, !todo.isCompleted());
        mAdapter.notifyDataSetChanged();
        mListView.invalidate();
    }

    @OnItemLongClick(R.id.todo_list_lv)
    boolean onItemLongClick(int position) {
        Todo todo = mAdapter.getItem(position);
        startActivityForResult(
                TodoCreateActivity.createIntent(getActivity(), todo.getId()), TODO_CREATE);
        return true;
    }

    public TodoListFragment() { }

    public interface Callback {
        void onLogoutSucceeded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_list, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mTodoManager = new TodoManager(getActivity());

        ActionBarActivity activity = (ActionBarActivity)getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new TodoListAdapter(getActivity(), mTodoManager.findAll(), true);
        mAdapter.setCallback(this);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyTextView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.todo_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_logout:
            CustomDialog dialog = new CustomDialog();
            dialog.setTitle("OK, Do Logout");
            dialog.setOnClickListener(new CustomDialog.OnClickListener() {
                @Override
                public void onClickPositiveButton() {
                    if(callback != null) callback.onLogoutSucceeded();
                }
                @Override
                public void onClickNegativeButton() { }
            });
            dialog.show(getFragmentManager(), TAG);
            return true;
        case R.id.m_delete:
            mTodoManager.deleteCompleted();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case TODO_CREATE:
                // RealmBaseAdapter watch Realm data changing.
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setCallback(Callback callback) { this.callback = callback; }
}

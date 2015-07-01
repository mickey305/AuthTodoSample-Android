package jp.mickey305.authtodosample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import jp.mickey305.authtodosample.model.Todo;

public class TodoListAdapter extends RealmBaseAdapter<Todo> implements ListAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Callback callback;

    public TodoListAdapter(Context context, RealmResults<Todo> realmResults,
                           boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public interface Callback {
        void onClickCheckBox(int position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.todo_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Todo todo = realmResults.get(position);

        int textColor = todo.isCompleted() ? R.color.myDisabledTextColor : R.color.myTextColor;
        viewHolder.textView.setText(todo.getName());
        viewHolder.textView.setTextColor(mContext.getResources().getColor(textColor));

        viewHolder.checkbBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) callback.onClickCheckBox(position);
            }
        });
        viewHolder.checkbBox.setChecked(todo.isCompleted());

        return convertView;
    }

    public static class ViewHolder {
        @InjectView(R.id.todo_list_item_tv) TextView textView;
        @InjectView(R.id.todo_list_item_checkbox) CheckBox checkbBox;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void setCallback(Callback callback) { this.callback = callback; }
}

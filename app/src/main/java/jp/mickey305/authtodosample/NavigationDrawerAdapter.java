package jp.mickey305.authtodosample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem>
        implements NavigationDrawerItemTypeCode
{

    private LayoutInflater mInflater;
    private List<NavigationDrawerItem> navigationDrawerItemList;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> list) {
        super(context, -1, list);
        mInflater = LayoutInflater.from(context);
        navigationDrawerItemList = list;
    }

    public boolean isCheckableItem(int position) {
        return (getItem(position).getType() == TYPE_CHECKABLE_ITEM);
    }

    @Override
    public int getCount() {
        return navigationDrawerItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return navigationDrawerItemList.get(position).getType();
    }

    @Override
    public NavigationDrawerItem getItem(int position) {
        return navigationDrawerItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationDrawerItem item = getItem(position);

        switch (item.getType()) {
            case TYPE_ITEM:
            case TYPE_CHECKABLE_ITEM:
                convertView = mInflater.inflate(R.layout.navigation_drawer_list_item, parent, false);
                TextView textView = ButterKnife.findById(convertView, R.id.navigation_drawer_list_item_tv);
                ImageView imageView = ButterKnife.findById(convertView, R.id.navigation_drawer_list_item_iv);
                textView.setText(item.getName());
                imageView.setImageResource(item.getResId());
                break;
            default:
                convertView = mInflater.inflate(R.layout.navigation_drawer_list_item_separator, parent, false);
                convertView.setEnabled(false);
                convertView.setOnClickListener(null);
                break;
        }

        return convertView;
    }
}

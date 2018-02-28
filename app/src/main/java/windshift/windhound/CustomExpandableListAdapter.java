package windshift.windhound;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listHeaderData;
    private HashMap<String, List<String>> listChildData;

    public CustomExpandableListAdapter(Context context, List<String> listHeaderData,
                                       HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listHeaderData = listHeaderData;
        this.listChildData = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return listChildData.get(listHeaderData.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        //TextView txtListChild = (TextView) convertView.findViewWithTag(android.R.layout.simple_list_item_1);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.textView_list_item);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildData.get(listHeaderData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeaderData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listHeaderData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_list_group);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

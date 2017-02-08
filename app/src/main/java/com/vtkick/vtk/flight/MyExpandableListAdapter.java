package com.vtkick.vtk.flight;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vtkick.vtk.flight.models.ReturnModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Flames on 12/1/17.
 * This class handles the expandable list that holds the search results.
 */
public class MyExpandableListAdapter implements ExpandableListAdapter {

    private Context context;
    private ArrayList<ReturnModel> ret;
    private ArrayList<String> children;

    public MyExpandableListAdapter(Context context, ArrayList<ReturnModel> deptList) {
        this.context = context;
        this.ret = deptList;
        this.children = new ArrayList<>();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public int getGroupCount() {
        if (ret != null)
            return ret.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPos) {
        if (ret.get(groupPos).getReturnDate() != null) //if return is enabled
            return 5;
        else
            return 3;
    }

    @Override
    public Object getGroup(int groupPos) {
        return ret.get(groupPos);
    }

    /**
     * Sets the data of the flight to the child.
     *
     * @return -> the child.
     */
    @Override
    public Object getChild(int groupPos, int childPos) {
        children.clear();
        children.add(ret.get(groupPos).getFromArpt() + ret.get(groupPos).getToArpt());
        children.add(ret.get(groupPos).getDepartFromTime() + " - " + ret.get(groupPos).getDepartToTime()
                + "  (" + context.getResources().getString(R.string.duration) + ": " + getDuration(ret.get(groupPos).getDepartureDate(), ret.get(groupPos).getArrivalDate()) + ")");
        children.add(ret.get(groupPos).getPrice() + "€");

        //if return is enabled, add the returning flight info
        if (ret.get(groupPos).getReturnDate() != null) {
            children.add("%" + context.getResources().getString(R.string.rtrn) + ": " + ret.get(groupPos).getReturnFromArpt() + ret.get(groupPos).getReturnToArpt());
            children.add("%" + ret.get(groupPos).getReturnFromTime() + " - " + ret.get(groupPos).getReturnToTime()
                    + "  (" + context.getResources().getString(R.string.duration) + ": " + getDuration(ret.get(groupPos).getReturnDate(), ret.get(groupPos).getReturnArrival()) + ")");
        }

        return children.get(childPos);
    }

    @Override
    public long getGroupId(int groupPos) {
        return groupPos;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Inflates the group and sets the appropriate text.
     */
    @Override
    public View getGroupView(int groupPos, boolean exp, View view, ViewGroup viewGroup) {
        if (view == null)
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.group_items, null);

        ((TextView) view.findViewById(R.id.heading)).setText(((ReturnModel) getGroup(groupPos)).getAirline());
        //mExpandableListView = (ExpandableListView) viewGroup;
        //mExpandableListView.expandGroup(groupPos);

        return view;
    }

    /**
     * Inflates the children and sets the appropriate text.
     */
    @Override
    public View getChildView(int groupPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        if (view == null)
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.child_items, null);

        TextView childItem = (TextView) view.findViewById(R.id.childItem);
        childItem.setText(((String) getChild(groupPos, childPos)).trim());
        if (childItem.getText().toString().charAt(0) == '%') {
            childItem.setTypeface(null, Typeface.ITALIC);
            childItem.setText(childItem.getText().toString().substring(1, childItem.getText().toString().length()));
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPos, int childPos) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int pos) {
        //mExpandableListView.expandGroup(pos);
    }

    @Override
    public void onGroupCollapsed(int pos) {
        //mExpandableListView.collapseGroup(pos);
    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    /**
     * Calculates the duration of the flight.
     *
     * @param departure -> the departure date.
     * @param arrival   -> the arrival date.
     * @return -> the duration of the flight.
     */
    private String getDuration(String departure, String arrival) {
        String dateStart = departure;
        String dateStop = arrival;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        dateStart = dateStart.replaceAll("T", " ");
        dateStop = dateStop.replaceAll("T", " ");

        Date d1;
        Date d2;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            //to minutes, hours and days
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            String s = "";
            if (diffDays > 0)
                s += diffDays + context.getResources().getString(R.string.days) + " ";
            s += diffHours + context.getResources().getString(R.string.hours) + " ";
            s += diffMinutes + context.getResources().getString(R.string.minutes);
            return s;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

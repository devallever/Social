package com.allever.social.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by XM on 2016/5/18.
 */
public class ListViewUtil {

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /** 动态改变listView的高度 */
    public  static void setExpandableListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // params.height = 80 * (listAdapter.getCount() - 1);
        // params.height = 80 * (listAdapter.getCount());
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);

    }

    /**
     * 可扩展listview展开时调用
     *
     * @param listView
     * @param groupPosition
     */
    public static void setExpandedListViewHeightBasedOnChildren(
            ExpandableListView listView, int groupPosition) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }
        View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
                listView);
        listItem.measure(0, 0);
        int appendHeight = 0;
        for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
            appendHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        Log.d(TAG, "Expand params.height" + params.height);
        params.height += appendHeight;
        listView.setLayoutParams(params);
    }


    /**
     * 可扩展listview收起时调用
     *
     * @param listView
     * @param groupPosition
     */
    public static void setCollapseListViewHeightBasedOnChildren(
            ExpandableListView listView, int groupPosition) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }
        View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
                listView);
        listItem.measure(0, 0);
        int appendHeight = 0;
        for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
            appendHeight += listItem.getMeasuredHeight();
        }
        /*Log.d(TAG,
                "Collapse childCount="
                        + listAdapter.getChildrenCount(groupPosition));*/
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height -= appendHeight;
        listView.setLayoutParams(params);
    }
}

package com.allever.social.view.sortlistview;

/**
 * Created by XM on 2016/6/14.
 */
import com.allever.social.pojo.FriendItem;
import com.allever.social.pojo.NearByUserItem;

import java.util.Comparator;

public class PinyinComparator implements Comparator<FriendItem> {

    public int compare(FriendItem o1, FriendItem o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}


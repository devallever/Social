package com.allever.social.ease;

import com.allever.social.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;

import java.util.Arrays;

/**
 * Created by XM on 2016/7/25.
 */
public class QQFaceGroupData {

    private static int[] icons = new int[]{
            R.drawable.f000,
            R.drawable.f001,
            R.drawable.f002,
            R.drawable.f003,
            R.drawable.f004,
            R.drawable.f005,
            R.drawable.f006,
            R.drawable.f009,
            R.drawable.f010,
            R.drawable.f011,
            R.drawable.f012,
            R.drawable.f013,
            R.drawable.f014,
            R.drawable.f015,
            R.drawable.f017,
            R.drawable.f018,
            R.drawable.f019,
            R.drawable.f020,
            R.drawable.f021,
            R.drawable.f022,
            R.drawable.f023,
            R.drawable.f024,
            R.drawable.f025,
            R.drawable.f026,
            R.drawable.f027,
            R.drawable.f029,
            R.drawable.f030,
            R.drawable.f031,
            R.drawable.f033,
            R.drawable.f034,
            R.drawable.f035,
            R.drawable.f036,
            R.drawable.f037,
            R.drawable.f038,
            R.drawable.f040,
            R.drawable.f041,
    };

    private static int[] bigIcons = new int[]{
            R.drawable.f000,
            R.drawable.f001,
            R.drawable.f002,
            R.drawable.f003,
            R.drawable.f004,
            R.drawable.f005,
            R.drawable.f006,
            R.drawable.f009,
            R.drawable.f010,
            R.drawable.f011,
            R.drawable.f012,
            R.drawable.f013,
            R.drawable.f014,
            R.drawable.f015,
            R.drawable.f017,
            R.drawable.f018,
            R.drawable.f019,
            R.drawable.f020,
            R.drawable.f021,
            R.drawable.f022,
            R.drawable.f023,
            R.drawable.f024,
            R.drawable.f025,
            R.drawable.f026,
            R.drawable.f027,
            R.drawable.f029,
            R.drawable.f030,
            R.drawable.f031,
            R.drawable.f033,
            R.drawable.f034,
            R.drawable.f035,
            R.drawable.f036,
            R.drawable.f037,
            R.drawable.f038,
            R.drawable.f040,
            R.drawable.f041,
    };


    private static final EaseEmojiconGroupEntity DATA = createData();

    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], null, EaseEmojicon.Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            datas[i].setName("表情" + (i + 1));
            switch (i){
                case 0:
                    datas[i].setName("呲牙");
                    break;
                case 1:
                    datas[i].setName("调皮");
                    break;
                case 2:
                    datas[i].setName("流汗");
                    break;
                case 3:
                    datas[i].setName("偷笑");
                    break;
                case 4:
                    datas[i].setName("再见");
                    break;
                case 5:
                    datas[i].setName("敲打");
                    break;
                case 6:
                    datas[i].setName("察汗");
                    break;
                case 7:
                    datas[i].setName("流泪");
                    break;
                case 8:
                    datas[i].setName("大哭");
                    break;
                case 9:
                    datas[i].setName("嘘");
                    break;
                case 10:
                    datas[i].setName("酷");
                    break;
                case 11:
                    datas[i].setName("抓狂");
                    break;
                case 12:
                    datas[i].setName("委屈");
                    break;
                case 13:
                    datas[i].setName("便便");
                    break;
                case 14:
                    datas[i].setName("菜刀");
                    break;
                case 15:
                    datas[i].setName("可爱");
                    break;
                case 16:
                    datas[i].setName("色");
                    break;
                case 17:
                    datas[i].setName("害羞");
                    break;
                case 18:
                    datas[i].setName("得意");
                    break;
                case 19:
                    datas[i].setName("吐");
                    break;
                case 20:
                    datas[i].setName("微笑");
                    break;
                case 21:
                    datas[i].setName("发怒");
                    break;
                case 22:
                    datas[i].setName("尴尬");
                    break;
                case 23:
                    datas[i].setName("惊恐");
                    break;
                case 24:
                    datas[i].setName("冷汗");
                    break;
                case 25:
                    datas[i].setName("嘴唇");
                    break;
                case 26:
                    datas[i].setName("白眼");
                    break;
                case 27:
                    datas[i].setName("傲慢");
                    break;
                case 28:
                    datas[i].setName("哇");
                    break;
                case 29:
                    datas[i].setName("疑问");
                    break;
                case 30:
                    datas[i].setName("睡觉");
                    break;
                case 31:
                    datas[i].setName("亲亲");
                    break;
                case 32:
                    datas[i].setName("憨笑");
                    break;
                case 33:
                    datas[i].setName("爱情");
                    break;
                case 34:
                    datas[i].setName("撇嘴");
                    break;
                case 35:
                    datas[i].setName("阴险");
                    break;




            }
            datas[i].setIdentityCode("em"+ (1000+i+1));
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.f000_static);
        emojiconGroupEntity.setType(EaseEmojicon.Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }


    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }

}

package com.can.appstore.index.data;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.Request.Method.HEAD;

/**
 * Created by liuhao on 2016/10/31.
 * 魏梦帆负责每天更新5个内涵段子
 */

public class JokeData {
    private static List<String> jokeList = new ArrayList<>();

    public static List<String> getJokeList() {
        jokeList.add("　三岁的儿子从幼儿园回来气呼呼的对我说：“爸爸，老师一点都不好，总是凶我，中午还不拍拍我睡觉。”\n" +
                "　　我：“老师不可能像妈妈一样照顾那么多人的，你要听话。”\n" +
                "　　儿子：“让老师和妈妈换换就好了，妈妈每天在幼儿园陪我，老师在家陪爸爸睡觉。”\n" +
                "　　想想竟然有点激动呢");
        jokeList.add("上学时和同学一起去打热水，回宿舍路上暖瓶吱吱的响。\n" +
                "　　我说：“不好要炸啦。”\n" +
                "　　这哥们嗖的一声把暖瓶扔出去了，嘭，果然炸了。\n" +
                "　　这哥们心有余悸的说：“还好我扔的快，没炸到我。");
        jokeList.add("老师提问小明：“你知道西班牙的国王是谁吗？”\n" +
                "　　小明：“嗯，我不知道。”\n" +
                "　　老师：“那你今后一定要多花功夫在学习上啊！”\n" +
                "　　小明问：“老师，你知道贾翠花是谁吗？”\n" +
                "　　老师：“那是谁？我不知道。”\n" +
                "　　小明：“那你今后一定要多花功夫在老公身上啊！");
        jokeList.add("在外地打工回来，让媳妇来车站接我。\n" +
                "　　车晚点了就在车站附近一个宾馆住下了。\n" +
                "　　到前台我给钱的时候，媳妇说：“我有VIP卡！”\n" +
                "　　我一惊：“你怎么会有的？”\n" +
                "　　她说：“猜到今天晚上你肯定要带我来宾馆，所以就办了一张。”");
        jokeList.add("一男同学，冲出教室不小心摸到一位女同学的胸，刚想道歉。\n" +
                "　　只见女同学很生气的说：“真不要脸，乱摸人家胸。”\n" +
                "　　男同学立马整个人都不好了，回答道：“你有吗？我怎么没感觉啊！”\n" +
                "　　女生又问：“那你知道为什么地球是圆的，我们也没感觉到吗？那是因为它大！”");
        return jokeList;
    }

}

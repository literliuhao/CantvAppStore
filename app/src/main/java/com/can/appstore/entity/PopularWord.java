package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 热词
 */
public class PopularWord {

    /**
     * word : 斗地主
     * pinyin : DDZ
     */

    @SerializedName("word")
    private String word;
    @SerializedName("pinyin")
    private String pinyin;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}

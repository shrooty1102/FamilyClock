package com.themeinnov8.code.familyclock;

/**
 * Created by code on 3/8/15.
 */
public class MyGroups {


    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String _groupname;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String _groupid;

    public MyGroups() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return _groupname;
    }

    public String getId() {
        return _groupid;
    }

    public final void setText(String text) {
        _groupname = text;
    }

    public final void setId(String id) {
        _groupid = id;
    }




}

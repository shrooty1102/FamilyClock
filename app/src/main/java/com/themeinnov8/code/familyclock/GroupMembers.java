package com.themeinnov8.code.familyclock;

/**
 * Created by code on 3/8/15.
 */
public class GroupMembers {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String _membername;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String _memberid;

    public GroupMembers() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return _membername;
    }

    public String getId() {
        return _memberid;
    }

    public final void setText(String text) {
        _membername = text;
    }

    public final void setId(String id) {
        _memberid = id;
    }



}

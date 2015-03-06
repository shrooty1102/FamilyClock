package com.themeinnov8.code.familyclock;

/**
 * Created by code on 3/5/15.
 */
public class Member {

    private String _imei;
    private String _name;
    private String _rcode;
    private long _phno;
    //private String doing;

    // Item Id
    @com.google.gson.annotations.SerializedName("id")
    private String Id;

    public void set_imei(String _imei) {
        this._imei = _imei;
    }
    public String get_imei() {
        return this._imei;
    }

    public void set_name(String mname) {
        _name = mname;
    }
    public String get_name() {
        return _name;
    }

    public void set_rcode(String regioncode) {
        _rcode = regioncode;
    }
    public String get_rcode() {
        return _rcode;
    }

    public void set_phno(long phnumber) {
        _phno = phnumber;
    }
    public long get_phno() {
        return _phno;
    }

    /*public void setDoing(String doing) {
        this.doing = doing;
    }
    public String getDoing() {
        return this.doing;
    }*/

    //Returns the item id
    public String getId() {
        return Id;
    }

    //Sets the item id
    public final void setId(String id) {
        Id = id;
    }
}

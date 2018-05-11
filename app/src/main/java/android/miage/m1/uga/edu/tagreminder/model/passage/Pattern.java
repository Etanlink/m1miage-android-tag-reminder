package android.miage.m1.uga.edu.tagreminder.model.passage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pattern {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("desc")
    @Expose
    public String desc;
    @SerializedName("dir")
    @Expose
    public Integer dir;
    @SerializedName("shortDesc")
    @Expose
    public String shortDesc;

    public Pattern(String id, String desc, Integer dir, String shortDesc) {
        this.id = id;
        this.desc = desc;
        this.dir = dir;
        this.shortDesc = shortDesc;
    }

    /* GETTER */
    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getDir() {
        return dir;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    /* SETTER */

    public void setId(String id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDir(Integer dir) {
        this.dir = dir;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
}

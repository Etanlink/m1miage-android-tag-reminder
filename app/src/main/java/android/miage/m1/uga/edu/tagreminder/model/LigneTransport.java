
package android.miage.m1.uga.edu.tagreminder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LigneTransport implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("longName")
    @Expose
    private String longName;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("type")
    @Expose
    private String type;

    public LigneTransport(String id, String shortName, String longName, String color, String textColor, String mode, String type) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
        this.color = color;
        this.textColor = textColor;
        this.mode = mode;
        this.type = type;
    }

    /* GETTER */
    public String getId() { return id; }
    public String getShortName() { return shortName; }
    public String getLongName() { return longName; }
    public String getColor() { return color; }
    public String getTextColor() { return textColor; }
    public String getMode() { return mode; }
    public String getType() { return type; }

    /* SETTER */
    public void setId(String id) { this.id = id; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setLongName(String longName) { this.longName = longName; }
    public void setColor(String color) { this.color = color; }
    public void setTextColor(String textColor) { this.textColor = textColor; }
    public void setMode(String mode) { this.mode = mode; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "LigneTransport{" +
                "id='" + id + '\'' +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", color='" + color + '\'' +
                ", textColor='" + textColor + '\'' +
                ", mode='" + mode + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

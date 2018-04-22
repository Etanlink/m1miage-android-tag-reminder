package android.miage.m1.uga.edu.tagreminder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Arret implements Serializable {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("lat")
    @Expose
    private Double lat;

    public Arret(String code, String city, String name, Double lon, Double lat) {
        super();
        this.code = code;
        this.city = city;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    /* GETTER */
    public String getCode() { return code; }
    public String getCity() { return city; }
    public String getName() { return name; }
    public Double getLon() { return lon; }
    public Double getLat() { return lat; }

    /* SETTER */
    public void setCode(String code) { this.code = code; }
    public void setCity(String city) { this.city = city; }
    public void setName(String name) { this.name = name; }
    public void setLon(Double lon) { this.lon = lon; }
    public void setLat(Double lat) { this.lat = lat; }

    @Override
    public String toString() {
        return "Arret{" +
                "code='" + code + '\'' +
                ", city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}

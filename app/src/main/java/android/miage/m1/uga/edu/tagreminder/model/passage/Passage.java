package android.miage.m1.uga.edu.tagreminder.model.passage;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Passage {

    @SerializedName("pattern")
    @Expose
    public Pattern pattern;
    @SerializedName("times")
    @Expose
    public List<Time> times = new ArrayList<Time>();

    public Passage(Pattern pattern, List<Time> times) {
        this.pattern = pattern;
        this.times = times;
    }

    /* GETTER */
    public Pattern getPattern() { return pattern; }
    public List<Time> getTimes() { return times; }

    /* SETTER */
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
    public void setTimes(List<Time> times) { this.times = times; }

    @Override
    public String toString() {
        return "Passage{" +
                "pattern=" + pattern +
                ", times=" + times +
                '}';
    }
}
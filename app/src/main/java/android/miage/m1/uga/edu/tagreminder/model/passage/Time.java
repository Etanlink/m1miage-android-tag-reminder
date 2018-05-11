package android.miage.m1.uga.edu.tagreminder.model.passage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Time {

    @SerializedName("stopId")
    @Expose
    public String stopId;
    @SerializedName("stopName")
    @Expose
    public String stopName;
    @SerializedName("scheduledArrival")
    @Expose
    public Integer scheduledArrival;
    @SerializedName("scheduledDeparture")
    @Expose
    public Integer scheduledDeparture;
    @SerializedName("realtimeArrival")
    @Expose
    public Integer realtimeArrival;
    @SerializedName("realtimeDeparture")
    @Expose
    public Integer realtimeDeparture;
    @SerializedName("arrivalDelay")
    @Expose
    public Integer arrivalDelay;
    @SerializedName("departureDelay")
    @Expose
    public Integer departureDelay;
    @SerializedName("timepoint")
    @Expose
    public Boolean timepoint;
    @SerializedName("realtime")
    @Expose
    public Boolean realtime;
    @SerializedName("serviceDay")
    @Expose
    public Integer serviceDay;
    @SerializedName("tripId")
    @Expose
    public String tripId;

    public Time(String stopId, String stopName, Integer scheduledArrival, Integer scheduledDeparture, Integer realtimeArrival, Integer realtimeDeparture, Integer arrivalDelay, Integer departureDelay, Boolean timepoint, Boolean realtime, Integer serviceDay, String tripId) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.realtimeArrival = realtimeArrival;
        this.realtimeDeparture = realtimeDeparture;
        this.arrivalDelay = arrivalDelay;
        this.departureDelay = departureDelay;
        this.timepoint = timepoint;
        this.realtime = realtime;
        this.serviceDay = serviceDay;
        this.tripId = tripId;
    }

    /* GETTER */
    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public Integer getScheduledArrival() {
        return scheduledArrival;
    }

    public Integer getScheduledDeparture() {
        return scheduledDeparture;
    }

    public Integer getRealtimeArrival() {
        return realtimeArrival;
    }

    public Integer getRealtimeDeparture() {
        return realtimeDeparture;
    }

    public Integer getArrivalDelay() {
        return arrivalDelay;
    }

    public Integer getDepartureDelay() {
        return departureDelay;
    }

    public Boolean getTimepoint() {
        return timepoint;
    }

    public Boolean getRealtime() {
        return realtime;
    }

    public Integer getServiceDay() {
        return serviceDay;
    }

    public String getTripId() {
        return tripId;
    }

    /* SETTER */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setScheduledArrival(Integer scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public void setScheduledDeparture(Integer scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public void setRealtimeArrival(Integer realtimeArrival) {
        this.realtimeArrival = realtimeArrival;
    }

    public void setRealtimeDeparture(Integer realtimeDeparture) {
        this.realtimeDeparture = realtimeDeparture;
    }

    public void setArrivalDelay(Integer arrivalDelay) {
        this.arrivalDelay = arrivalDelay;
    }

    public void setDepartureDelay(Integer departureDelay) {
        this.departureDelay = departureDelay;
    }

    public void setTimepoint(Boolean timepoint) {
        this.timepoint = timepoint;
    }

    public void setRealtime(Boolean realtime) {
        this.realtime = realtime;
    }

    public void setServiceDay(Integer serviceDay) {
        this.serviceDay = serviceDay;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}

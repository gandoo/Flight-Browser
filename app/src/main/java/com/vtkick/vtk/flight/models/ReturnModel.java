package com.vtkick.vtk.flight.models;

/**
 * Created by Flames on 12/1/17.
 * This class describes a flight.
 */
public class ReturnModel {

    private String airline;

    private String fromOrigin; //unused. kept for wholeness
    private String fromArpt;
    private String toArpt;
    private String departureDate;
    private String arrivalDate;
    private String departFromTime;
    private String returnFromTime;

    private String toDestination; //unused. kept for wholeness
    private String returnFromArpt;
    private String returnToArpt;
    private String returnDate;
    private String returnArrival;
    private String departToTime;
    private String returnToTime;
    private String price;


    public void setFromOrigin(String fromOrigin) {
        this.fromOrigin = fromOrigin;
    }

    public void setToDestination(String toDestination) {
        this.toDestination = toDestination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDepartFromTime() {
        return departFromTime;
    }

    public void setDepartFromTime(String departFromTime) {
        this.departFromTime = departFromTime;
    }

    public String getDepartToTime() {
        return departToTime;
    }

    public void setDepartToTime(String departToTime) {
        this.departToTime = departToTime;
    }

    public String getFromArpt() {
        return fromArpt;
    }

    public void setFromArpt(String fromArpt) {
        this.fromArpt = fromArpt;
    }

    public String getToArpt() {
        return toArpt;
    }

    public void setToArpt(String toArpt) {
        this.toArpt = toArpt;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getReturnArrival() {
        return returnArrival;
    }

    public void setReturnArrival(String returnArrival) {
        this.returnArrival = returnArrival;
    }

    public String getReturnFromTime() {
        return returnFromTime;
    }

    public void setReturnFromTime(String returnFromTime) {
        this.returnFromTime = returnFromTime;
    }

    public String getReturnToTime() {
        return returnToTime;
    }

    public void setReturnToTime(String returnToTime) {
        this.returnToTime = returnToTime;
    }

    public String getReturnFromArpt() {
        return returnFromArpt;
    }

    public void setReturnFromArpt(String returnFromArpt) {
        this.returnFromArpt = returnFromArpt;
    }

    public String getReturnToArpt() {
        return returnToArpt;
    }

    public void setReturnToArpt(String returnToArpt) {
        this.returnToArpt = returnToArpt;
    }
}

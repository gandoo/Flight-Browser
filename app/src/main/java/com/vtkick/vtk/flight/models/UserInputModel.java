package com.vtkick.vtk.flight.models;

/**
 * Created by Flames on 12/1/17.
 * This class has the information that the user gives
 * to search a flight.
 */
public class UserInputModel {

    private String fromMsg;
    private String toMsg;
    private String fromDateMsg;
    private String toDateMsg;
    private String adultsMsg;
    private String childrenMsg;
    private String infantsMsg;
    private String classMsg;
    private String directOnlyMsg;

    public String getToMsg() {
        return toMsg;
    }

    public String getFromMsg() {
        return fromMsg;
    }

    public String getFromDateMsg() {
        return fromDateMsg;
    }

    public String getToDateMsg() {
        return toDateMsg;
    }

    public String getAdultsMsg() {
        return adultsMsg;
    }

    public String getChildrenMsg() {
        return childrenMsg;
    }

    public String getInfantsMsg() {
        return infantsMsg;
    }

    public String getClassMsg() {
        return classMsg;
    }

    public String getDirectOnlyMsg() {
        return directOnlyMsg;
    }

    public void setFromMsg(String fromMsg) {
        this.fromMsg = fromMsg;
    }

    public void setToMsg(String toMsg) {
        this.toMsg = toMsg;
    }

    public void setFromDateMsg(String fromDateMsg) {
        this.fromDateMsg = fromDateMsg;
    }

    public void setAdultsMsg(String adultsMsg) {
        this.adultsMsg = adultsMsg;
    }

    public void setToDateMsg(String toDateMsg) {
        this.toDateMsg = toDateMsg;
    }

    public void setChildrenMsg(String childrenMsg) {
        this.childrenMsg = childrenMsg;
    }

    public void setInfantsMsg(String infantsMsg) {
        this.infantsMsg = infantsMsg;
    }

    public void setClassMsg(String classMsg) {
        this.classMsg = classMsg;
    }

    public void setDirectOnlyMsg(String directOnlyMsg) {
        this.directOnlyMsg = directOnlyMsg;
    }
}

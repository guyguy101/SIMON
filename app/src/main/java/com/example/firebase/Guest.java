package com.example.firebase;

import java.util.Objects;

public class Guest {

    protected String fname;
    protected String lname;
    public Guest(){

    }
    public Guest(String fname,String lname){
        this.lname= lname;
        this.fname=fname;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(fname, guest.fname) && Objects.equals(lname, guest.lname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, lname);
    }
}

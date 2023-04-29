package com.example.firebase;

import java.util.Objects;

public class Guest {

    protected String nickname;
    public Guest(){

    }
    public Guest(String name){

        this.nickname = name;
    }

    public String getNickname(){return this.nickname;}

    public void setNickname(String nickname) {this.nickname = nickname;}

    @Override
    public String toString() {
        return "Guest{" +
                "Nickname='" + nickname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(nickname, guest.nickname) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}

package com.example.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class User extends Guest{

        private String uid;//user uid
        private String email;
        private Integer maxScore;
        private final Date dateJoined;
        private Date lastDatePlayed;

        //region Constructors
        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
            dateJoined = new Date();
        }

        public User(String email,String name,String uid)
        {
            super(name);



            this.dateJoined = new Date();
            this.lastDatePlayed = new Date();
            this.uid = uid;
            this.email = email;
            this.maxScore = 0;

        }
        //endregion
        //region Getters
         public String getUid(){
            return this.uid;
         }

         public String getEmail(){
            return this.email;
         }

         public Integer getMaxScore(){
            return this.maxScore;
         }

         public Date getDateJoined() { return this.dateJoined; }

         public Date getLastDatePlayed() { return this.lastDatePlayed; }
  //endregion
        //region Setters

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setLastDatePlayed(Date lastDatePlayed) {this.lastDatePlayed = lastDatePlayed;}

        public void setMaxScore(Integer maxScore) {this.maxScore = maxScore;}

        //NOTE 4 SETTERS BECAUSE DATEJOINED FIELD IS FINAL
    //endregion
        //region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return maxScore == user.maxScore && Objects.equals(uid, user.uid)  && Objects.equals(email, user.email) && Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, email, nickname, maxScore);
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", last name='" + nickname + '\'' +
                ", points=" + maxScore +
                '}';
    }
    //endregion











}
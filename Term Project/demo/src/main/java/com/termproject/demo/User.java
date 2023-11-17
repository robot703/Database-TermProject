package com.termproject.demo;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="member")
public class User {

    @Id
   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userid;
    private String passwd;
    private String name;
    private String email;
    private LocalDate join_date;

    public String getPassword() {
        return passwd;
    }
    public void setPassword(String password) {
        this.passwd = password;
    }
    public String getUserid() {
        return userid;
    }

    // Getter, Setter, 생성자 등 추가
}

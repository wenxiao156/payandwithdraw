package com.cmit.payandwithdraw.domain;

public class Token {
    private String token;


    public Token(String salerId, String timestamp) {
        this.token = generateToken(salerId, timestamp);
    }

    public String generateToken(String salerId, String timestamp) {
        return  salerId + timestamp;
    }

    public String getToken() {
        return this.token;
    }
}

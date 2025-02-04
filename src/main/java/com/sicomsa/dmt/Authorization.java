/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import java.time.Instant;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * 
 * @since 2024.9.9
 * 
 * Contains the token provided from SAT to access massive download web services.
 * Includes instant the authorization was received, created and its expiration.
 *
 */
public class Authorization implements java.io.Serializable { 
    private static final long serialVersionUID = 20241118L;
    
    protected Instant instant;
    protected Instant created;
    protected Instant expires;
    protected String token;
  
    /**
     * 
     * @param satInstant
     * @param created
     * @param expires
     * @param token 
     * @throws IllegalArgumentException if any argument is null or if
     *          token is blank or expires instant is not after created instant
     */
    public Authorization(Instant satInstant, Instant created, Instant expires, String token) {
        if (satInstant == null) {
            throw new IllegalArgumentException("satinstant is required");
        }
        if (!isConsistent(token)) {
            throw new IllegalArgumentException("can not have authorization without token");
        }
        if (!isConsistent(created, expires)) {
            throw new IllegalArgumentException("invalid token instant range");
        }
        this.instant = satInstant;
        this.created = created;
        this.expires = expires;
        this.token = token;
    }
    
    public static boolean isConsistent(String token) {
        return (token != null && !token.isBlank());
    }
    
    public static boolean isConsistent(Instant created, Instant expires) {
        return (created != null && expires != null && expires.isAfter(created));
    }
    
    public Instant getInstant() {
        return instant;
    }
    
    public Instant getCreated() {
        return created;
    }
    
    public Instant getExpires() {
        return expires;
    }
    
    public String getToken() {
        return token;
    }
  
    @Override public String toString() {
        return new StringBuilder("Authorization:{")
                .append("satInstant=").append(instant)
                .append(",created=").append(created)
                .append(",expires=").append(expires)
                .append(",token=").append(token)
                .append("}")
                .toString();
    }
    
    public static String wrapp(String token) {
        return new StringBuilder("WRAP access_token=\"")
            .append(token)
            .append("\"")
            .toString();
    }
   
}

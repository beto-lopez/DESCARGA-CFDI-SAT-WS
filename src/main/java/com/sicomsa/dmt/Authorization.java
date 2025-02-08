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
 * @version     2024.9.9
 * @since       1.0
 * 
 * Contains the token provided from SAT to access massive download web services.
 * Includes instant the authorization was received, created and its expiration.
 *
 */
public class Authorization implements java.io.Serializable { 
    private static final long serialVersionUID = 20241118L;
    
    /**
     * instant when message was received
     */
    protected Instant instant;
    /**
     * instant when token was created
     */
    protected Instant created;
    /**
     * instant when token will expire
     */
    protected Instant expires;
    /**
     * token as it was received
     */
    protected String token;
  
    /**
     * builds an authorization with the received parameters
     * 
     * @param satInstant instant message was received
     * @param created instant token was created
     * @param expires intant token exprires
     * @param token  received token
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
    
    /**
     * @param token
     * @return true if token is not null and not blank
     */
    public static boolean isConsistent(String token) {
        return (token != null && !token.isBlank());
    }
    
    /**
     * 
     * @param created instant created
     * @param expires instant expires
     * @return  true if created and expires are not null and expires is after created
     */
    public static boolean isConsistent(Instant created, Instant expires) {
        return (created != null && expires != null && expires.isAfter(created));
    }
    
    /**
     * 
     * @return instant message was received
     */
    public Instant getInstant() {
        return instant;
    }
    
    /**
     * 
     * @return instant token was created
     */
    public Instant getCreated() {
        return created;
    }
    
    /**
     * 
     * @return instant token will expire
     */
    public Instant getExpires() {
        return expires;
    }
    
    /**
     * 
     * @return token received
     */
    public String getToken() {
        return token;
    }
  
    /**
     * 
     * @return string representation of <code>Authorization</code>
     */
    @Override public String toString() {
        return new StringBuilder("Authorization:{")
                .append("satInstant=").append(instant)
                .append(",created=").append(created)
                .append(",expires=").append(expires)
                .append(",token=").append(token)
                .append("}")
                .toString();
    }
    
    /**
     * 
     * @param token to be wrapped
     * @return the token wrapped as needed to be sent in requests
     */
    public static String wrapp(String token) {
        return new StringBuilder("WRAP access_token=\"")
            .append(token)
            .append("\"")
            .toString();
    }
   
}

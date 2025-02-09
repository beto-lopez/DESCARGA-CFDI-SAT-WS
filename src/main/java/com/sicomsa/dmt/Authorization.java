/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import java.time.Instant;

/**
 * <code>Authorization</code> contains the token provided from SAT to access
 * massive download web services. Includes instant the authorization was received,
 * created and its expiration.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2024.9.9
 * @since       1.0
 * 
 */
public class Authorization implements java.io.Serializable { 
    private static final long serialVersionUID = 20241118L;
    
    /**
     * Instant when message was received
     */
    protected Instant instant;
    /**
     * Instant when token was created
     */
    protected Instant created;
    /**
     * Instant when token will expire
     */
    protected Instant expires;
    /**
     * Token as it was received
     */
    protected String token;
  
    /**
     * Returns an authorization with the received parameters
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
     * Returns true if token is not null and not blank
     * 
     * @param token the token to be tested
     * @return true if token is not null and not blank
     */
    public static boolean isConsistent(String token) {
        return (token != null && !token.isBlank());
    }
    
    /**
     * Returns true if created and expires instants are not null and expires is
     * after created
     * 
     * @param created instant created
     * @param expires instant expires
     * @return  true if created and expires are not null and expires is after created
     */
    public static boolean isConsistent(Instant created, Instant expires) {
        return (created != null && expires != null && expires.isAfter(created));
    }
    
    /**
     * Returns the instant the message was received from SAT 
     * 
     * @return instant message was received
     */
    public Instant getInstant() {
        return instant;
    }
    
    /**
     * Returns the instant the token was created
     * 
     * @return instant token was created
     */
    public Instant getCreated() {
        return created;
    }
    
    /**
     * Returns the instant when the token will expire
     * 
     * @return instant token will expire
     */
    public Instant getExpires() {
        return expires;
    }
    
    /**
     * Returns the token as it was received from SAT
     * 
     * @return token received
     */
    public String getToken() {
        return token;
    }
  
    /**
     * Returns a string representation of this <code>Authorization</code>
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
     * Returns the token wrapped as needed to be sent to SAT in a request
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

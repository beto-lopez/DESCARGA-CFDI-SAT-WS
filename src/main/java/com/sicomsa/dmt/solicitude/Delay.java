/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.06
 * 
 * A Delay means that SAT has already authorized the request but has not yet
 * finished verifying it.
 */
public enum Delay {
    ACCEPTED, IN_PROGRESS, OTHER;
}

/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;
import java.util.Arrays;

/**
 * Implementation of <code>DownloadRegistry</code>.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.27
 * @since 1.0
 *  
 */
public class DownloadRegistryImpl implements java.io.Serializable, DownloadRegistry {

    private static final long serialVersionUID = 20241027L;
    
    /**
     * wrapper of the package names to download
     */
    protected final PackageIds ids;
    
    /**
     * boolean array. True when package at index has been downloaded.
     */
    protected final boolean[] flags;
    
    /**
     * Constructs a <code>DownloadRegistryImpl</code> with the specified
     * <code>PackageIds</code> whose enclosed packages have not been downloaded.
     * 
     * @param ids the wrapper that contains the package identifiers to download.
     */
    public DownloadRegistryImpl(PackageIds ids) {
        this(ids, null);
    }
    
    /**
     * Constructs a <code>DownloadRegistryImpl</code> with the specified
     * <code>PackageIds</code> whose enclosed packages' download states are stated
     * in the specified flags array.
     * <p>Note: the boolean array will be cloned.</p>
     * 
     * @param ids the wrapper that contains the package identifiers to download.
     * @param flags array containing ids' download state
     */
    public DownloadRegistryImpl(PackageIds ids, boolean[] flags) {
        if (ids == null) {
            throw new IllegalArgumentException("packageIds is required");
        }
        if (flags == null) {
            this.flags = new boolean[ids.size()];
        }
        else if (flags.length == ids.size()) {
            this.flags = flags.clone();
        }
        else {
            throw new IllegalArgumentException("inconsistent parameter sizes");
        }
        this.ids = ids;
    }

    /**
     * Returns the ammount of identifiers this registry contains.
     * 
     * @return the ammount of identifiers this registry contains
     */
    public int size() {
        return ids.size();
    }
    
    /**
     * Returns the position of the specified package identifier or -1 if it does
     * not contain one. This is a case-sensitive method.
     * 
     * @param packageId the package identifier to search
     * @return the index of the specified package identifier or -1 if it does
     *         not contain one
     */
    public int indexOf(String packageId) {
        return ids.indexOf(packageId);
    }

    /**
     * Returns the package identifier at the specified position.
     * 
     * @param index index of identifier to return
     * @return the package identifier at the specified position
     */
    public String getPackageId(int index) {
        return ids.getPackageId(index);
    }
    
    /**
     * Returns a string representation of this registry implementation.
     * 
     * @return a string representation of this registry implementation
     */
    @Override public String toString() {
        return new StringBuilder("DownloadRegistryImpl{")
                .append("ids:")
                .append(ids)
                .append(",downloaded:")
                .append(Arrays.toString(flags))
                .append("}").toString();
    }
    ////////////////////////////////////////////////////////////////////////////
    //// DownloadRegistry implementation
    
    @Override public PackageIds getPackageIds() {
        return ids;
    }
    
    @Override public boolean isDownloaded(int index) {
        return (index >= 0
                && index < flags.length
                && flags[index]);
    }
    
    @Override public boolean isDownloadDone() {
        for (boolean done : flags) {
            if (!done) {
                return false;
            }
        }
        return true;
    }
    
    @Override public String getNextDownloadablePackageId() {
        for (int idx = 0; idx < flags.length; idx++) {
            if (!flags[idx]) {
                return getPackageId(idx);
            }
        }
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////


}

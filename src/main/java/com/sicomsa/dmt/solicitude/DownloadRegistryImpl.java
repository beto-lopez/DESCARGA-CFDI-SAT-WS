/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;
import java.util.Arrays;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 2024.10.27
 * 
 * Implementation of DownloadRegistry
 * 
 */
public class DownloadRegistryImpl implements java.io.Serializable, DownloadRegistry {

    private static final long serialVersionUID = 20241027L;
    
    protected final PackageIds ids;
    protected final boolean[] flags;
    
    /**
     * 
     * @param ids
     */
    public DownloadRegistryImpl(PackageIds ids) {
        this(ids, null);
    }
    
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
    
    @Override public PackageIds getPackageIds() {
        return ids;
    }
    
    @Override public boolean isDownloaded(int index) {
        return (index >= 0
                && index < flags.length
                && flags[index]);
    }

    public int size() {
        return ids.size();
    }
    
    public int indexOf(String packageId) {
        return ids.indexOf(packageId);
    }

    public String getPackageId(int index) {
        return ids.getPackageId(index);
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

    @Override public String toString() {
        return new StringBuilder("DownloadRegistryImpl{")
                .append("ids:")
                .append(ids)
                .append(",downloaded:")
                .append(Arrays.toString(flags))
                .append("}").toString();
    }

}

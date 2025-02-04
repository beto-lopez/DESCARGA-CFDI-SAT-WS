/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.util.List;
import java.util.Arrays;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 2024.10.27
 * 
 * 
 * Class containing the names of the packages to be downloaded from SAT.
 * This wrapper is used to move ids through different classes without having to
 * recreate or mutate them, since they should not change.
 * 
 */
public class PackageIds implements java.io.Serializable {

    private static final long serialVersionUID = 20241027L;
    
    private final String[] idsArray;
    
    /**
     * 
     * @param idList
     */
    public PackageIds(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            throw new IllegalArgumentException("non empty list is required");
        }
        this.idsArray = idList.toArray(String[]::new);
    }
     
    public int size() {
        return idsArray.length;
    }
    
    public int indexOf(String packageId) {
        if (packageId != null) {
            for (int idx = 0; idx < idsArray.length; idx++) {
                if (packageId.equals(idsArray[idx])) {
                    return idx;
                }
            }
        }
        return -1;
    }

    public String getPackageId(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index:"+index+" Size:"+size());
        }
        return idsArray[index];
    }

    @Override public String toString() {
        return new StringBuilder("PackageIds{")
                .append(Arrays.toString(idsArray))
                .append("}").toString();
    }
    
    
}

/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.util.List;
import java.util.Arrays;

/**
 * This class containing the names or ids of the packages to be downloaded from SAT.
 * This wrapper is used to move ids through different classes without having to
 * recreate or mutate them, since they should not change.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.27
 * @since 1.0 
 * 
 */
public class PackageIds implements java.io.Serializable {

    private static final long serialVersionUID = 20241027L;
    
    /**
     * Array of ids
     */
    private final String[] idsArray;
    
    /**
     * Builds a <code>PackageIds</code> using the list of ids received.<p>
     * It will use the List only to create it's own private array of ids.
     * 
     * @param idList List of package Id's to download.
     * @throws IllegalArgumentException if <code>idList</code> is null or empty
     */
    public PackageIds(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            throw new IllegalArgumentException("non empty list is required");
        }
        this.idsArray = idList.toArray(String[]::new);
    }
    
    /**
     * Returns the number of package names or ids it contains.
     * 
     * @return the number of package names or ids it contains.
     */
    public int size() {
        return idsArray.length;
    }
    
    /**
     * Returns the index of the first occurrence of the specified <code>packageId</code>
     * in this class, or -1 if this list does not contain the id.
     * This method is case-sensitive.
     * 
     * @param packageId String to search for
     * @return the index of the first occurrence of the specified <code>packageId</code>
     *         in this class, or -1 if this list does not contain the id. Case-sensitive.
     */
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

    /**
     * Returns the id at the specified position.
     * 
     * @param index index of the id to return
     * @return the id at the specified position
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public String getPackageId(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index:"+index+" Size:"+size());
        }
        return idsArray[index];
    }

    /**
     * Returns a string representation of this instance.
     * 
     * @return a string representation of this instance
     */
    @Override public String toString() {
        return new StringBuilder("PackageIds{")
                .append(Arrays.toString(idsArray))
                .append("}").toString();
    }
    
    
}

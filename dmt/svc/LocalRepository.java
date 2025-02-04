/*

 */
package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.DownloadRepository;
import com.sicomsa.dmt.RepositoryException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import java.lang.System.Logger.Level;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.12
 * 
 * Download repository implementation that saves downloads in a specified directory.
 * 
 * The directory can be defined when instantiating this class or it can be set
 * after. Order in which the class defines the directory:
 * ** It is important to consider that the specified directory must exist.
 *
 * 1) Use the directory specified in the constructor, if defined.
 * 2) Use the one from the System.getProperty(DOWNLOAD_PATH_PROPERTY) property
 * 3) Use the one returned by System.getProperty("user.home")
 *      In this case, the user downloads directory will be searched,
 *      if it is not found, "user.home" will be used.
 * 
 *  
 */
public class LocalRepository implements DownloadRepository {
    
    public static final String DOWNLOAD_PATH_PROPERTY = "com.sicomsa.dmt.svc.LocalRepository.path";
    
    private static final System.Logger LOG = System.getLogger(LocalRepository.class.getName());
    
    protected Base64.Decoder decoder;
    protected File downloadsDir;
    
    public LocalRepository() {
        this(null);
    }
    
    public LocalRepository(File downloadsDir) {
        this.downloadsDir = downloadsDir;
    }
    
    public synchronized File getDownloadDirectory() {
        if (downloadsDir == null) {
            downloadsDir = defineDirectory();
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Download directory ({0})", downloadsDir.getPath());
            }
        }
        return downloadsDir;
    }
    
    public synchronized void setDownloadDirectory(File dir) {
        this.downloadsDir = dir;
    }
    
    /**
     * 
     * @param rfc
     * @param packageId
     * @param encodedPackage
     * @param params
     * @throws RepositoryException 
     * @throws IllegalArgumentException if rfc or packageId are null or blank
     * @throws IllegalArgumentException - if encodedPackage is not in valid Base64 scheme
     * @throws NullPointerException - if encodedPackage is null
     */
    @Override
    public void save(String rfc, String packageId, String encodedPackage, Object params) throws RepositoryException {
        LOG.log(Level.DEBUG, "Saving package ({0}) from ({1})", packageId, rfc);
        try {
            save(getFile(rfc, packageId), decode(encodedPackage));
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, e.getMessage(), e);
            throw new RepositoryException(e.getMessage(), e);
        }
    }
    
    ////////////////////////////////////////////////////////////////////
    
    protected void save(File file, byte[] data) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
            out.flush();
        }
    }
    
    /**
     * 
     * @param rfc
     * @param packageId
     * @return 
     * @throws IllegalArgumentException if rfc or packageId are null or blank
     */
    protected File getFile(String rfc, String packageId) {
        return new File(getDownloadDirectory(), getZipFileName(rfc, packageId));
    }
    
   
    /**
     * 
     * @param encoded
     * @return
     * @throws IllegalArgumentException - if src is not in valid Base64 scheme
     * @throws NullPointerException - if encoded is null
     */
    protected byte[] decode(String encoded) {
        if (decoder == null) {
            decoder = Base64.getDecoder();
        }
        return decoder.decode(encoded);
    }
    
    /**
     * 
     * @param rfc
     * @param packageId
     * @return 
     * @throws IllegalArgumentException if rfc or packageId are null or blank
     */
    protected String getZipFileName(String rfc, String packageId) {
        if (rfc == null || packageId == null || rfc.isBlank() || packageId.isBlank()) {
            throw new IllegalArgumentException("rfc nor packageId can be null in order to produce a valid file name");
        }
        return new StringBuilder(rfc)
                .append(".")
                .append(packageId)
                .append(".zip")
                .toString();
    }
    
    protected File defineDirectory() {
        String path = System.getProperty(DOWNLOAD_PATH_PROPERTY);
        if (path != null && !path.isBlank()) {
            return new File(path);
        }
        String userHome = System.getProperty("user.home");
        File file = findDownloadsDirectory(userHome);
        return (file == null ? new File(userHome) : file);
    }
    
    protected String[] downloadsDirNames() {
        return new String[] {"Downloads", "Download", "Descargas"};
    }
    
    protected File findDownloadsDirectory(String userHome) {
        for (String dir : downloadsDirNames()) {
            File file = new File(userHome, dir);
            if (isDirectory(file)) {
                return file;
            }
        }
        return null;
    }
    
    public static boolean isDirectory(File file) {
        return (file != null
                && file.exists()
                && file.isDirectory());
    }

}

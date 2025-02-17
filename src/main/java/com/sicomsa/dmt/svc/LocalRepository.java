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
 * Download repository implementation that saves downloads in a specified directory.
 * <p>The directory can be defined when instantiating this class or it can be set
 * afterwards. Order in which the class defines the directory:</p>
 * <ol>
 * <li>The <code>downloadDir</code> property set either by the constructor
 *     or by the {@link LocalRepository#setDownloadDirectory(java.io.File)} method.</li>
 * <li>A non empty path provided by the method {@link java.lang.System#getProperty(java.lang.String) }
 *     using the property name {@link LocalRepository#DOWNLOAD_PATH_PROPERTY}.</li>
 * <li>A download directory found under the user home directory provided by the JVM</li>
 * </ol>
 * <p>Note: download directory must exist</p>
 * <p>You can know what directory this repository will use with the method:
 * {@link LocalRepository#getDownloadDirectory()}</p>
 * <p>It is suggested that you test the download directory before making actual
 * downloads. With something like this:</p>
 * <pre>
 * LocalRepository repository = new LocalRepository();
 * System.out.println("downloadDirectory="+repository.getDownloadDirectory());
 * File file = new File(repository.getDownloadDirectory(), "test.txt");
 * byte[] data = new byte[10];
 * try (FileOutputStream out = new FileOutputStream(file)) {
 *      out.write(data);
        out.flush();
   }
 * </pre>
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.12
 * @since 1.0
 * 
 *  
 */
public class LocalRepository implements DownloadRepository {
    
    /**
     * Name of property used to define download directory
     */
    public static final String DOWNLOAD_PATH_PROPERTY = "com.sicomsa.dmt.svc.LocalRepository.path";
    
    private static final System.Logger LOG = System.getLogger(LocalRepository.class.getName());
    
    /**
     * Decoder tho decode encoded packages
     */
    protected Base64.Decoder decoder;
    
    /**
     * Current download directory
     */
    protected File downloadsDir;
    
    /**
     * Constructs a new LocalRepository.
     */
    public LocalRepository() {
        this(null);
    }
    
    /**
     * Constructs a new LocalRepository that will save packages under the
     * specified directory.
     * 
     * @param downloadsDir directory where package files will be saved
     */
    public LocalRepository(File downloadsDir) {
        this.downloadsDir = downloadsDir;
    }
    
    /**
     * Returns the directory this repository will use to store packages.
     * 
     * @return the directory this repository will use to store packages
     */
    public synchronized File getDownloadDirectory() {
        if (downloadsDir == null) {
            downloadsDir = defineDirectory();
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Download directory ({0})", downloadsDir.getPath());
            }
        }
        return downloadsDir;
    }
    
    /**
     * Sets the download directory to the specified File
     * 
     * @param dir the download directory
     */
    public synchronized void setDownloadDirectory(File dir) {
        this.downloadsDir = dir;
    }
    
    /**
     * Saves the specified <code>encodedPackage</code> in the defined directory
     * of this repository.
     * 
     * @param rfc RFC of download requestor
     * @param packageId package identifier of this downloaded package
     * @param encodedPackage the downloaded package
     * @param params alternative parameter
     * @throws RepositoryException if unable to save package in repository
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
    
    /**
     * Saves the specified data in the specified File.
     * 
     * @param file the file where data will be written to
     * @param data the data to save
     * @throws IOException if there were IO problems
     */
    protected void save(File file, byte[] data) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
            out.flush();
        }
    }
    
    /**
     * Returns a {@link java.io.File} for storing a package with the specified
     * parameters.
     * 
     * @param rfc RFC of the requesting package
     * @param packageId packageId of the downloaded package
     * @return a {@link java.io.File} for storing a package with the specified
     *         parameters
     * @throws IllegalArgumentException if rfc or packageId are null or blank
     */
    protected File getFile(String rfc, String packageId) {
        return new File(getDownloadDirectory(), getZipFileName(rfc, packageId));
    }
    
   
    /**
     * Decodes a Base64 encoded String into a newly-allocated byte array using
     * a base64 decoder.
     * 
     * @param encoded the string to decode
     * @return a byte array containing the specified string base64 decoded.
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
     * Returns the name of the file given the specified parameters.
     * 
     * @param rfc RFC of requesting contributor
     * @param packageId packageId that was downloaded
     * @return the name of the file given the specified parameters
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
    
    /**
     * Returns the {@link java.io.File} directory where the downloads will be
     * saved.
     * <p>If the method <code>System.getProperty(DOWNLOAD_PATH_PROPERTY)</code>
     * returns a non blank path, this path will be used. If it is not set then
     * a download directory will be searched under the user home directory
     * provided by the JVM.</p>
     * 
     * @return the {@link java.io.File} directory where the downloads will be saved
     */
    protected File defineDirectory() {
        String path = System.getProperty(DOWNLOAD_PATH_PROPERTY);
        if (path != null && !path.isBlank()) {
            return new File(path);
        }
        String userHome = System.getProperty("user.home");
        File file = findDownloadsDirectory(userHome);
        return (file == null ? new File(userHome) : file);
    }
    
    /**
     * Returns a string array of posible download directory file names.
     * 
     * @return a string array of posible download directory file names
     */
    protected String[] downloadsDirNames() {
        return new String[] {"Downloads", "Download", "Descargas"};
    }
    
    /**
     * Returns a {@link java.io.File File} from the ones specified in the method
     * {@link LocalRepository#downloadsDirNames() downloadDirNames()},
     * below the path defined by <code>userHome</code> or null if no existing
     * directory was found.
     * 
     * @param userHome the path to find a directory from
     * @return a {@link java.io.File File} from the ones specified in the method
     *         {@link LocalRepository#downloadsDirNames() downloadDirNames()},
     *         below the path defined by <code>userHome</code> or null if no
     *         existing directory was found
     */
    protected File findDownloadsDirectory(String userHome) {
        for (String dir : downloadsDirNames()) {
            File file = new File(userHome, dir);
            if (isDirectory(file)) {
                return file;
            }
        }
        return null;
    }
    
    /**
     * Returns true if the specified file is an existing directory.
     * 
     * @param file the file to evaluate
     * @return true if the specified file is an existing directory
     */
    public static boolean isDirectory(File file) {
        return (file != null
                && file.exists()
                && file.isDirectory());
    }

}

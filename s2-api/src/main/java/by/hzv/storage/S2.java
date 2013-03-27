package by.hzv.storage;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface S2 {
    /**
     * Creates new file with specified path. Overrides content of the existing file with the same name.
     *
     * @param path - file path within the storage
     * @param content - file content
     * @param properties - additional properties that should be associated with the file
     * @return the File ID (FLID) of the parent folder
     */
    String createFile(String path, ContentStream content, Map<String, ?> properties);

    /**
     * Deletes file with specified path
     *
     * @param path - path to file that should be deleted
     */
    void deleteFile(String path);

    /**
     * Recursively deletes all child folder and files for the folder specified by Folder ID
     *
     * @param fid - Folder ID
     */
    void deleteFolder(String fid);

    /**
     * Returns collection of FileInfo for all files within specified folder and its subfolders.
     *
     * @param fid - Folder ID
     * @return collection of FileInfo for all files within specified folder and its subfolders
     */
    Collection<FileInfo> listFiles(String fid);

    /**
     * Returns FID for given folder path if exists
     *
     * @param folderPath - path to folder
     * @return - FID or null
     */
    String getFid(String folderPath);

    /**
     * Returns folder path for given FID
     *
     * @param fid - Folder ID
     * @return folder path or null
     */
    String getFolderPath(String fid);

    /**
     * Returns ContentStream for file specified by path
     *
     * @param path - path to file
     * @return - ContentStream or null
     */
    ContentStream getContentStream(String path);
}

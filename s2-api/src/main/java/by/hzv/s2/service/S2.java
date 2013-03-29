package by.hzv.s2.service;

import java.util.Collection;
import java.util.Map;

import by.hzv.s2.model.ContentStream;
import by.hzv.s2.model.FileInfo;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface S2 {
    /**
     * Creates new file with specified logical path. Overrides content of the existing file with the same name.
     *
     * @param filePath - logical file path
     * @param content - file content
     * @param properties - additional properties that should be associated with the file
     * @return the File ID (FID)
     */
    String createFile(String filePath, ContentStream content, Map<String, ?> properties);

    /**
     * Deletes file with specified logical path
     *
     * @param fid - File ID
     */
    void deleteFile(String fid);

    /**
     * Deletes file with specified logical path
     *
     * @param filePath - logical file path
     */
    void deleteFileByPath(String filePath);

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
     * Returns ContentStream for file associated with fid
     *
     * @param fid - File ID
     * @return - ContentStream or null
     */
    ContentStream getContentStream(String fid);


    /**
     * Returns ContentStream for file associated with logical path
     *
     * @param path - logical file path
     * @return - ContentStream or null
     */
    ContentStream getContentStreamByPath(String filePath);

    /**
     * Returns FID for logical folder or file path
     *
     * @param path - logical folder or file path
     * @return - FID or null
     */
    String getFid(String path);

    /**
     * Returns logical path for given FID
     *
     * @param fid - Folder or File ID
     * @return logical path or null
     */
    String getPath(String fid);
}

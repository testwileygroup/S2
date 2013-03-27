package by.hzv.storage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.hzv.storage.util.KeyBasedLocker;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
@Service
public class SimpleStorage implements S2 {
    @Autowired
    private PathInfoController pathInfoController;
    @Autowired
    private DataStoreController dataStoreController;
    @Autowired
    private KeyBasedLocker locker;


    /* (non-Javadoc)
     * @see by.hzv.storage.S2#createFile(java.lang.String, by.hzv.storage.ContentStream, java.util.Map)
     */
    @Override
    public String createFile(String path, ContentStream content, Map<String, ?> properties) {
        String normalizedPath = normalizePath(path);

        FolderInfo finfo = pathInfoController.getOrCreateFolderInfo(FilenameUtils.getFullPath(normalizedPath));

        Lock lock = locker.getLock(normalizedPath);
        lock.lock();
        try {
            pathInfoController.deleteFileInfo(normalizedPath);
            FileInfo flinfo = pathInfoController.createFileInfo(normalizedPath, finfo.getFid(), properties);
            dataStoreController.storeContentStream(flinfo.getFlid(), content);
        } finally {
            lock.unlock();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#deleteFile(java.lang.String)
     */
    @Override
    public void deleteFile(String path) {
        String normalizedPath = normalizePath(path);
        pathInfoController.deleteFileInfo(normalizedPath);
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#deleteFolder(java.lang.String)
     */
    @Override
    public void deleteFolder(String fid) {
        pathInfoController.deleteFolderInfo(fid);
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#listFiles(java.lang.String)
     */
    @Override
    public Collection<FileInfo> listFiles(String fid) {
        return pathInfoController.listFileInfo(fid);
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#getFid(java.lang.String)
     */
    @Override
    public String getFid(String folderPath) {
        String normalizedPath = normalizePath(folderPath);
        return pathInfoController.getFid(normalizedPath);
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#getFolderPath(java.lang.String)
     */
    @Override
    public String getFolderPath(String fid) {
        return pathInfoController.getFolderPath(fid);
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.S2#getContentStream(java.lang.String)
     */
    @Override
    public ContentStream getContentStream(String path) {
        String normalizedPath = normalizePath(path);
        return dataStoreController.getContentStream(normalizedPath);
    }

    String normalizePath(String path) {
        return FilenameUtils.normalize(path, true);
    }
}

package by.hzv.storage;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.hzv.storage.dao.PathInfoRepository;
import by.hzv.storage.entity.FileInfoEntity;
import by.hzv.storage.entity.FolderInfoEntity;
import by.hzv.storage.util.KeyBasedLocker;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
@Component
public class PathInfoController {
    @Autowired
    private PathInfoRepository repository;
    @Autowired
    private KeyBasedLocker loker;


    void deleteFolderInfo(String fid) {
        repository.deleteFolderInfo(fid);
    }

    void deleteFileInfo(String path) {
        repository.deleteFileInfo(path);
    }

    Collection<FileInfo> listFileInfo(String fid) {
        return repository.findFileInfoByFid(fid);
    }

    String getFid(String folderPath) {
        return repository.findFid(folderPath);
    }

    String getFolderPath(String fid) {
        return repository.findFolderPath(fid);
    }

    FolderInfo getOrCreateFolderInfo(String folderPath) {
        FolderInfoEntity finfo = repository.findFolderInfo(folderPath);

        /* performance optimization for case when several file from the same folder uploaded concurrently
         * in this case we will block folder info creation for the first request
         * and other requests should not be blocked */
        if (finfo == null) {
            Lock lock = loker.getLock(folderPath);
            lock.lock();
            try {
                finfo = repository.findFolderInfo(folderPath);
                finfo = (finfo != null) ? finfo : new FolderInfoEntity(folderPath);
                repository.save(finfo);
            } finally {
                lock.unlock();
            }
        }

        return finfo;
    }

    public FileInfo createFileInfo(String path, String fid, Map<String, ?> properties) {
        String filename = FilenameUtils.getBaseName(path);
        String extension = FilenameUtils.getExtension(path);
        BigInteger size = (BigInteger) properties.get(FilePropertyNames.CONTENT_SIZE.name());

        FileInfoEntity entity = new FileInfoEntity(fid, filename, extension, size);
        repository.save(entity);

        return entity;
    }

}

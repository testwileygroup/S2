package by.hzv.storage.dao;

import java.util.Collection;

import by.hzv.storage.FileInfo;
import by.hzv.storage.entity.Entity;
import by.hzv.storage.entity.FolderInfoEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface PathInfoRepository {

    void deleteFolderInfo(String fid);

    void deleteFileInfo(String path);

    Collection<FileInfo> findFileInfoByFid(String fid);

    String findFid(String folderPath);

    String findFolderPath(String fid);

    void save(Entity entity);

    FolderInfoEntity findFolderInfo(String folderPath);

}

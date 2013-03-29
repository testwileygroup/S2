package by.hzv.s2.dao;

import java.util.Collection;

import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.entity.Entity;
import by.hzv.s2.model.entity.FolderInfoEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface PathInfoRepository {

    void deleteFolderInfo(String fid);

    void deleteFileInfo(String flid);

    void deleteFileInfoByPath(String path);

    Collection<FileInfo> findFileInfo(String fid);

    String findFid(String folderPath);

    String findPath(String fid);

    void save(Entity entity);

    FolderInfoEntity findFolderInfo(String folderPath);

}

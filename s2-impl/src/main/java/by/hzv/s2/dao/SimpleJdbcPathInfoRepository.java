package by.hzv.s2.dao;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.entity.Entity;
import by.hzv.s2.model.entity.FolderInfoEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 28.03.2013
 */
@Repository
public class SimpleJdbcPathInfoRepository implements PathInfoRepository {

    @Override
    public void deleteFolderInfo(String fid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteFileInfo(String flid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteFileInfoByPath(String path) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<FileInfo> findFileInfo(String fid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String findFid(String folderPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String findPath(String fid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(Entity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public FolderInfoEntity findFolderInfo(String folderPath) {
        // TODO Auto-generated method stub
        return null;
    }

}

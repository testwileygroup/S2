package by.hzv.s2.model.entity;

import by.hzv.s2.model.FolderInfo;
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 27.03.2013
 */
@EqualsAndHashCode
@ToString
class FolderInfoEntity extends Entity implements FolderInfo {
    String path

    FolderInfoEntity(String folderPath) {
        this.path = folderPath
    }

    /* (non-Javadoc)
     * @see by.hzv.storage.FolderInfo#getFid()
     */
    @Override
    String getFid() {
        return String.valueOf(getId())
    }
}

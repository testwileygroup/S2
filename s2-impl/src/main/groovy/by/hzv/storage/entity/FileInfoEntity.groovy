package by.hzv.storage.entity;


import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;
import groovy.transform.TupleConstructor;
import by.hzv.storage.FileInfo

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 27.03.2013
 */

@EqualsAndHashCode
@ToString
class FileInfoEntity extends Entity implements FileInfo {
    String fid
    String filename
    String extension
    BigInteger size

    FileInfoEntity(String fid, String filename, String extension, BigInteger size) {
        this.fid = fid
        this.filename = filename
        this.extension = extension
        this.size = size
    }


    @Override
    public String getFlid() {
        return fid + ":" + getId()
    }
}

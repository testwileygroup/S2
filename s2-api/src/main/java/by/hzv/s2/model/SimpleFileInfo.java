package by.hzv.s2.model;

import java.math.BigInteger;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 02.04.2013
 */
public class SimpleFileInfo implements FileInfo {
    public static final int HASH_SEED = 17;
    public static final int HASH_OFFSET = 37;

    private String flid;
    private String filename;
    private String extension;
    private BigInteger size;

    public SimpleFileInfo() {}

    public SimpleFileInfo(String flid, String filename, String extension, BigInteger size) {
        this.flid = flid;
        this.filename = filename;
        this.extension = extension;
        this.size = size;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.FileInfo#getFlid()
     */
    @Override
    public String getFlid() {
        return flid;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.FileInfo#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.FileInfo#getExtension()
     */
    @Override
    public String getExtension() {
        return extension;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.FileInfo#getSize()
     */
    @Override
    public BigInteger getSize() {
        return size;
    }

    public void setFlid(String flid) {
        this.flid = flid;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    @Override
    public int hashCode(){
        int hash = HASH_SEED * HASH_OFFSET + flid.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof FileInfo)) {
            return false;
        }

        FileInfo fInfo = (FileInfo) o;
        return fInfo.getFlid().equals(getFlid());
    }
}

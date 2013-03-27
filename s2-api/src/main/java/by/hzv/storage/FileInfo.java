package by.hzv.storage;

import java.math.BigInteger;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface FileInfo {
    /**
     * Returns File ID within S2 storage
     *
     * @return File ID
     */
    String getFlid();
    String getFilename();
    String getExtension();
    BigInteger getSize();
}

package by.hzv.s2.model;

import java.io.InputStream;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface ContentStream {
    InputStream getStream();
    boolean isProxy();
    String getUrl();
}

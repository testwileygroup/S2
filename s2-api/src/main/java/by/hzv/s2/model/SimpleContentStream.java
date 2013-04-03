package by.hzv.s2.model;

import java.io.InputStream;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 01.04.2013
 */
public class SimpleContentStream implements ContentStream {
    private final InputStream inputStream;
    private boolean proxy;
    private String url;

    public SimpleContentStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public SimpleContentStream(String url) {
        this.proxy = true;
        this.url = url;
        this.inputStream = null;
    }


    /* (non-Javadoc)
     * @see by.hzv.s2.model.ContentStream#getStream()
     */
    @Override
    public InputStream getStream() {
        return inputStream;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.ContentStream#isProxy()
     */
    @Override
    public boolean isProxy() {
        return proxy;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.model.ContentStream#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }
}

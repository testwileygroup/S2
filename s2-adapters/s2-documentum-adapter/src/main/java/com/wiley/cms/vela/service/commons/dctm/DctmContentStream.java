package com.wiley.cms.vela.service.commons.dctm;

import java.io.InputStream;

import by.hzv.s2.model.ContentStream;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 29.03.2013
 */
public class DctmContentStream implements ContentStream {
    private final InputStream is;
    private boolean proxy;
    private String url;

    public DctmContentStream(InputStream inputStream) {
        this.is = inputStream;
    }

    public DctmContentStream(String url) {
        this.proxy = true;
        this.url = url;
        this.is = null;
    }

    @Override
    public InputStream getStream() {
        return is;
    }

    @Override
    public boolean isProxy() {
        return proxy;
    }

    @Override
    public String getUrl() {
        return url;
    }
}

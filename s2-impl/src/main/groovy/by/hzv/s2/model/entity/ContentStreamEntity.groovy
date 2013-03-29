package by.hzv.s2.model.entity;

import by.hzv.s2.model.ContentStream;
import groovy.transform.EqualsAndHashCode

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 28.03.2013
 */
@EqualsAndHashCode(excludes = ['stream'])
class ContentStreamEntity implements ContentStream {
    String flid
    InputStream stream

    ContentStreamEntity(String flid, InputStream stream) {
        this.flid = flid
        this.stream = stream
    }

    @Override
    InputStream getStream() {
        return stream
    }
}

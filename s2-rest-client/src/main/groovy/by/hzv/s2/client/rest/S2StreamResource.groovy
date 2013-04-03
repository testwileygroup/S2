package by.hzv.s2.client.rest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.AbstractResource;

import com.google.common.io.FileBackedOutputStream;

/**
 * Support multiple reads
 *
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 02.04.2013
 */
class S2StreamResource extends AbstractResource {
    private String filename
    private static final int kiloByte = 1024
    private FileBackedOutputStream fbos = new FileBackedOutputStream(kiloByte)
    private long contentLength = -1

    public S2StreamResource(InputStream inputStream, String filename) throws IOException {
        Validate.notNull(filename, "Filename should be provided")
        this.filename = filename

        Validate.notNull(inputStream, "Input stream should be provided")
        int bytesCopied = IOUtils.copy(inputStream, fbos)

        if (bytesCopied != -1) {
            contentLength = bytesCopied
        }

    }

    @Override
    String getFilename() {
        return filename
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.Resource#getDescription()
     */
    @Override
    String getDescription() {
        return "S2 content stream resource"
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.InputStreamSource#getInputStream()
     */
    @Override
    InputStream getInputStream() throws IOException {
        return fbos.getSupplier().getInput()
    }

    @Override
    long contentLength() throws IOException {
        return contentLength != -1 ? contentLength : super.contentLength()
    }
}

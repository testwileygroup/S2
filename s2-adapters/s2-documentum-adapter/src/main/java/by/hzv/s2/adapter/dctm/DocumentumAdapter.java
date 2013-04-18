package by.hzv.s2.adapter.dctm;

import static com.google.common.collect.Collections2.transform;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import by.hzv.s2.model.ContentStream;
import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.SimpleContentStream;
import by.hzv.s2.service.S2;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;
import com.google.common.base.Optional;
import com.wiley.dctm.DctmContentServer;
import com.wiley.dctm.DctmRepositoryImpl;


/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 29.03.2013
 */
@Component
@Profile("s2-documentum-adapter")
public class DocumentumAdapter implements S2 {
    private static final Logger LOG = LoggerFactory.getLogger(DctmRepositoryImpl.class);
    private static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";

    @Autowired
    private DctmContentServer contentServer;

    @Override
    public String createFile(String filePath, ContentStream content, Map<String, ?> properties) {
        try {
            String doctype = properties == null ? null : (String) properties.get(DOCUMENT_TYPE);

            return contentServer.importDoc(filePath, doctype, content);
        } catch (DfException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteFile(String fid) {
        contentServer.deleteDoc(fid);
    }

    @Override
    public void deleteFileByPath(String filePath) {
        contentServer.deleteDocByPath(filePath);
    }

    @Override
    public void deleteFolder(String fid) {
        contentServer.deleteFolder(fid);
    }

    @Override
    public Collection<FileInfo> listFiles(String fid) {
        String folderPath = contentServer.getPath(fid);
        Collection<IDfDocument> docs = contentServer.getDocsUnderPath(folderPath);
        Collection<FileInfo> fileInfos = transform(docs, new IdfToFileInfoMapper());

        return fileInfos;
    }

    @Override
    public Optional<ContentStream> getContentStream(String fid) {
        InputStream content = contentServer.getDocContent(fid);

        return converToOptional(content);
    }

    @Override
    public Optional<ContentStream> getContentStreamByPath(String filePath) {
        InputStream content = contentServer.getDocContentByPath(filePath);

        return converToOptional(content);
    }

    private Optional<ContentStream> converToOptional(InputStream content) {
        return content == null
            ? Optional.<ContentStream>absent()
            : Optional.<ContentStream>of(new SimpleContentStream(content));
    }

    @Override
    public String getFid(String path) {
        return contentServer.getFid(path);
    }

    @Override
    public String getPath(String fid) {
        return contentServer.getPath(fid);
    }

    @Override
    public Object rpc(String opName, Map<String, ?> parameters) {
        throw new UnsupportedOperationException();
    }
}

package by.hzv.s2.adapter.dctm;

import java.util.ArrayList;
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
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.common.DfException;
import com.wiley.cms.vela.service.commons.dctm.DctmContentServer;
import com.wiley.cms.vela.service.commons.dctm.DctmRepositoryImpl;

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
            return contentServer.importDoc(filePath, (String) properties.get(DOCUMENT_TYPE), content);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFolder(String fid) {
        contentServer.deleteFolder(fid);
    }

    @Override
    public Collection<FileInfo> listFiles(String fid) {
        Collection<FileInfo> result = new ArrayList<>();
        try {
            IDfFolder folder = contentServer.getFolder(fid);
            Collection<IDfDocument> docs = contentServer.getDocsUnderPath(folder.getPath(0));
            //FIXME implement mapping logic
            return result;
        } catch (DfException e) {
            //FIXME implement proper error handling
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ContentStream getContentStream(String fid) {
        return new SimpleContentStream(contentServer.getDocContent(fid));
    }

    @Override
    public ContentStream getContentStreamByPath(String filePath) {
        return new SimpleContentStream(contentServer.getDocContentByPath(filePath));
    }

    @Override
    public String getFid(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPath(String fid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object rpc(String opName, Map<String, ?> parameters) {
        throw new UnsupportedOperationException();
    }
}

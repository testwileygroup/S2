package com.wiley.cms.vela.service.commons.dctm;

import java.io.InputStream;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.common.DfException;

/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 3/4/11
 */
@Service("dctmContentServer")
public class DctmContentServerImpl implements DctmContentServer {
    @Autowired
    private DctmDao dmdao;

    @Override
    public String importDoc(String fullPath, String objectType, ContentStream contentStream) throws DfException {
        return dmdao.importDoc(fullPath, objectType, contentStream);
    }

    @Override
    public InputStream getDocContent(final String docId) {
        return dmdao.getDocContent(docId);
    }

    @Override
    public InputStream getDocContentByPath(String docPath) {
        return dmdao.getDocContentByPath(docPath);
    }

    @Override
    public void deleteDoc(final String objId) {
        dmdao.deleteDoc(objId);
    }

    @Override
    public IDfDocument getDocById(String docId) {
        return dmdao.getDoc(docId);
    }


    @Override
    public IDfDocument getDocByPath(final String docPath) {
        return dmdao.getDocByPath(docPath);
    }

    @Override
    public Collection<IDfDocument> getDocsUnderPath(String folderPath) {
        return dmdao.getDocsUnderPath(folderPath);
    }

    @Override
    public void deleteFolder(String folderId) {
        dmdao.deleteFolder(folderId);
    }

//    @Override
//    public IDfFolder getFolderByPath(String path) throws DfException {
//        return dmdao.getFolderByPath(path);
//    }

    @Override
    public IDfFolder getFolder(String folderId) throws DfException {
        return dmdao.getFolder(folderId);
    }
}
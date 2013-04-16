package com.wiley.dctm;

import java.io.InputStream;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.common.DfException;


/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 3/4/11
 */
public class DctmContentServerImpl implements DctmContentServer {
    @Autowired
    private DctmRepository repository;

    @Override
    public String importDoc(String fullPath, String objectType, ContentStream contentStream) throws DfException {
        return repository.importDoc(fullPath, objectType, contentStream);
    }

    @Override
    public InputStream getDocContent(final String docId) {
        return repository.getDocContent(docId);
    }

    @Override
    public InputStream getDocContentByPath(String docPath) {
        return repository.getDocContentByPath(docPath);
    }

    @Override
    public void deleteDoc(final String objId) {
        repository.deleteDoc(objId);
    }

    @Override
    public void deleteDocByPath(String path) {
        repository.deleteDocByPath(path);
    }

    @Override
    public IDfDocument getDocById(String docId) {
        return repository.<IDfDocument>getObject(docId);
    }


    @Override
    public IDfDocument getDocByPath(final String docPath) {
        return repository.<IDfDocument>getObjectByPath(docPath);
    }

    @Override
    public Collection<IDfDocument> getDocsUnderPath(String folderPath) {
        return repository.getDocsUnderPath(folderPath);
    }

    @Override
    public void deleteFolder(String folderId) {
        repository.deleteFolder(folderId);
    }

    @Override
    public IDfFolder getFolder(String folderId) throws DfException {
        return repository.<IDfFolder>getObject(folderId);
    }

    @Override
    public String getFid(String path) {
        return repository.getFid(path);
    }

    @Override
    public String getPath(String fid) {
        return repository.getPath(fid);
    }
}
package com.wiley.cms.vela.service.commons.dctm;

import java.io.InputStream;
import java.util.Collection;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.common.DfException;

/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 3/4/11
 */
public interface DctmContentServer {
    String importDoc(String fullPath, String type, ContentStream contentStream) throws DfException;
    InputStream getDocContent(String docId);
    InputStream getDocContentByPath(String docPath);
    void deleteDoc(final String objId);

    IDfDocument getDocById(String docId);
    IDfDocument getDocByPath(String docPath);
    Collection<IDfDocument> getDocsUnderPath(String folderPath);

    void deleteFolder(String folderId);
//    IDfFolder getFolderByPath(String path) throws DfException;
    IDfFolder getFolder(String folderId) throws DfException;
}
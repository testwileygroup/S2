package com.wiley.cms.vela.service.commons.dctm;


import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * All conditions should reference target object with alias "o"
 * (e.g. valid condition = "o.isbn = 'XXXXXXXXXXXXX' or o.product_line != 'F'")
 *
 * @author <a href="mailto:eantaev@wiley.com">Evgeny Antaev</a>
 * @since Jun 23, 2010
 */
public interface DctmRepository {
    Collection<IDfDocument> getDocsUnderPath(String folderPath);

    String importDoc(String fullPath, String objectType, ContentStream contentStream) throws DfException;
    InputStream getDocContent(String docId);
    InputStream getDocContentByPath(String docPath);

    IDfDocument getDoc(String docId);
    IDfDocument getDocByPath(String path);

    DfcTemplate getDfcTemplate();

    <T extends IDfPersistentObject> List<T> getObjectsByQuery(IDfSession s, String dql, String dmType)
        throws DfException;

    IDfFolder getFolderByPath(String path) throws DfException;
    IDfFolder getFolder(String folderId);
    void move(String objId, String path);
    void deleteDoc(String objId);
    Void deleteFolderContent(String path);
    void deleteFolder(String folderId);
    String getContentType(IDfSession s, String name) throws DfException;
}


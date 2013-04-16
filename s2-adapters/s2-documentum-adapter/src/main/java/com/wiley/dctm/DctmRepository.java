package com.wiley.dctm;


import java.io.InputStream;
import java.util.Collection;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPersistentObject;

/**
 * DAO object for Documentum
 *
 * @author <a href="mailto:eantaev@wiley.com">Evgeny Antaev</a>
 * @since Jun 23, 2010
 */
public interface DctmRepository {
    /*create and updated*/
    String importDoc(String fullPath, String objectType, ContentStream contentStream);
    void move(String objId, String path);

    /*read*/
    InputStream getDocContent(String docId);
    InputStream getDocContentByPath(String docPath);

    <T extends IDfPersistentObject> T getObject(String objectId);
    <T extends IDfPersistentObject> T getObjectByPath(String path);

    Collection<IDfDocument> getDocsUnderPath(String folderPath);

    String getFid(String path);
    String getPath(String fid);

    /*delete*/
    void deleteDoc(String objId);
    void deleteDocByPath(String path);
    void deleteFolderContent(String path);
    void deleteFolder(String folderId);
}


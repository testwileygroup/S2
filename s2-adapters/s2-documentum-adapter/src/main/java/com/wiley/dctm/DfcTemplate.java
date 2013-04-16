package com.wiley.dctm;

import java.io.InputStream;
import java.util.Collection;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;


/**
 * It simplifies the use of dfc and helps to avoid common errors.
 * It executes core DQL/DFC workflow, leaving application code to provide DQL/DFC and extract results.
 * This class executes DQL/DFC queries or updates,
 * and catching dfc exceptions and translating them to the <code>DfcException</code>.
 * That interface is very similar to Spring <code>JdbcTemplate</code> plus <code>TransactionTemplate</code>.
 *
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-09-09 18:05:45 +0400 (Thu, 09 Sep 2010) $
 */
public interface DfcTemplate {
    /**
     * Execute the action specified by the given callback object within a dfc session and transaction.
     * First entrance in this method will create new dfc transaction and open new dfc session.
     *
     * @param dfcCallback the callback object that specifies the dfc action
     * @return a result object returned by the callback, or null if none
     */
    <T> T executeInSession(DfcCallback<T> dfcCallback);

    <T> T executeInSession(DfcCallback<T> dfcCallback, boolean requiresNew);

    void execQuery(String dql);

    <T extends IDfPersistentObject> Collection<T> getObjectsByQuery(String dql, String objectType);

    <T extends IDfPersistentObject> T getObject(String objectId);

    <T extends IDfPersistentObject> T getObjectByPath(String path);

    InputStream getDocContent(String docId);

    InputStream getDocContentByPath(String docPath);

    String getFid(String path);

    String getPath(String fid);

    void deleteDoc(String objId);

    void deleteDocByPath(String path);

    void deleteFolder(String folderId);

    IDfFolder forceCreateFolder(String typeName, String path);

    String importDoc(String fullpath, String objectType, ContentStream contentStream);
}
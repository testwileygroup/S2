package com.wiley.dctm;

import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import by.hzv.s2.model.ContentStream;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.operations.IDfDeleteOperation;


/**
 * @author <a href="mailto;eantaev@wiley.com">Evgeny Antaev</a>
 * @since Jun 23, 2010
 */
public class DctmRepositoryImpl implements DctmRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DctmRepositoryImpl.class);

    private static final String TYPE_NAME_DM_DOCUMENT = "dm_document";

    private static final String DQL_FOLDER_CONTENT_DELETE_TMPL =
            "delete dm_document %s objects where folder('%s', DESCEND)";
    private static final String DQL_FOLDER_GET_FOLDER_CONTENT_TMPL =
            "select r_object_id, r_aspect_name, i_is_reference, i_is_replica, i_vstamp"
                    + " from dm_document where FOLDER('%s', DESCEND)";
    private static final String DQL_MOVE_TMPL = "update dm_sysobject object move '%s' where r_object_id='%s'";

    @Autowired
    private DfcTemplate dfcTemplate;

    @Override
    public Collection<IDfDocument> getDocsUnderPath(final String folderPath) {
        StopWatch sw = new StopWatch("getDocsUnderPath: " + folderPath);
        sw.start("run dql");

        String dql = String.format(DQL_FOLDER_GET_FOLDER_CONTENT_TMPL, folderPath);
        Collection<IDfDocument> res = dfcTemplate.<IDfDocument>getObjectsByQuery(dql, TYPE_NAME_DM_DOCUMENT);

        sw.stop();
        LOG.debug(sw.prettyPrint());
        LOG.debug(res.size() + " documents retrieved");
        return res;
    }

    public String importDoc(String fullPath, String objectType, ContentStream contentStream) {
        return dfcTemplate.importDoc(fullPath,
                                     StringUtils.defaultString(objectType, TYPE_NAME_DM_DOCUMENT),
                                     contentStream);
    }

    @Override
    public InputStream getDocContent(String docId) {
        return dfcTemplate.getDocContent(docId);
    }

    @Override
    public InputStream getDocContentByPath(String docPath) {
        return dfcTemplate.getDocContentByPath(docPath);
    }

    @Override
    public <T extends IDfPersistentObject> T getObject(String objectId) {
        return dfcTemplate.<T>getObject(objectId);
    }

    @Override
    public <T extends IDfPersistentObject> T getObjectByPath(String path) {
        return dfcTemplate.<T>getObjectByPath(path);
    }

    @Override
    public String getFid(String path) {
        return dfcTemplate.getFid(path);
    }

    @Override
    public String getPath(String fid) {
        return dfcTemplate.getPath(fid);
    }

    @Override
    public void deleteDoc(final String objId) {
        dfcTemplate.deleteDoc(objId);
    }

    @Override
    public void deleteDocByPath(final String path) {
        dfcTemplate.deleteDocByPath(path);
    }

    @Override
    public void deleteFolderContent(final String path) {
        String dql = String.format(DQL_FOLDER_CONTENT_DELETE_TMPL, IDfDeleteOperation.ALL_VERSIONS, path);
        dfcTemplate.execQuery(dql);
    }

    @Override
    public void deleteFolder(String folderId) {
        dfcTemplate.deleteFolder(folderId);
    }

    @Override
    public void move(final String objId, final String path) {
        String dql = String.format(DQL_MOVE_TMPL, path);
        dfcTemplate.execQuery(dql);
    }
}
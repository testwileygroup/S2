package com.wiley.cms.vela.service.commons.dctm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dctmutils.common.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import by.hzv.s2.model.ContentStream;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.operations.IDfDeleteOperation;
import com.google.common.base.Strings;


/**
 * @author <a href="mailto;eantaev@wiley.com">Evgeny Antaev</a>
 * @since Jun 23, 2010
 */
public class DctmRepositoryImpl implements DctmRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DctmRepositoryImpl.class);

    private static final String SLASH = "/";
    private static final String TYPE_NAME_DM_FOLDER = "dm_folder";
    private static final String DEFAULT_CONTENT_TYPE = "binary";

    private static final String DQL_FOLDER_CONTENT_DELETE_TMPL =
            "delete dm_document %s objects where folder('%s', DESCEND)";

    @Autowired
    private DfcTemplate dfcTemplate;

    @Override
    public Collection<IDfDocument> getDocsUnderPath(final String folderPath) {
        return dfcTemplate.executeInSession(new DfcCallback<Collection<IDfDocument>>() {
            @Override
            public Collection<IDfDocument> doInDocbase(IDfSession session) throws Exception {
                StopWatch sw = new StopWatch("getDocsUnderPath: " + folderPath);
                sw.start("run dql");

                String dql = String.format("select r_object_id, r_aspect_name, i_is_reference, i_is_replica, i_vstamp"
                        + " from dm_document where FOLDER('%s', DESCEND)", folderPath);

                IDfEnumeration en = session.getObjectsByQuery(dql, "dm_document");

                sw.stop();
                sw.start("process result");

                List<IDfDocument> coll = new ArrayList<IDfDocument>();
                while (en.hasMoreElements()) {
                    coll.add((IDfDocument) en.nextElement());
                }

                sw.stop();
                LOG.debug(sw.prettyPrint());
                return coll;
            }
        });
    }

    public String importDoc(final String fullPath, final String objectType, final ContentStream contentStream)
        throws DfException {

        return dfcTemplate.executeInSession(new DfcCallback<String>() {

            @Override
            public String doInDocbase(IDfSession session) throws Exception {
                return doImportDoc(session, fullPath, objectType, contentStream);
            }
        });
    }

    @Override
    public InputStream getDocContent(final String docId) {
        return dfcTemplate.executeInSession(new DfcCallback<InputStream>() {

            @Override
            public InputStream doInDocbase(IDfSession session) throws Exception {
                IDfDocument doc = (IDfDocument) session.getObject(new DfId(docId));
                return (doc != null) ? doc.getContent() : null;
            }
        });
    }

    @Override
    public InputStream getDocContentByPath(final String docPath) {
        return dfcTemplate.executeInSession(new DfcCallback<InputStream>() {

            @Override
            public InputStream doInDocbase(IDfSession session) throws Exception {
                IDfDocument doc = (IDfDocument) session.getObjectByPath(docPath);
                return (doc != null) ? doc.getContent() : null;
            }
        });
    }

    @Override
    public IDfDocument getDoc(final String docId) {
        return dfcTemplate.executeInSession(new DfcCallback<IDfDocument>() {

            @Override
            public IDfDocument doInDocbase(IDfSession session) throws Exception {
                return (IDfDocument) session.getObject(new DfId(docId));
            }
        });
    }

    @Override
    public IDfFolder getFolder(final String folderId) {
        return dfcTemplate.executeInSession(new DfcCallback<IDfFolder>() {

            @Override
            public IDfFolder doInDocbase(IDfSession session) throws Exception {
                return (IDfFolder) session.getObject(new DfId(folderId));
            }
        });
    }

    @Override
    public IDfDocument getDocByPath(final String path) {
        return dfcTemplate.executeInSession(new DfcCallback<IDfDocument>() {

            @Override
            public IDfDocument doInDocbase(IDfSession session) throws Exception {
                return (IDfDocument) session.getObjectByPath(path);
            }
        });
    }


    @Override
    public IDfFolder getFolderByPath(final String path) {
        return dfcTemplate.executeInSession(new DfcCallback<IDfFolder>() {
            public IDfFolder doInDocbase(IDfSession s) throws Exception {
                return s.getFolderByPath(path);
            }
        });
    }

    @Override
    public void deleteDoc(final String objId) {
        dfcTemplate.executeInSession(new DfcCallback<Void>() {
            public Void doInDocbase(IDfSession s) throws Exception {
                getObject(objId, s).destroy();
                return null;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IDfPersistentObject> List<T> getObjectsByQuery(IDfSession s, String dql, String dmType)
        throws DfException {

        LOG.trace("getObjectsByQuery [{}] [{}]", dql, dmType);

        List<T> coll = new ArrayList<T>();
        IDfEnumeration en = s.getObjectsByQuery(dql, dmType);
        while (en.hasMoreElements()) {
            coll.add((T) en.nextElement());
        }

        return coll;
    }


    @Override
    public DfcTemplate getDfcTemplate() {
        return dfcTemplate;
    }

    public void setDfcTemplate(DfcTemplate dfcTemplate) {
        this.dfcTemplate = dfcTemplate;
    }


    @Override
    public Void deleteFolderContent(final String path) {
        return dfcTemplate.executeInSession(new DfcCallback<Void>() {
            public Void doInDocbase(IDfSession session) throws Exception {
                String dql = String.format(DQL_FOLDER_CONTENT_DELETE_TMPL, IDfDeleteOperation.ALL_VERSIONS, path);
                execQuery(session, dql);
                return null;
            }
        });
    }

    @Override
    public String getContentType(IDfSession s, String name) throws DfException {
        String contentType = FormatHelper.getInstance(s).getFormatForExtension(FilenameUtils.getExtension(name));
        if (Strings.isNullOrEmpty(contentType)) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        return contentType;
    }





    @Override
    public void deleteFolder(final String folderId) {
        dfcTemplate.executeInSession(new DfcCallback<Void>() {
            public Void doInDocbase(IDfSession session) throws Exception {
                IDfClientX clientx = new DfClientX();
                IDfPersistentObject obj = session.getObject(clientx.getId(folderId));

                if (obj == null) {
                    LOG.trace("Object with id [{}] does not exist", folderId);
                    return null;
                }

                IDfDeleteOperation dop = clientx.getDeleteOperation();
                dop.setDeepFolders(true);
                dop.setVersionDeletionPolicy(IDfDeleteOperation.ALL_VERSIONS);
                dop.add(obj);

                dop.execute();
                return null;
            }
        });
    }

    private IDfPersistentObject getObject(String objId, IDfSession session) throws DfException {
        IDfId sysObjId = new DfId(objId);
        return session.getObject(sysObjId);
    }

    @Override
    public void move(final String objId, final String path) {
        dfcTemplate.executeInSession(new DfcCallback<Void>() {

            @Override
            public Void doInDocbase(IDfSession session) throws Exception {
                String dql = String.format("update dm_sysobject object move '%s' where r_object_id='%s'", path);
                execQuery(session, dql);
                return null;
            }
        });
    }

    private void execQuery(IDfSession session, String dql) throws DfException {
        IDfQuery fq = new DfQuery();
        fq.setDQL(dql);

        IDfCollection coll = null;
        try {
            coll = fq.execute(session, DfQuery.DF_READ_QUERY);
        } finally {
            closeQuietly(coll);
        }
    }

    private static void closeQuietly(IDfCollection iCollection) {
        if (iCollection != null) {
            try {
                iCollection.close();
            } catch (DfException e) {
                LOG.error("IDfCollection.close() failed", e);
            }
        }
    }

    private static IDfFolder forceCreateFolder(IDfSession session, String typeName, String path) throws DfException {
        LOG.debug("forceCreateFolder [{}, {}]", typeName, path);
        Validate.isTrue(path.startsWith(SLASH), "Path should start with root /");

        IDfFolder ret = session.getFolderByPath(path);
        if (ret != null) { // folder exists. No need to create it. Just return it.
            return ret;
        }

        // create folder
        ret = (IDfFolder) session.newObject(typeName);
        ret.setObjectName(StringUtils.substringAfterLast(path, SLASH));
        String parentPath = StringUtils.substringBeforeLast(path, SLASH);
        if (StringUtils.isNotEmpty(parentPath)) { // folder has parent. Check if it exists and if not then create it
            forceCreateFolder(session, TYPE_NAME_DM_FOLDER, parentPath);
            ret.link(parentPath);
        }
        ret.save();
        return ret;
    }

    private String doImportDoc(IDfSession session, String fullpath, String objectType, ContentStream contentStream)
        throws DfException, IOException {

        String docId;
        final StopWatch stopWatch = new StopWatch("importDocument(..)");

        stopWatch.start("s.getObjectByPath(fullpath)");
        IDfSysObject doc = (IDfSysObject) session.getObjectByPath(fullpath);
        stopWatch.stop();
        if (doc == null) {
            LOG.trace("Object under path '{}' is not found. Create a new one", fullpath);

            /* force create folders */
            stopWatch.start("forceCreateFolders");
            String folderPath = FilenameUtils.getFullPathNoEndSeparator(fullpath);
            forceCreateFolder(session, TYPE_NAME_DM_FOLDER, folderPath);
            stopWatch.stop();

            /* create document */
            stopWatch.start("create DCTM object");
            String name = FilenameUtils.getName(fullpath);
            doc = (IDfSysObject) session.newObject(objectType);
            doc.setObjectName(name);
            doc.setContentType(getContentType(session, name));
            stopWatch.stop();
            stopWatch.start("link DCTM object");
            doc.link(folderPath);
            stopWatch.stop();
        }

        stopWatch.start("write DCTM object");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(contentStream.getStream(), baos);
        doc.setContent(baos);
        stopWatch.stop();
        stopWatch.start("save DCTM object");
        doc.save();
        stopWatch.stop();
        docId =  doc.getObjectId().getId();

        LOG.trace(stopWatch.prettyPrint());
        return docId;
    }
}
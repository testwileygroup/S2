package com.wiley.dctm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dctmutils.common.DmObjectHelper;
import org.dctmutils.common.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import by.hzv.s2.model.ContentStream;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.DfServiceException;
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
import com.documentum.operations.IDfDeleteOperation;
import com.google.common.base.Strings;

import fj.P;
import fj.P2;

/**
 * It simplifies the use of dfc and helps to avoid common errors.
 * It executes core DQL/DFC workflow, leaving application code to provide DQL/DFC and extract results.
 * This class executes DQL/DFC queries or updates,
 * and catching dfc exceptions and translating them to the <code>DfcException</code>.
 * That interface is very similar to Spring <code>JdbcTemplate</code> plus <code>TransactionTemplate</code>.
 *
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-09-20 18:02:02 +0400 (Mon, 20 Sep 2010) $
 */
public class DfcTemplateImpl implements DfcTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(DfcTemplateImpl.class);

    private static final String SLASH = "/";
    private static final String TYPE_NAME_DM_FOLDER = "dm_folder";
    private static final String DEFAULT_CONTENT_TYPE = "binary";

    @Autowired
    private DfcSessionFactory dfcSessionFactory;

    @Override
    public void execQuery(final String dql) {
        executeInSession(new DfcCallback<Void>() {
            @Override
            public Void doInDocbase(IDfSession session) throws Exception {
                IDfQuery fq = new DfQuery();
                fq.setDQL(dql);

                IDfCollection coll = null;
                try {
                    try {
                        coll = fq.execute(session, DfQuery.DF_READ_QUERY);
                    } catch (DfException e) {
                        throw new DfcException(e);
                    }
                } finally {
                    closeQuietly(coll);
                }
                return null;
            }
        });
    }

    @Override
    public <T> T executeInSession(DfcCallback<T> dfcCallback) {
        try {
            return executeInSession(dfcCallback, false);
        } catch (Exception e) {
            throw new DfcException(e);
        }
    }

    @Override
    public <T> T executeInSession(DfcCallback<T> dfcCallback, boolean requiresNew) {
        try {
            return executeInSession(dfcSessionFactory, dfcCallback, requiresNew);
        } catch (Exception e) {
            throw new DfcException(e);
        }
    }

    @Override
    public <T extends IDfPersistentObject> Collection<T> getObjectsByQuery(final String dql, final String objectType) {
        return executeInSession(new DfcCallback<Collection<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Collection<T> doInDocbase(IDfSession session) throws Exception {
                IDfEnumeration en = session.getObjectsByQuery(dql, objectType);

                List<T> coll = new ArrayList<T>();
                while (en.hasMoreElements()) {
                    coll.add((T) en.nextElement());
                }

                return coll;
            }
        });
    }

    @Override
    public <T extends IDfPersistentObject> T getObject(final String objectId) {
        return executeInSession(new DfcCallback<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T doInDocbase(IDfSession session) throws Exception {
                return (T) session.getObject(new DfId(objectId));
            }
        });
    }

    @Override
    public <T extends IDfPersistentObject> T getObjectByPath(final String path) {
        return executeInSession(new DfcCallback<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T doInDocbase(IDfSession session) throws Exception {
                return (T) session.getObjectByPath(path);
            }
        });
    }

    @Override
    public InputStream getDocContent(final String docId) {
        return executeInSession(new DfcCallback<InputStream>() {

            @Override
            public InputStream doInDocbase(IDfSession session) throws Exception {
                IDfDocument doc = getObject(docId);
                return doc.getContent();
            }
        });
    }

    @Override
    public InputStream getDocContentByPath(final String docPath) {
        return executeInSession(new DfcCallback<InputStream>() {

            @Override
            public InputStream doInDocbase(IDfSession session) throws Exception {
                IDfDocument doc = getObjectByPath(docPath);
                return doc == null ? null : doc.getContent();
            }
        });
    }


    @Override
    public String getFid(String path) {
        try {
            IDfSysObject obj = getObjectByPath(path);
            return obj == null ? null : obj.getObjectId().getId();
        } catch (DfException e) {
            throw new DfcException("Cannot retrieve ID for " + path);
        }
    }

    @Override
    public String getPath(String fid) {
        try {
            IDfSysObject obj = getObject(fid);
            return DmObjectHelper.getPath(obj) + SLASH + obj.getObjectName();
        } catch (DfException e) {
            throw new DfcException("Cannot retrieve Path for " + fid);
        }
    }

    @Override
    public void deleteDoc(final String objId) {
        executeInSession(new DfcCallback<Void>() {
            public Void doInDocbase(IDfSession s) throws Exception {
                getObject(objId).destroy();
                return null;
            }
        });
    }

    @Override
    public void deleteDocByPath(final String path) {
        executeInSession(new DfcCallback<Void>() {
            public Void doInDocbase(IDfSession s) throws Exception {
                getObjectByPath(path).destroy();
                return null;
            }
        });
    }

    @Override
    public void deleteFolder(final String folderId) {
        executeInSession(new DfcCallback<Void>() {
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


    @Override
    public String importDoc(final String fullpath, final String objectType, final ContentStream contentStream) {
        return executeInSession(new DfcCallback<String>() {
            @Override
            public String doInDocbase(IDfSession session) throws Exception {
                final StopWatch stopWatch = new StopWatch("importDocument(..)");
                stopWatch.start("s.getObjectByPath(fullpath)");
                IDfSysObject doc = (IDfSysObject) session.getObjectByPath(fullpath);
                stopWatch.stop();
                if (doc == null) {
                    LOG.trace("Object under path '{}' is not found. Create a new one", fullpath);
                    /* force create folders */
                    stopWatch.start("forceCreateFolders");
                    String folderPath = FilenameUtils.getFullPathNoEndSeparator(fullpath);
                    doForceCreateFolder(session, TYPE_NAME_DM_FOLDER, folderPath);
                    stopWatch.stop();
                    /* create document */
                    stopWatch.start("create DCTM object");
                    String name = FilenameUtils.getName(fullpath);
                    doc = (IDfSysObject) session.newObject(objectType);
                    doc.setObjectName(name);
                    doc.setContentType(resolveContentType(session, fullpath));
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
                String docId =  doc.getObjectId().getId();
                LOG.trace(stopWatch.prettyPrint());

                return docId;
            }
        });
    }

    @Override
    public IDfFolder forceCreateFolder(final String typeName, final String path) {
        return executeInSession(new DfcCallback<IDfFolder>() {
            @Override
            public IDfFolder doInDocbase(IDfSession session) throws Exception {
                return doForceCreateFolder(session, typeName, path);
            }
        });
    }

    private IDfFolder doForceCreateFolder(IDfSession session, String typeName, String path) throws DfException {
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
            doForceCreateFolder(session, TYPE_NAME_DM_FOLDER, parentPath);
            ret.link(parentPath);
        }
        ret.save();
        return ret;
    }

    private static <T> T executeInSession(DfcSessionFactory factory, DfcCallback<T> callback, boolean requiresNew) {
        P2<IDfSession, Boolean> p = getSession(factory, requiresNew); // Session and "is it new" flag
        IDfSession s = p._1();
        boolean closeOnCompletion = p._2(); // whether or not the session is opened by this call
        try {
            T ret = callback.doInDocbase(s);
            if (closeOnCompletion /* && sm.isTransactionActive()*/) {
                LOG.trace("commitTransaction");
                s.getSessionManager().commitTransaction();
            }
            return ret;
        } catch (Throwable e) {
            if (closeOnCompletion /*&& sm.isTransactionActive()*/) {
                try {
                    LOG.trace("rollbackTransaction");
                    s.getSessionManager().setTransactionRollbackOnly();
                    s.getSessionManager().abortTransaction();
                } catch (DfServiceException se) { // this exception is not re-thrown
                    LOG.error("error on rolling back transaction (ignored)", se);
                }
            }
            throw new DfcException(e);
        } finally {
            if (closeOnCompletion) {
                factory.close(s);
            }
        }
    }

    /**
     * Gets current DFC session from session factory if one is open; Otherwise opens new session.
     *
     * @param sessionFactory DfcSessionFactory for session instance creation
     * @param requiresNew true makes method to open new session ignoring existing current session
     * @return Pair of IDfSession and boolean that specifies if this session is new
     */
    private static P2<IDfSession, Boolean> getSession(DfcSessionFactory sessionFactory, boolean requiresNew) {
        IDfSession s = sessionFactory.getCurrentSession();
        if (requiresNew || s == null) {
            try {
                return P.p(sessionFactory.openSession(true), true);
            } catch (DfException e) {
                throw new DfcException(e);
            }
        }
        return P.p(s, false);
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

    private String resolveContentType(IDfSession s, String name) throws DfException {
        String contentType = FormatHelper.getInstance(s).getFormatForExtension(FilenameUtils.getExtension(name));
        if (Strings.isNullOrEmpty(contentType)) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        return contentType;
    }
}

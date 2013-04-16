package com.wiley.dctm;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ClassPathResource;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;


/**
 * A factory for connections to the dfc session that this DfcSessionFactory object represents.
 *
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-08-27 13:40:50 +0400 (Fri, 27 Aug 2010) $
 */
public class SimpleDfcSessionFactory implements DfcSessionFactory {
    private static ThreadLocal<IDfSession> sessionHolder = new ThreadLocal<IDfSession>();

    private String username;
    private String password;
    private String docBaseName;
    private Properties dfcProperties;
    private Properties dctmProperties;

    public SimpleDfcSessionFactory(Properties dfcProperties, Properties dctmProperties) {
        Validate.notEmpty(dfcProperties, "dfc.properties file should be present in classpath");
        this.dfcProperties = dfcProperties;
        Validate.notEmpty(dctmProperties, "dctm.properties file should be present in classpath");
        this.dctmProperties = dctmProperties;
        this.docBaseName = dfcProperties.getProperty("dfc.globalregistry.repository");
        this.username = dctmProperties.getProperty("authentication.username");
        this.password = dctmProperties.getProperty("authentication.password");
    }


    public static void init() throws IOException {
        ClassPathResource cpr = new ClassPathResource("dfc.properties");
        System.setProperty("dfc.properties.file", cpr.getFile().getAbsolutePath());
    }

    @PostConstruct
    private void selfCheck() throws DfException {
        testConnection();
    }

    private IDfSessionManager newSessionManager() throws DfException {
        IDfClient client = DfClient.getLocalClient();
        IDfSessionManager sessionManager = client.newSessionManager();
        sessionManager.getConfig().setLocale("en");

        IDfLoginInfo linf = new DfLoginInfo();
        linf.setUser(username);
        linf.setPassword(password);
        sessionManager.setIdentity(docBaseName, linf);

        return sessionManager;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IDfSession openSession(boolean anOpenTransaction) throws DfException {
        try {
            IDfSessionManager sessionManager = newSessionManager();
            if (anOpenTransaction) {
                if (sessionManager.isTransactionActive()) {
                    sessionManager.setTransactionRollbackOnly();
                    throw new DfcException("DFC nested transactions are not supported");
                }
                sessionManager.beginTransaction();
            }

            IDfSession newSession = sessionManager.getSession(docBaseName);
            if (newSession == null) {
                throw new DfcException("could not open DFC session");
            }

            bind(newSession);
            return newSession;
        } catch (DfServiceException e) {
            throw new DfcException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IDfSession getCurrentSession() {
        return sessionHolder.get();
    }

    /**
     * Bind session to current thread.
     *
     * @param aSession dfc session.
     */
    private void bind(IDfSession aSession) {
        sessionHolder.set(aSession);
    }

    /**
     * Unbind session from current thread.
     *
     */
    private void unbind() {
        sessionHolder.remove();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(IDfSession aSession) {
        unbind();
        aSession.getSessionManager().release(aSession);
    }

    private void testConnection() throws DfException {
        newSessionManager().authenticate(docBaseName);
    }
}
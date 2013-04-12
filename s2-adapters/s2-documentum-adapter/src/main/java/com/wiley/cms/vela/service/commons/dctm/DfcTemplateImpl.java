package com.wiley.cms.vela.service.commons.dctm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

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

    @Autowired
    private DfcSessionFactory dfcSessionFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T executeInSession(DfcCallback<T> dfcCallback) {
        return executeInSession(dfcCallback, false);
    }

    @Override
    public <T> T executeInSession(DfcCallback<T> dfcCallback, boolean requiresNew) {
        return executeInSession(dfcSessionFactory, dfcCallback, requiresNew);
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
}

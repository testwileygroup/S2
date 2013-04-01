package com.wiley.cms.vela.service.commons.dctm;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * A factory for connections to the dfc session that this DfcSessionFactory object represents.
 *
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-08-27 13:40:50 +0400 (Fri, 27 Aug 2010) $
 */
public interface DfcSessionFactory {
    /**
     * Open new dfc session, bind it to the current thread and return session.
     *
     * @param anOpenTransaction (true - open dfc transaction before return session).
     * @return dfc session.
     *
     * @throws com.documentum.fc.common.DfException if cannot open new session.
     */
    IDfSession openSession(boolean anOpenTransaction) throws DfException;

    /**
     * Return dfc session which was bind to current thread.
     *
     * @return dfc session.
     */
    IDfSession getCurrentSession();

    /**
     * Close dfc session and unbind it from current thread.
     *
     * @param aSession dfc session.
     */
    void close(IDfSession aSession);
}


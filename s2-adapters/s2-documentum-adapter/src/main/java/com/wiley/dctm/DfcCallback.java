package com.wiley.dctm;


import com.documentum.fc.client.IDfSession;

/**
 * Callback interface for dfc code.
 * To be used with <code>DfcTemplate</code>'s execute method;
 * assemble often as anonymous class within a method implementation.
 *
 * @param <R> Callback result typ
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-04-07 08:41:32 +0400 (Wed, 07 Apr 2010) $
 */
public interface DfcCallback<R> {

    /**
     * Gets called by DfcTemplate.execute within a dfc transaction and session.
     * Does not need to care about dfc session and transactions itself.
     *
     * @param aSession dfc session.
     * @return a result object, or null
     * @throws com.documentum.fc.common.DfException
     *          when some DFC exception.
     */
    R doInDocbase(IDfSession aSession) throws Exception;
}
package com.wiley.cms.vela.service.commons.dctm;


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
}
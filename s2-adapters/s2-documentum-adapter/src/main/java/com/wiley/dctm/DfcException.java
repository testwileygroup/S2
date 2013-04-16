package com.wiley.dctm;

/**
 * @author <a href="mailto:SDebalchuk@wiley.ru">Stanislav Debalchuk</a>
 *         $Date: 2010-04-07 08:41:32 +0400 (Wed, 07 Apr 2010) $
 */
public class DfcException extends RuntimeException {
    private static final long serialVersionUID = 9203818126857269155L;

    public DfcException(String message) {
        super(message);
    }

    public DfcException(String message, Throwable cause) {
        super(message, cause);
    }

    public DfcException(Throwable cause) {
        super(cause);
    }
}

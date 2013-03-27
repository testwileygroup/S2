package by.hzv.storage.util;

import java.util.concurrent.locks.Lock;

/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 7/3/11
 */
public interface KeyBasedLocker {
    Lock getLock(String resource);
}

package by.hzv.storage.util;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapMaker;



/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 7/2/11
 */
@Component
public class SimpleLocker implements KeyBasedLocker {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleLocker.class);
    private final ConcurrentMap<String, Lock> locks = new MapMaker().weakValues().makeMap();

    @Override
    public Lock getLock(String resource) {
        logState();

        Lock lock = locks.get(resource);
        if (lock != null) {
            return lock;
        }

        lock = new ReentrantLock();
        Lock old = locks.putIfAbsent(resource, lock);
        return old == null ? lock : old;
    }

    private void logState() {
        LOG.trace("{} elements locked", locks.size());
    }
}
package by.hzv.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.hzv.storage.dao.DataStoreRepository;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
@Component
public class DataStoreController {
    @Autowired
    private DataStoreRepository repository;


    ContentStream getContentStream(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    void storeContentStream(String flid, ContentStream content) {
        // TODO Auto-generated method stub
    }

}

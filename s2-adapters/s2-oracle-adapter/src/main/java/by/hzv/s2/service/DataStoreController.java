package by.hzv.s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.hzv.s2.dao.DataStoreRepository;
import by.hzv.s2.model.ContentStream;
import by.hzv.s2.model.entity.ContentStreamEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
@Component
public class DataStoreController {
    @Autowired
    private DataStoreRepository repository;


    ContentStream getContentStream(String flid) {
        if (flid == null) {
            return null;
        }

        // TODO map entity to DTO and return it
        return repository.find(flid);
    }

    void storeContentStream(String flid, ContentStream content) {
        ContentStreamEntity entity = new ContentStreamEntity(flid, content.getStream());
        repository.save(entity);
    }

}

package by.hzv.s2.dao;

import by.hzv.s2.model.entity.ContentStreamEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 26.03.2013
 */
public interface DataStoreRepository {

    ContentStreamEntity find(String flid);

    void save(ContentStreamEntity entity);

}

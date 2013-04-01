package by.hzv.s2.dao;

import org.springframework.stereotype.Repository;

import by.hzv.s2.model.entity.ContentStreamEntity;

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 28.03.2013
 */
@Repository
public class SimpleJdbcDataStoreRepository implements DataStoreRepository {
	

    /* (non-Javadoc)
     * @see by.hzv.s2.dao.DataStoreRepository#find(java.lang.String)
     */
    @Override
    public ContentStreamEntity find(String flid) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see by.hzv.s2.dao.DataStoreRepository#save(by.hzv.s2.model.entity.ContentStreamEntity)
     */
    @Override
    public void save(ContentStreamEntity entity) {
        // TODO Auto-generated method stub

    }

}

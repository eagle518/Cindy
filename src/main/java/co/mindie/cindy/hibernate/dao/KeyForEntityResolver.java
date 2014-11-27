package co.mindie.cindy.hibernate.dao;

/**
 * Created by simoncorsin on 17/11/14.
 */
public interface KeyForEntityResolver<Key, ObjectType> {

	Key getKeyForEntity(ObjectType object);

}

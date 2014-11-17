package co.mindie.cindy.dao.impl;

/**
 * Created by simoncorsin on 17/11/14.
 */
public interface KeyForEntityResolver<Key, ObjectType> {

	Key getKeyForEntity(ObjectType object);

}

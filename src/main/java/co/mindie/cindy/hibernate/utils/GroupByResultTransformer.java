package co.mindie.cindy.hibernate.utils;

import me.corsin.javatools.reflect.ReflectionUtils;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by simoncorsin on 18/08/14.
 */
public class GroupByResultTransformer implements ResultTransformer {
    private static final long serialVersionUID = 882346353573589561L;
    private Class<?> outputClass;

    public GroupByResultTransformer(Class<?> outputClass) {
        this.outputClass = outputClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            Field[] fields = this.outputClass.getDeclaredFields();
            Object outputObject = this.outputClass.newInstance();

            for (int i = 0; i < fields.length && i < tuple.length; i++) {
                ReflectionUtils.setField(outputObject, fields[i], tuple[i]);
            }

            return outputObject;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }



}
package com.Fluxsens.CodesGeneration.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.proxy.HibernateProxy;

/**
 * @author keshab.g
 */
public class BeanUtils
{

    private static final Log LOG = LogFactory.getLog(BeanUtils.class);

    @SuppressWarnings("unchecked")
    public static <T, V> void populateEntity(T entity, V dataEntity)
    {
        if (entity instanceof HibernateProxy) entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        if (dataEntity instanceof HibernateProxy) dataEntity = (V) ((HibernateProxy) dataEntity).getHibernateLazyInitializer().getImplementation();

        Field[] fields = dataEntity.getClass().getDeclaredFields();
        Field[] superClassFields = dataEntity.getClass().getSuperclass().getDeclaredFields();
        for (Field ff : superClassFields)
        {
            fields[fields.length - 1] = ff;
        }
        for (Field fi : fields)
        {
            try
            {
                Field f = entity.getClass().getDeclaredField(fi.getName());
                f.setAccessible(true);
                fi.setAccessible(true);
                Object val = fi.get(dataEntity);
                if (val != null) f.set(entity, val);
            }
            catch (Exception e)
            {
                if (fi.getGenericType() instanceof ParameterizedType)
                {
                    // Collection
                }
                else
                {
                    try
                    {
                        Field f = entity.getClass().getSuperclass().getDeclaredField(fi.getName());
                        f.setAccessible(true);
                        fi.setAccessible(true);
                        Object val = fi.get(dataEntity);
                        if (val != null) f.set(entity, val);
                    }
                    catch (Exception e1)
                    {
                        LOG.info("error " + e1.getMessage());
                    }
                }

            }
        }
    }

    public static <T> Field getFieldForPropertyName(T entity, String name)
    {
        try
        {
            return entity.getClass().getDeclaredField(name);
        }
        catch (Exception e)
        {
            LOG.info("field Not found " + e.getLocalizedMessage());
            return null;
        }
    }
}

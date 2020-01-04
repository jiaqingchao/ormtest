package com.jqc.ormtest.step010.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class XxxEntity_Helper {
    /**
     * 创建用户实体类
     * @param rs
     * @return
     * @throws Exception
     */
    public <TEntity> TEntity create(Class<TEntity> entityClazz, ResultSet rs) throws Exception{
        if(entityClazz == null || rs == null){
            return null;
        }

        //创建新的实体对象
        Object newEntity = entityClazz.getDeclaredConstructor().newInstance();

        //获取实体类的所有字段
        Field[] fields = entityClazz.getDeclaredFields();
        for (Field filed : fields){

            //获取自定义注解
            Column annoColumn = filed.getAnnotation(Column.class);
            if(annoColumn == null){
                continue;
            }

            //获取数据表字段
            String columnName = annoColumn.name();
            //获取数据表中的值
            Object columnVal = rs.getObject(columnName);
            if(columnVal == null){
                return null;
            }

            filed.setAccessible(true);
            filed.set(newEntity, columnVal);
        }

        return (TEntity) newEntity;
    }
}

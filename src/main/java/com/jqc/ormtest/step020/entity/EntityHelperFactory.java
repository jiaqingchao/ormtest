package com.jqc.ormtest.step020.entity;

import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;

public final class EntityHelperFactory {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EntityHelperFactory.class);
    /**
     * 私有化默认构造器
     */
    private EntityHelperFactory(){
    }

    static public AbstractEntityHelper getEntityHelper(Class<?> entityClazz) throws Exception{
        if(entityClazz == null){
            return null;
        }

        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();

        //import java.sql.ResultSet
        //import com.jqc.ormtest.step020.entity.XxxEntity
        pool.importPackage(ResultSet.class.getName());
        pool.importPackage(entityClazz.getName());

        //拿到AbstractEntityHelper类
        ClassPool.getDefault().insertClassPath(new ClassClassPath(AbstractEntityHelper.class));
        CtClass clazzHelper = pool.getCtClass(AbstractEntityHelper.class.getName());
        //要创建的助手类名
        String helperClazzName = entityClazz.getName() + "Helper";

        //创建XxxEntityHelper extends AbstractEntityHelper
        CtClass cc = pool.makeClass(helperClazzName, clazzHelper);

        //创建构造器
        //生产如下代码 ： public XxxEntityHelper(){}
        CtConstructor constructor = new CtConstructor(new CtClass[0], cc);
        constructor.setBody("{}");

        cc.addConstructor(constructor);

        StringBuilder sb = new StringBuilder();
        sb.append("public Object create(java.sql.ResultSet rs) throws Exception{\n");
        sb.append(entityClazz.getName())
            .append(" obj = new ")
            .append(entityClazz.getName())
            .append("();\n");

        Field[] fields = entityClazz.getDeclaredFields();
        Method[] methods = entityClazz.getDeclaredMethods();

        for (Field filed : fields){
            Column annoColumn = filed.getAnnotation(Column.class);
            if(annoColumn == null){
                continue;
            }

            String columnName = annoColumn.name();

            String filedName = filed.getName();
            for (Method method : methods){
                String methodName = method.getName();
                if(!methodName.startsWith("set") ||
                        !methodName.toLowerCase().endsWith(filedName.toLowerCase())){
                    continue;
                }
                if(filed.getType() == Integer.TYPE){
                    sb.append("obj.")
                        .append(methodName)
                        .append("(rs.getInt(\"")
                        .append(columnName)
                        .append("\"));");
                }else if(filed.getType().equals(String.class)){
                    sb.append("obj.")
                            .append(methodName)
                            .append("(rs.getString(\"")
                            .append(columnName)
                            .append("\"));");
                }else if(filed.getType() == Double.TYPE){
                    sb.append("obj.")
                            .append(methodName)
                            .append("(rs.getDouble(\"")
                            .append(columnName)
                            .append("\"));");
                }else if(filed.getType() == Long.TYPE){
                    sb.append("obj.")
                            .append(methodName)
                            .append("(rs.getLong(\"")
                            .append(columnName)
                            .append("\"));");
                }else if(filed.getType() == Float.TYPE){
                    sb.append("obj.")
                            .append(methodName)
                            .append("(rs.getFloat(\"")
                            .append(columnName)
                            .append("\"));");
                }else if(Object.class.isAssignableFrom(filed.getType())){
                    sb.append("obj.")
                            .append(methodName)
                            .append("((" + filed.getType().getName() + ")rs.getObject(\"")
                            .append(columnName)
                            .append("\"));");
                }else {
                    LOGGER.error("不支持的数据类型, type = " + filed.getType());
                }

            }
        }
        sb.append("return obj;\n");
        sb.append("}\n");

        CtMethod cm = CtNewMethod.make(sb.toString(), cc);
        cc.addMethod(cm);

        String classPath = EntityHelperFactory.class.getResource("/").toString().substring(6);
        cc.writeFile(classPath);

        Class<?> javaClazz = cc.toClass();

        Object helperImpl = javaClazz.getDeclaredConstructor().newInstance();

        return (AbstractEntityHelper)helperImpl;
    }
}

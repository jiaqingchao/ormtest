package com.jqc.ormtest.step020.entity;

import java.sql.ResultSet;

/**
 * 抽象实体助手
 */
public abstract class AbstractEntityHelper {
    /**
     * 创建实体类
     * @param rs
     * @return
     * @throws Exception
     */
    public abstract Object create(ResultSet rs) throws Exception;
}

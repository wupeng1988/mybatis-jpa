package org.apache.ibatis.singledog.jpa.generator.impl;

/**
 * Created by Adam on 2017/7/4.
 */
public class SaveSelectiveAutoIncrementGeneratorImpl extends SaveSqlGeneratorImpl {

    @Override
    boolean includeNull() {
        return false;
    }

    @Override
    boolean useGenerateKeys() {
        return true;
    }
}

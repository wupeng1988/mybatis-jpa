package org.apache.ibatis.singledog.jpa.generator.impl;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveAllAutoIncrementKeyGeneratorImpl extends SaveAllGeneratorImpl {

    @Override
    boolean useGeneratedKeys() {
        return true;
    }
}

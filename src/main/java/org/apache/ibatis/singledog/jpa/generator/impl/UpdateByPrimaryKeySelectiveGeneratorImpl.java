package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class UpdateByPrimaryKeySelectiveGeneratorImpl extends UpdateByPrimaryKeyGeneratorIml {

    @Override
    boolean includeNull() {
        return false;
    }
}

package org.apache.ibatis.singledog.jpa.annotation;

import org.apache.ibatis.singledog.jpa.generator.SqlGenerator;

import java.lang.annotation.*;

/**
 * Created by Adam on 2017/7/4.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomProvider {

    Class<? extends SqlGenerator> value();

}

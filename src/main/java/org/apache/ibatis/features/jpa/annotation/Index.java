/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.features.jpa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Index annotation is used in schema generation. Note that it is not necessary to specify an index
 * for a primary key, as the primary key index will be created automatically, however, the Index annotation
 * may be used to specify the ordering of the columns in the index for the primary key.
 *
 * @since JPA 2.1
 */
@Target({}) @Retention(RUNTIME)
public @interface Index {
	/**
	 * (Optional) The name of the index.  Defaults to a provider-generated value.
	 *
	 * @return The index name
	 */
	String name() default "";

	/**
	 * (Required) The names of the columns to be included in the index.
	 *
	 * @return The names of the columns making up the index
	 */
	String columnList();

	/**
	 * (Optional) Whether the index is unique.  Default is false.
	 *
	 * @return Is the index unique?
	 */
	boolean unique() default false;
}

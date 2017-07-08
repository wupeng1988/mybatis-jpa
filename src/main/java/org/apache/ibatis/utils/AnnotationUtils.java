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
package org.apache.ibatis.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

public abstract class AnnotationUtils {

    public static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return element.getAnnotation(annotationType);
    }

    public static <T extends Annotation> boolean hasAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return element.isAnnotationPresent(annotationType);
    }

    public static Annotation[] getAnnotations(AnnotatedElement element) {
        return element.getAnnotations();
    }

    public static <T extends Annotation> T getAnnotationRecursive(AnnotatedElement element, Class<T> annotationType) {
        T anno = getAnnotation(element, annotationType);
        if (anno == null) {
            Annotation[] annotations = getAnnotations(element);
            if (annotations == null || annotations.length == 0)
                return null;

            for (Annotation annotation : annotations) {
                Class type = annotation.annotationType();
                if (type == Retention.class || type == Target.class || type == Documented.class)
                    continue;

                anno = getAnnotationRecursive(type, annotationType);
                if (anno != null)
                    return anno;
            }
        }

        return anno;
    }

}

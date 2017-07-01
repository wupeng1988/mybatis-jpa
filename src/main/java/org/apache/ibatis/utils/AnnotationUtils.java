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

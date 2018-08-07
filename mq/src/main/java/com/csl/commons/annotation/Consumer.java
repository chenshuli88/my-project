package com.csl.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Consumer {
    String group() default "";

    String topic();

    String tag();

    String[] tags() default {};

    String filter() default "";

    boolean orderly() default false;

    int batchSize() default 1;

    int maxReconsumeTimes() default 0;

    boolean recomsumeAll() default false;
}

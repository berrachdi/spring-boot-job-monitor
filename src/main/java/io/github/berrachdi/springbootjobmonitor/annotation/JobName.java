package io.github.berrachdi.springbootjobmonitor.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobName {
    String value();
}

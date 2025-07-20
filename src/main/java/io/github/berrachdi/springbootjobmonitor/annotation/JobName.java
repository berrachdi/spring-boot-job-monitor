package io.github.berrachdi.springbootjobmonitor.annotation;

import java.lang.annotation.*;

/**
 * Annotation to specify the name of a job.
 * This annotation can be used on methods to indicate the name of the job being executed.
 * The value of the annotation should be the name of the job.
 *
 * @author Mohamed Berrachdi
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobName {
    String value();
}

package com.qg.memori.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SqlInfo {
    boolean id() default false; // id are longs
}

package com.andframe.annotation.view;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 注解式绑定控件<br>
 *  View.OnTouchListener
 *  @ BindClick(R.id.viewId)
 *  boolean onTouch(View v, MotionEvent event) {
 *  }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindTouch {
    @IdRes int[] value();
}


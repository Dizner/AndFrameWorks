package com.andframe.api.task.handler;

/**
 * 任务
 * Created by SCWANG on 2016/10/13.
 */

public interface LoadSuccessHandler<T> {

    void onSuccess(T model);

}

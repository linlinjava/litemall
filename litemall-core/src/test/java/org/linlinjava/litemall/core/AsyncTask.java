package org.linlinjava.litemall.core;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTask {
    @Async
    public void asyncMethod() {
        System.out.println("Execute method asynchronously. "
                + Thread.currentThread().getName());
    }

    public void nonasyncMethod() {
        System.out.println("Execute method nonasynchronously. "
                + Thread.currentThread().getName());
    }
}

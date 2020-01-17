package org.linlinjava.litemall.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTask {
    private final Log logger = LogFactory.getLog(AsyncTask.class);

    @Async
    public void asyncMethod() {
        logger.info("Execute method asynchronously. "
                + Thread.currentThread().getName());
    }

    public void nonasyncMethod() {
        logger.info("Execute method nonasynchronously. "
                + Thread.currentThread().getName());
    }
}

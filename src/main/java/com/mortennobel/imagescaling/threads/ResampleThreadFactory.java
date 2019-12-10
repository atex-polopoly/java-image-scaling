package com.mortennobel.imagescaling.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * ResampleThreadFactory
 *
 * @author mnova
 */
public class ResampleThreadFactory implements ThreadFactory {

    private final ThreadFactory threadFactory;

    public ResampleThreadFactory() {
        this(Executors.defaultThreadFactory());
    }

    public ResampleThreadFactory(final ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = threadFactory.newThread(r);
        thread.setName("resample-" + thread.getName());
        return thread;
    }
}

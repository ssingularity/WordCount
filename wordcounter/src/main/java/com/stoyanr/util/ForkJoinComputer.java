/*
 * $Id: $
 *
 * Copyright 2012 Stoyan Rachev (stoyanr@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stoyanr.util;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinComputer<T> {
    
    private static final int PAR_LEVEL = Runtime.getRuntime().availableProcessors();
    
    public interface Computer<T> {
        T compute(int lo, int hi);
    }
    
    public interface Merger<T> {
        T merge(T result1, T result2);
    }
    
    private final int size;
    private final int threshold;
    private final Computer<T> computer;
    private final Merger<T> merger;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    
    public ForkJoinComputer(int size, int threshlod, Computer<T> computer, Merger<T> merger) {
        this.size = size;
        this.threshold = threshlod;
        this.computer = computer;
        this.merger = merger;
    }

    public T compute() {
        return forkJoinPool.invoke(new Task(0, size));
    }

    @SuppressWarnings("serial")
    private final class Task extends RecursiveTask<T> {

        private final int lo;
        private final int hi;

        Task(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected T compute() {
            logStarting();
            T result;
            if (hi - lo <= Math.max(size / PAR_LEVEL, threshold)) {
                result = computer.compute(lo, hi);
            } else {
                int mid = (lo + hi) >>> 1;
                Task t1 = new Task(lo, mid);
                t1.fork();
                Task t2 = new Task(mid, hi);
                T r2 = t2.compute();
                T r1 = t1.join();
                result = merger.merge(r1, r2);
            }
            logFinished();
            return result;
        }

        private void logStarting() {
            if (Logger.isDebug()) {
                Logger.debug("[Task %d - %d (%s)] Starting ...", lo, hi, getThreadName());
            }
        }

        private void logFinished() {
            if (Logger.isDebug()) {
                Logger.debug("[Task %d - %d (%s)] Finished", lo, hi, getThreadName());
            }
        }
    }

    private static String getThreadName() {
        return Thread.currentThread().getName();
    }
    
}
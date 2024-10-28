package com.yune.utils;

public interface Ilock {
     boolean tryLock(Long timeout);

    void unlock();
}

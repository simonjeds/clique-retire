package com.clique.retire.util;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseHelper {

    @Transactional(rollbackFor = Exception.class)
    public <T> T executeInTransaction(Supplier<T> entityManagerConsumer) {
        return entityManagerConsumer.get();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void executeInTransaction(Runnable entityManagerConsumer) {
        entityManagerConsumer.run();
    }

}
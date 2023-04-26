/*
package com.programvaruprojekt.springbatchtutorial.classifier;

import com.programvaruprojekt.springbatchtutorial.model.RemovedTransaction;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class TransactionClassifier implements Classifier<RemovedTransaction, ItemWriter<? super RemovedTransaction>> {
    private final ItemWriter<RemovedTransaction> writerA;
    private final ItemWriter<RemovedTransaction> writerB;

    public TransactionClassifier(ItemWriter<RemovedTransaction> writerA, ItemWriter<RemovedTransaction> writerB) {
        this.writerA = writerA;
        this.writerB = writerB;
    }

    @Override
    public ItemWriter<? super RemovedTransaction> classify(RemovedTransaction output) {
        if (*/
/* your condition based on output *//*
) {
            return writerA;
        } else {
            return writerB;
        }
    }
}
*/

/*
package com.programvaruprojekt.springbatchtutorial.classifier;
import com.programvaruprojekt.springbatchtutorial.model.RemovedPerson;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;
public class PersonClassifier implements Classifier<RemovedPerson, ItemWriter<? super RemovedPerson>> {
    private final ItemWriter<RemovedPerson> writerA;
    private final ItemWriter<RemovedPerson> writerB;

    public PersonClassifier(ItemWriter<RemovedPerson> writerA, ItemWriter<RemovedPerson> writerB) {
        this.writerA = writerA;
        this.writerB = writerB;
    }

    @Override
    public ItemWriter<? super YourOutputType> classify(YourOutputType output) {
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

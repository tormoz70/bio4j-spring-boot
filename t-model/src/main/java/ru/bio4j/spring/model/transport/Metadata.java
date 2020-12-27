package ru.bio4j.spring.model.transport;

import ru.bio4j.spring.model.transport.jstore.StoreMetadata;

import java.io.Serializable;
import java.util.Objects;

public class Metadata implements Serializable {
    private StoreMetadata dataset;
    private ABean createUpdateObject;

    public StoreMetadata getDataset() {
        return dataset;
    }

    public void setDataset(StoreMetadata dataset) {
        this.dataset = dataset;
    }

    public ABean getCreateUpdateObject() {
        return createUpdateObject;
    }

    public void setCreateUpdateObject(ABean createUpdateObject) {
        this.createUpdateObject = createUpdateObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(dataset, metadata.dataset) &&
                Objects.equals(createUpdateObject, metadata.createUpdateObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataset, createUpdateObject);
    }
}

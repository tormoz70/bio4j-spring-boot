package ru.bio4j.spring.commons.serializers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DoubleDataObject {
    private Double bdProp3;
    private Double bdProp0;
    private Double bdProp_2;
    private Double bdProp3Exact;
    private Double bdProp3Min1;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(3)
    public Double getBdProp3() {
        return bdProp3;
    }

    public void setBdProp3(Double bdProp) {
        this.bdProp3 = bdProp;
    }

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(0)
    public Double getBdProp0() {
        return bdProp0;
    }

    public void setBdProp0(Double bdProp0) {
        this.bdProp0 = bdProp0;
    }

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(-2)
    public Double getBdProp_2() {
        return bdProp_2;
    }

    public void setBdProp_2(Double bdProp_2) {
        this.bdProp_2 = bdProp_2;
    }

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(value = 3, exact = true)
    public Double getBdProp3Exact() {
        return bdProp3Exact;
    }

    public void setBdProp3Exact(Double bdProp3Exact) {
        this.bdProp3Exact = bdProp3Exact;
    }

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(value = 3, minPrecision = 1)
    public Double getBdProp3Min1() {
        return bdProp3Min1;
    }

    public void setBdProp3Min1(Double bdProp3Min1) {
        this.bdProp3Min1 = bdProp3Min1;
    }
}

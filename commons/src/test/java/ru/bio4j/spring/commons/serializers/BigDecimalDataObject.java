package ru.bio4j.spring.commons.serializers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

public class BigDecimalDataObject {
    private BigDecimal bdProp3;
    private BigDecimal bdProp0;
    private BigDecimal bdProp_2;
    private BigDecimal bdProp3Exact;
    private BigDecimal bdProp3Min1;

    @JsonSerialize(using = BigDecimalContextualSerializer.class)
    @Precision(3)
    public BigDecimal getBdProp3() {
        return bdProp3;
    }

    public void setBdProp3(BigDecimal bdProp) {
        this.bdProp3 = bdProp;
    }

    @JsonSerialize(using = BigDecimalContextualSerializer.class)
    @Precision(0)
    public BigDecimal getBdProp0() {
        return bdProp0;
    }

    public void setBdProp0(BigDecimal bdProp0) {
        this.bdProp0 = bdProp0;
    }

    @JsonSerialize(using = BigDecimalContextualSerializer.class)
    @Precision(-2)
    public BigDecimal getBdProp_2() {
        return bdProp_2;
    }

    public void setBdProp_2(BigDecimal bdProp_2) {
        this.bdProp_2 = bdProp_2;
    }

    @JsonSerialize(using = BigDecimalContextualSerializer.class)
    @Precision(value = 3, exact = true)
    public BigDecimal getBdProp3Exact() {
        return bdProp3Exact;
    }

    public void setBdProp3Exact(BigDecimal bdProp3Exact) {
        this.bdProp3Exact = bdProp3Exact;
    }

    @JsonSerialize(using = BigDecimalContextualSerializer.class)
    @Precision(value = 3, minPrecision = 1)
    public BigDecimal getBdProp3Min1() {
        return bdProp3Min1;
    }

    public void setBdProp3Min1(BigDecimal bdProp3Min1) {
        this.bdProp3Min1 = bdProp3Min1;
    }
}

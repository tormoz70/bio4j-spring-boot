package ru.bio4j.spring.model.transport;

public class CrudOptions {
    private int recordsLimit = 0;
    private boolean forceCalcCount = false;
    private boolean appendMetadata = false;

    public static class  Builder {
        private int recordsLimit = 0;
        private boolean forceCalcCount = false;
        private boolean appendMetadata = false;

        public Builder recordsLimit(int value) {
            recordsLimit = value;
            return this;
        }
        public Builder forceCalcCount(boolean value) {
            forceCalcCount = value;
            return this;
        }
        public Builder appendMetadata(boolean value) {
            appendMetadata = value;
            return this;
        }
        public CrudOptions build() {
            CrudOptions rslt = new CrudOptions();
            rslt.recordsLimit = this.recordsLimit;
            rslt.forceCalcCount= this.forceCalcCount;
            rslt.appendMetadata = this.appendMetadata;
            return rslt;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getRecordsLimit() {
        return recordsLimit;
    }

    public boolean isForceCalcCount() {
        return forceCalcCount;
    }

    public boolean isAppendMetadata() {
        return appendMetadata;
    }

}

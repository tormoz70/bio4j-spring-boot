package ru.bio4j.spring.model.transport;

import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.List;

public class BeansPage<T> {
    private List<T> rows;
    private long paginationPage;
    private long paginationOffset;
    private long paginationCount;
    private long paginationPageSize;
    private long totalCount;
    private List<Field> metadata;
    private List<Total> totals;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getPaginationOffset() {
        return paginationOffset;
    }

    public void setPaginationOffset(long paginationOffset) {
        this.paginationOffset = paginationOffset;
    }

    public long getPaginationCount() {
        return paginationCount;
    }

    public void setPaginationCount(long paginationCount) {
        this.paginationCount = paginationCount;
    }

    public List<Field> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Field> metadata) {
        this.metadata = metadata;
    }

    public long getPaginationPage() {
        return paginationPage;
    }

    public void setPaginationPage(long paginationPage) {
        this.paginationPage = paginationPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPaginationPageSize() {
        return paginationPageSize;
    }

    public void setPaginationPageSize(long paginationPageSize) {
        this.paginationPageSize = paginationPageSize;
    }

    public List<Total> getTotals() {
        return totals;
    }

    public void setTotals(List<Total> totals) {
        this.totals = totals;
    }
}

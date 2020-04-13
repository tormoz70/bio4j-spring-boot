package ru.bio4j.spring.model.transport;

import ru.bio4j.spring.model.transport.jstore.Field;

import java.util.List;

public class BeansPage<T> {
    private List<T> rows;
    private int paginationPage;
    private int paginationOffset;
    private int paginationCount;
    private int paginationPageSize;
    private int totalCount;
    private List<Field> metadata;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getPaginationOffset() {
        return paginationOffset;
    }

    public void setPaginationOffset(int paginationOffset) {
        this.paginationOffset = paginationOffset;
    }

    public int getPaginationCount() {
        return paginationCount;
    }

    public void setPaginationCount(int paginationCount) {
        this.paginationCount = paginationCount;
    }

    public List<Field> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Field> metadata) {
        this.metadata = metadata;
    }

    public int getPaginationPage() {
        return paginationPage;
    }

    public void setPaginationPage(int paginationPage) {
        this.paginationPage = paginationPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPaginationPageSize() {
        return paginationPageSize;
    }

    public void setPaginationPageSize(int paginationPageSize) {
        this.paginationPageSize = paginationPageSize;
    }
}

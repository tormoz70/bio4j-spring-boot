package ru.bio4j.spring.model.transport;

import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.List;

/**
 * Набор данных для возврата клинету
 * @param <T> - bean описывающий запись
 */
public class BeansPage<T> {
    /**
     * Строки с данными
     */
    private List<T> rows;
    /**
     * Номер страницы
     */
    private long paginationPage;
    /**
     * Номер первой записи
     */
    private long paginationOffset;
    /**
     * Фактическое кол-во записей на данной странице
     */
    private long paginationCount;
    /**
     * Запрошенный размер страницы
     */
    private long paginationPageSize;
    /**
     * Общее число записей в наборе (если не известно, то здесь будет 999999999)
     * Реальное значение будет если последняя страница была достигнута, запрошены агрегаты, forceCount=true или запрос вернул все записи
     */
    private long totalCount;
    /**
     * Описание полей
     */
    private List<Field> metadata;
    /**
     * Агрегаты (если запрошено)
     */
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

    public static class Builder<T> {
        private List<T> rows;
        private long paginationPage;
        private long paginationOffset;
        private long paginationCount;
        private long paginationPageSize;
        private long totalCount;
        private List<Field> metadata;
        private List<Total> totals;

        public Builder rows(List<T> value) {
            rows = value;
            return this;
        }
        public Builder paginationPage(long value) {
            paginationPage = value;
            return this;
        }
        public Builder paginationOffset(long value) {
            paginationOffset = value;
            return this;
        }
        public Builder paginationCount(long value) {
            paginationCount = value;
            return this;
        }
        public Builder paginationPageSize(long value) {
            paginationPageSize = value;
            return this;
        }
        public Builder totalCount(long value) {
            totalCount = value;
            return this;
        }
        public Builder metadata(List<Field> value) {
            metadata = value;
            return this;
        }
        public Builder totals(List<Total> value) {
            totals = value;
            return this;
        }

        public BeansPage<T> build() {
            BeansPage<T> result = new BeansPage<>();
            result.setRows(rows);
            result.setPaginationPage(paginationPage);
            result.setPaginationOffset(paginationOffset);
            result.setPaginationCount(paginationCount);
            result.setPaginationPageSize(paginationPageSize);
            result.setTotalCount(totalCount);
            result.setMetadata(metadata);
            result.setTotals(totals);
            return result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

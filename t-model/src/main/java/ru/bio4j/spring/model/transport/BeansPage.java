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
}

package ru.bio4j.spring.common.model;


import ru.bio4j.spring.common.model.jstore.Sort;
import ru.bio4j.spring.common.model.jstore.filter.Filter;

import java.util.List;

public class FilterAndSorterHolder {
    private List<Sort> sorter;
    private Filter filter;

    public List<Sort> getSorter() {
        return sorter;
    }

    public void setSorter(List<Sort> sorter) {
        this.sorter = sorter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}

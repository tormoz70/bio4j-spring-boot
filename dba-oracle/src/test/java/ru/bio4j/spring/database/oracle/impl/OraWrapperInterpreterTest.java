package ru.bio4j.spring.database.oracle.impl;

import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.oracle.OraWrapperInterpreter;
import ru.bio4j.spring.model.transport.FilterAndSorter;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.io.InputStream;

public class OraWrapperInterpreterTest {

//    @Test(enabled = false)
//    public void filterToSQLTest() throws Exception {
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter.json");
//        String json = Utl.readStream(inputStream);
//        Filter filter = Jsons.decode(json, Filter.class);
//        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
//        String sql = filterWrapper.filterToSQL("fff", filter);
//        Assert.assertNotNull(sql);
//    }


//    @Test(enabled = true)
//    public void filter1ToSQLTest() throws Exception {
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter1.json");
//        String json = Utl.readStream(inputStream);
//        Filter filter = Jsons.decodeFilter(json);
//        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
//        String sql = filterWrapper.filterToSQL("fff", (Filter)filter);
//        Assert.assertNotNull(sql);
//    }
    @Test
    public void filterAndSorterToSQLTest() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter2.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jecksons.getInstance().decodeFilterAndSorter(json);
        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        Assert.assertNotNull(sql);
    }

    @Test
    public void filterAndSorterToSQLTest1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter3.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jecksons.getInstance().decodeFilterAndSorter(json);
        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        Assert.assertNotNull(sql);
    }

}

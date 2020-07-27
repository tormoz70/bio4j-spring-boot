package ru.bio4j.spring.dba;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.FilterAndSorter;
import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class UTool {
    private static final LogWrapper LOG = LogWrapper.getLogger(UTool.class);

    public static List<Param> extractBioParams(final BioQueryParams queryParams) {
        Paramus.setQueryParamsToBioParams(queryParams);
        return queryParams.bioParams;
    }

    public static FilterAndSorter createFilterAndSorter(final BioQueryParams queryParams) {
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                fs = Jecksons.getInstance().decodeFilterAndSorter(queryParams.jsonData);
            } catch (Exception e) {
                LOG.debug(String.format("Ошибка при восстановлении объекта %s. Json: %s", FilterAndSorter.class.getSimpleName(), queryParams.jsonData), e);
            }
        }
        if(fs == null) {
            fs = new FilterAndSorter();
            if(queryParams.sort != null) {
                fs.setSorter(new ArrayList<>());
                fs.getSorter().addAll(queryParams.sort);
            }
            fs.setFilter(queryParams.filter);
        }
        return fs;
    }

}

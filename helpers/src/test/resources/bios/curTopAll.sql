with :sortord as sortord
select * from (
    with :rlocale as rlocale
    select dd.pu_num as id, case when rlocale = 'en' then k.name_eng else k.name_rus end as title, k.name_eng as title_en, k.prod_year as myear
        ,case when rlocale = 'en' then k.madein_eng else k.madein end as country, k.madein_eng as country_en
        ,case when rlocale = 'en' then k.studia_eng else k.studia end as owner, k.studia_eng as owner_en
        ,case when rlocale = 'en' then k.annotxt_eng else k.annotxt end as description, k.annotxt_eng as description_en
        ,launch_date
        ,case when rlocale = 'en' then k.genre_eng else k.genre end as genre, k.genre_eng, k.kgenres as genreids
        ,case when rlocale = 'en' then k.view_restr_eng else k.view_restr end as view_restr, k.view_restr_eng as view_restr_en
        ,k.age_restr, k.forecastfk, k.forecastdist, k.forecastbk, k.forecast4
        ,case when rlocale = 'en' then k.mdirector_eng else k.mdirector end as mdirector, k.mdirector_eng as mdirector_en
        ,case when rlocale = 'en' then k.mscreenwriter_eng else k.mscreenwriter end as mscreenwriter, k.mscreenwriter_eng as mscreenwriter_en
        ,case when rlocale = 'en' then k.mproducer_eng else k.mproducer end as mproducer, k.mproducer_eng as mproducer_en
        ,case when rlocale = 'en' then k.mdistributor_eng else k.mdistributor end as mdistributor, k.mdistributor_eng as mdistributor_en
        ,case when rlocale = 'en' then k.mstarring_eng else k.mstarring end as mstarring, k.mstarring_eng as mstarring_en
        ,toFloat32OrZero(k.mrating) as mrating
        ,money0, money0_en, money0_eu, money0_ru, tickets0, seances0
        ,presales, presales_en, presales_eu, presales_ru, presalesstart, presalesend
        ,launchdatesales, launchdatesales_en, launchdatesales_eu, launchdatesales_ru
        ,first_weed_start, first_weed_end, first_weed_summ, first_weed_summ_en, first_weed_summ_eu, first_weed_summ_ru
        ,second_weed_start, second_weed_end, second_weed_summ, second_weed_summ_en, second_weed_summ_eu, second_weed_summ_ru
        ,sale_end
        ,case when rlocale = 'en' then k.poster_url_big_eng else k.poster_url_big end as poster_url_big, k.poster_url_big_eng as poster_url_big_en
        ,case when rlocale = 'en' then k.poster_url_small_eng else k.poster_url_small end as poster_url_small, k.poster_url_small_eng as poster_url_small_en
        ,isfollowed
        ,dictGet('distribs', 'anames_en', tuple(pu_num)) as distribs_en
        ,case when rlocale = 'en' then distribs_en else dictGet('distribs', 'anames', tuple(pu_num)) end as distribs
        ,dictGet('distribs', 'distrib_ids', tuple(pu_num)) as distribids
        ,showdays, previewFrom, previewTo, previewSumm, previewSumm1, previewSumm2, previewSumm3, previewSumm4, previewTickets, previewSessions
    from (
        with toDate(0) as emptyDate
        ,:currId as currId
        ,:userOrgId as userOrgId
        ,splitByChar(',', :userRole) as usrroles
        ,array('5','6','8') as rolesset1
        ,array('7') as rolesset2
        select nation_id, pu_num, startdate as launch_date
            ,startsumm
            ,toInt64(multiIf(currId = 0, startsumm, currId = 1, startsumm1, currId = 2, startsumm2, currId = 3, startsumm3, startsumm) / 100) as launchdatesales
            ,toInt64(startsumm1 / 100) as launchdatesales_en, toInt64(startsumm2 / 100) as launchdatesales_eu, toInt64(startsumm3 / 100) as launchdatesales_ru
            ,toInt64(multiIf(currId = 0, summttl, currId = 1, summ1ttl, currId = 2, summ2ttl, summttl) / 100) as money0
            ,toInt64(summ1ttl / 100) as money0_en, toInt64(summ2ttl / 100) as money0_eu, toInt64(summ3ttl / 100) as money0_ru, tcktsttl as tickets0, sessttl as seances0
            ,dictGet('weekendsper', 'datestart', toUInt64(weids[1])) as first_weed_start
            ,case when weids[1] != 0 then toDateTime(dictGet('weekendsper', 'dateend', toUInt64(weids[1])) + 1) - 1 else emptyDate end as first_weed_end
            ,toInt64(multiIf(currId = 0, weed_summ[1], currId = 1, weed_summ1[1], currId = 2, weed_summ2[1], currId = 3, weed_summ3[1],  weed_summ[1]) / 100) as first_weed_summ
            ,toInt64(weed_summ1[1]  /100) as first_weed_summ_en, toInt64(weed_summ2[1] / 100) as first_weed_summ_eu, toInt64(weed_summ3[1] / 100) as first_weed_summ_ru
            ,dictGet('weekendsper', 'datestart', toUInt64(weids[2])) as second_weed_start
            ,case when weids[2] != 0 then toDateTime(dictGet('weekendsper', 'dateend', toUInt64(weids[2])) + 1) - 1 else emptyDate end as second_weed_end
            ,toInt64(multiIf(currId = 0, weed_summ[2], currId = 1, weed_summ1[2], currId = 2, weed_summ2[2], currId = 3, weed_summ3[2],  weed_summ[2]) / 100) as second_weed_summ
            ,toInt64(weed_summ1[2]  /100) as second_weed_summ_en, toInt64(weed_summ2[2] / 100) as second_weed_summ_eu, toInt64(weed_summ3[2] / 100) as second_weed_summ_ru
            ,sale_end, orgs, srooms, showdays, isfollowed
            ,previewFrom, previewTo
            ,multiIf(currId = 0, prevSumm, currId = 1, prevSumm1, currId = 2, prevSumm2, currId = 3, prevSumm3, prevSumm) / 100 as previewSumm
            ,prevSumm1 / 100 as previewSumm1, prevSumm2 / 100 as previewSumm2, prevSumm3 / 100 as previewSumm3, prevSumm4 / 100 as previewSumm4
            ,previewTickets, previewSessions
            ,toInt64(multiIf(currId = 0, presaleSumm, currId = 1, presaleSumm1, currId = 2, presaleSumm2, currId = 3, presaleSumm3, presaleSumm) / 100) as presales
            ,toInt64(presaleSumm1 / 100) as presales_en, toInt64(presaleSumm2 / 100) as presales_eu, toInt64(presaleSumm3 / 100) as presales_ru
            ,case when sale_start >= launch_date then emptyDate else sale_start end as presalesstart
            ,case when launch_date = emptyDate or sale_start >= launch_date then emptyDate else launch_date-1 end as presalesend
        from (
            with toDate(0) as emptyDate
            select nation_id, pu_num, any(startdate) as startdate
                ,toInt64(sum(tckts_all)) as tcktsttl, toInt64(sum(sess_all)) as sessttl
                ,sum(summ_all) as summttl, sum(summ1_all) as summ1ttl, sum(summ2_all) as summ2ttl, sum(summ3_all) as summ3ttl, sum(summ4_all) as summ4ttl
                ,sum(presumm) as presaleSumm, sum(presumm1) as presaleSumm1, sum(presumm2) as presaleSumm2, sum(presumm3) as presaleSumm3, sum(presumm4) as presaleSumm4
                ,sum(startsumm) as startsumm, sum(startsumm1) as startsumm1, sum(startsumm2) as startsumm2, sum(startsumm3) as startsumm3, sum(startsumm4) as startsumm4
                ,uniqExactMerge(orgsState) as orgs
                ,uniqExactMerge(sroomsState) as srooms
                ,sum(showdays) as showdays
                ,max(isfollowed) as isfollowed
                ,min(sale_start) as sale_start
                ,max(sale_end) as sale_end
                ,groupArray(dictGetDate('weekendsper', 'dateend', toUInt64(wid))) as weendall
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(wid) as weidsall, weendall), 1, 2) as weids
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(summ_as), weendall), 1, 2) as weed_summ
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(summ1_as), weendall), 1, 2) as weed_summ1
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(summ2_as), weendall), 1, 2) as weed_summ2
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(summ3_as), weendall), 1, 2) as weed_summ3
                ,arraySlice(arrayFilter((x, y) -> startdate != emptyDate and y >= startdate, groupArray(summ4_as), weendall), 1, 2) as weed_summ4
                ,any(previewFrom) as previewFrom, any(previewTo) as previewTo
                ,sum(previewSumm) as prevSumm, sum(previewSumm1) as prevSumm1, sum(previewSumm2) as prevSumm2, sum(previewSumm3) as prevSumm3, sum(previewSumm4) as prevSumm4
                ,sum(previewTickets) as previewTickets, sum(previewSessions) as previewSessions
            from (
                with toDate(0) as emptyDate
                select
                    a.nation_id, a.pu_num
                    -- общее кол-во билетов, сеансов и суммы
                    ,sum(a.tckts) as tckts_all, sum(a.sess) as sess_all
                    ,sum(a.summ) as summ_all, sum(a.summ1) as summ1_all, sum(a.summ2) as summ2_all, sum(a.summ3) as summ3_all, sum(a.summ4) as summ4_all
                    ,min(a.sale_start) as sale_start, max(a.sale_end) as sale_end
                    -- суммы предпродаж
                    ,sum(a.presumm) as presumm, sum(a.presumm1) as presumm1, sum(a.presumm2) as presumm2, sum(a.presumm3) as presumm3, sum(a.presumm4) as presumm4
                    ,dictGet('kinos', 'startdate', tuple(a.pu_num)) as startdateStr, toDate(case when notEmpty(startdateStr) then startdateStr else '0000-00-00' end) as startdate
                    -- суммы в день старта проката
                    ,sumIf(a.summ, cast(a.show_date, 'Date') = startdate) as startsumm
                    ,sumIf(a.summ1, cast(a.show_date, 'Date') = startdate) as startsumm1
                    ,sumIf(a.summ2, cast(a.show_date, 'Date') = startdate) as startsumm2
                    ,sumIf(a.summ3, cast(a.show_date, 'Date') = startdate) as startsumm3
                    ,sumIf(a.summ4, cast(a.show_date, 'Date') = startdate) as startsumm4
                    -- суммы не раньше старта проката
                    ,sumIf(a.summ, startdate != emptyDate and startdate <= cast(a.show_date, 'Date')) as summ_as
                    ,sumIf(a.summ1, startdate != emptyDate and startdate <= cast(a.show_date, 'Date')) as summ1_as
                    ,sumIf(a.summ2, startdate != emptyDate and startdate <= cast(a.show_date, 'Date')) as summ2_as
                    ,sumIf(a.summ3, startdate != emptyDate and startdate <= cast(a.show_date, 'Date')) as summ3_as
                    ,sumIf(a.summ4, startdate != emptyDate and startdate <= cast(a.show_date, 'Date')) as summ4_as
                    ,uniqExactState(a.org_id) as orgsState
                    ,uniqExactState(a.sroom_id) as sroomsState
                    ,uniqExactIf(cast(a.show_date, 'Date'), startdate != emptyDate and cast(a.show_date, 'Date') >= startdate and cast(a.show_date, 'Date') <= today()) as showdays
                    ,maxIf(1, cast(a.show_date, 'Date') >= startdate and cast(a.show_date, 'Date') between yesterday() and today()) as isfollowed
                    ,dictGet('pfpreview', 'prefrom', tuple(a.pu_num)) as previewFromStr, toDate(case when notEmpty(previewFromStr) then previewFromStr else '0000-00-00' end) as previewFrom
                    ,dictGet('pfpreview', 'preto', tuple(a.pu_num)) as previewToStr, toDate(case when notEmpty(previewToStr) then previewToStr else '0000-00-00' end) as previewTo
                    -- суммы предпоказов
                    ,sumIf(a.summ, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSumm
                    ,sumIf(a.summ1, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSumm1
                    ,sumIf(a.summ2, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSumm2
                    ,sumIf(a.summ3, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSumm3
                    ,sumIf(a.summ4, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSumm4
                    ,sumIf(a.tckts, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewTickets
                    ,sumIf(a.sess, cast(a.show_date, 'Date') between previewFrom and previewTo) as previewSessions
                    ,dictGetUInt32('weekendsday', 'id', tuple(cast(a.show_date, 'Date'))) as wid
                  from cub6.data0_three a
                 prewhere dictHas('kinos', tuple(a.pu_num))
                      and not dictHas('pucommonpus', tuple(a.pu_num))
                  group by a.nation_id, a.pu_num, wid
                 order by a.nation_id, a.pu_num, wid
            )
            group by nation_id, pu_num
            order by nation_id, pu_num
        )
        where nation_id = :nationId
          and dictGet('kinos', 'mobpub', tuple(pu_num)) = 1
          and (usrroles is null
              or hasAny(usrroles, rolesset1)
              or hasAny(usrroles, rolesset2) and pu_num is not null and dictGet('org_film_perm', 'org_id', tuple(assumeNotNull(pu_num))) = toUInt16OrNull(userOrgId)
              )
    ) dd
    ANY LEFT JOIN cub6.kinos k USING(pu_num) SETTINGS join_use_nulls = 1
)
order by multiIf(sortord = 'sum', money0, sortord = 'quantity', tickets0, sortord = 'sessions', seances0, sortord = 'rating', toInt16(mrating * 1000), money0) desc

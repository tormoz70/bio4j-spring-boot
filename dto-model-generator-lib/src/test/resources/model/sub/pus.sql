with ooo as (
    select a.pu_number, '['||a.org_id||']-'||o.orgname as orgname
    from fk_api.orgm$org2film a
             inner join org$r o on o.id_org = a.org_id
    where a.org_id = to_number(:p_sys_curusr_org_uid)
      and (trunc(a.date_from) <= trunc(sysdate) and (a.date_to is null or trunc(a.date_to) >= trunc(sysdate)))
)
   ,ooo1 as (
    select pu_number, listagg(orgname,', ') within group(order by orgname) owners from ooo group by pu_number
)
select a0.pu_id, a0.film_id, a0.pu_num, a0.issued, pf.film_category, a0.rent_category, a0.rent_end, a0.rent_desc,
       a0.distributors, o.owners, a0.contr_num, a0.contr_date, a0.cre_date,
       pf.name_rus as rent_name, pf.name_orig_mnl as name_orig, pf.madein_orig_mnl as madein_orig, pf.lang_orig,
       pf.prod_year, pf.madein, pf.studia, pf.genre, pf.age_restr, pf.view_restr,
       pf.mproducer, pf.mscrwriters, pf.mdirector, pf.mcomposer, pf.moperator, pf.martist,
       a0.pu_num || ' - ' || pf.name_rus as pu_desc,
       (case when a0.pu_id < 100000000 and a0.orig_pu_id is null then 1 else 0 end) editable,
       null as pu_num_perm, null as date_from_perm, null as date_to_perm,
       a0.startdate, a0.distribs, a0.distrib_ids, a0.site_check,
       format_duration(pf.dur_hours, pf.dur_mins) as fduration, nvl(pf.dur_hours, 0) * 60 + nvl(pf.dur_mins, 0) as mduration, pf.sernum,
       a0.pogu_id,
       a0.deleted, a0.deleted_date,
       pf.notetxt,
       prvw.prefrom,prvw.preto,
       pf.name_orig as name_readonly, pf.madein as madein_readonly,
       pf.mdirector as mdirector_readonly, pf.mscrwriters as mscrwriters_readonly,
       pf.mproducer as mproducer_readonly, pf.view_restr as view_restr_readonly
from fk_nsi.pfilmnums a0
         inner join fk_nsi.pfilms pf on pf.film_id = a0.film_id --and pf.deleted = 0
         left outer join ooo1 o on o.pu_number = a0.pu_num
         left outer join GIVC_CUB.PFPREVIEW prvw ON prvw.pu_num = a0.pu_num and prvw.deleted = 0
where --a0.deleted = 0 and
    ((checkroles(:p_sys_curusr_roles,'1,2,6')=1) or
     ((a0.pu_id > 100000000) or (a0.pu_id < 100000000 and a0.pogu_id is not null))
        )
  and
    ((checkroles(:p_sys_curusr_roles,'1,2,6')=1 and a0.deleted in (0,1)) or
     (checkroles(:p_sys_curusr_roles,'1,2,6')=0 and a0.deleted in (0))
        )
  and (
        ((nvl(:onlymanual, 0) = 0) or (:onlymanual = 1 and a0.pu_id < 100000000))
        /*@{cutempty}*/and (upper(pf.name_rus) like '%'||upper(:filter)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (trunc(a0.issued) >= :issued_from)/*{cutempty}@*/
    /*@{cutempty}*/and (trunc(a0.issued) < to_date(:issued_to)+1)/*{cutempty}@*/
    /*@{cutempty}*/and (upper(a0.pu_num) like upper(:pu_num)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.name_rus) like '%'||upper(:rent_name)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.prod_year) like '%'||upper(:prod_year)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.madein) like '%'||upper(:madein)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.studia) like '%'||upper(:studia)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.genre) like '%'||upper(:genre)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.view_restr) like '%'||upper(:view_restr)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(a0.distributors) like '%'||upper(:distributors)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(pf.film_category) like '%'||upper(:film_category)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(a0.rent_category) like '%'||upper(:rent_category)||'%')/*{cutempty}@*/
    /*@{cutempty}*/and (upper(a0.distribs) like '%'||upper(:distribs)||'%')/*{cutempty}@*/
    )
--order by a0.issued desc NULLS LAST

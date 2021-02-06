with
sroomss as (
    SELECT a.org_id,
           LISTAGG(a.sroom_name, ';') WITHIN GROUP (ORDER BY a.sroom_name) srooms,
               LISTAGG(a.seatings, ';') WITHIN GROUP (ORDER BY a.sroom_name) places
    FROM ORGM$SHOWROOMS0 a
    GROUP BY a.org_id
)
,srooms_props as (
    SELECT a.org_id,
        count(distinct case when a.fksupported <> '0' then a.showr_id end) as sroom_fk_cnt,
        count(distinct case when a.digital = '1' then a.showr_id end) as sroom_digital_cnt,
        count(distinct case when a.autosroom = '1' then a.showr_id end) as autosroom_cnt,
        count(distinct case when a.showrstat_id = '1' then a.showr_id end) as sroom_paused_cnt,
        count(distinct case when a.showrstat_id = '2' then a.showr_id end) as sroom_closed_cnt,
        count(distinct case when a.digital = '0' and a.showrstat_id = '0' then a.showr_id end) as sroom_nodgtl_wrkng_cnt,
        count(distinct case when a.digital = '1' and a.showrstat_id = '0' then a.showr_id end) as sroom_digital_wrkng_cnt
    FROM showroom_act a
    GROUP BY a.org_id
)
SELECT
    o1.orgname,
    o1.id_org,
    case o1.orgtype
        when 'l' then 'Киносеть'
        when 'h' then 'Киносеть'
        when 's' then 'Некоммерческая киносеть'
        when 'b' then 'Некоммерческая киносеть'
        when 'p' then 'Кинотеатр'
        end as orgtype_name,
    o1.time_zone,
    o1.ownership,
    o1.holding_id,
    o1.hldname,
    o1.region,
    o1.nas_punkt,
    pp.people_cnt,
    o1.street_name,
    o1.email,
    o1.email_hld,
    o1.automatic,
    o1.automatic_str,
    o1.modern,
    o1.isonline,
    o1.original_dog,
    o1.original_dog_str,
    o1.isreported,
    o1.isreported_str,
    o1.dataisvalid_str,
    o1.whereisdata,
    o1.last_upld,
    o1.test,
    o1.registred,
    o1.test_str,
    o1.liquidate,
    o1.dog_num,
    o1.dog_date,
    o1.reg_date,
    o1.locname,
    o1.state,
    o1.statename,
    o1.state_date,
    o1.closereason_id,
    o1.closereason_name,
    o1.respons_person,
    o1.phone_fax,
    o1.id_k1,
    o1.id_k1_str,
    o1.id_k2,
    o1.id_k2_str,
    o1.id_k4,
    o1.id_k4_str,
    o1.id_3d,
    o1.id_3d_str,
    o1.imax,
    o1.imax_str,
    o1.id_plenka,
    o1.id_plenka_str,
    o1.analogsnd,
    o1.analogsnd_str,
    o1.digitsnd,
    o1.digitsnd_str,
    o1.vndname,
    o1.orgname_jur,
    o1.suspended,
    case o1.verstate when  '0' then 'не выверен'
                     when '1' then 'в процессе'
                     when '2' then 'адрес выверен'
                     when '9' then 'выверен'
        end as verstatename,
    o1.sroom_cnt,
    o1.splace_cnt,
    o1.space_ttl,
    o1.recid_io,
    o1.silence_period,
    o1.last_chng_usr,
    o1.last_chng_date,
    o1.last_chng_act,
    o1.comments,
    o1.post_index,
    o1.address_jur,
    o1.ogrn,
    o1.inn,
    o1.autoinfo_phone,
    o1.org_www,
    sr.srooms,
    sr.places,
    o1.emails,
    o1.mngmntform_id,
    o1.mngmntform_caption,
    o1.subsidyear,
    o1.nation_id,
    o1.nation_name,
    o1.fksupported,
    o1.npculture,
    o1.operator_state_id,
    (case when o1.operator_state_id = '1' then 'Закрыт' else o1.statename end) as operator_state_id_str,
    (
        select listagg(to_char(a.ref_org_id, 'FM99999999999999'), ', ') within group (order by a.ref_org_id)
        from(
            select b.ref_org_id, b.org_id
            from fk_org.org2org b
            union
            select c.org_id, c.ref_org_id
            from fk_org.org2org c
            union
            select d.org_id, d.org_id
            from fk_org.org2org d
            union
            select e.ref_org_id, e.ref_org_id
            from fk_org.org2org e
        ) a
        where a.org_id = o1.id_org
    ) as org2orgrefs,
    fdstr.aname as fdstr_aname,
    tkladr_separate.tkldr_name np_separate_name,
    tkladr_separate.sname np_separate_type,
    sp.sroom_fk_cnt,
    sp.sroom_digital_cnt,
    sp.autosroom_cnt,
    sp.sroom_paused_cnt,
    sp.sroom_closed_cnt,
    sp.sroom_nodgtl_wrkng_cnt,
    sp.sroom_digital_wrkng_cnt
FROM org$cinemalst o1
    left outer join sroomss sr on sr.org_id = o1.id_org
    left outer join FK_REG.POP$CITIES pp on pp.tkldr_uid = o1.kladr_code_np
    left outer join (
    SELECT a.tkldr_uid, a.fdistrict_id, a.nation_id, b.aname
    FROM fk_reg.regfo a
    INNER JOIN fk_nsi.fdistrict b ON a.fdistrict_id = b.fdistrict_id
    ) fdstr ON o1.kladr_code_r = fdstr.tkldr_uid
    left join (
    select b.tkldr_uid, b.tkldr_name, c.sname
    from fk_reg.tkladr b
    inner join fk_reg.tkladrtp c ON b.tkldrtp_id = c.tkldrtp_id
    ) tkladr_separate ON tkladr_separate.tkldr_uid = o1.kladr_code_np
    left join srooms_props sp ON sp.org_id = o1.id_org
WHERE  o1.orgtype in ('p', 's', 'l')
  AND (checkroles(:p_sys_curusr_roles,'1,2,5,6,10,11')=1)
  AND (checkroles(:p_sys_curusr_roles,'11')=0 or (checkroles(:p_sys_curusr_roles,'11')=1 and o1.fksupported = '1'))
 /*@{cutempty}*/AND (o1.holding_id = :parent_org_id)/*{cutempty}@*/
  AND
    (
     /*@{cutempty}*/(o1.nation_id = :nation_id) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.kladr_code_r, 'not_assigned') = :region_id) and/*{cutempty}@*/
     /*@{cutempty}*/(upper(o1.nas_punkt) like '%'||upper(:nas_punkt)||'%') and/*{cutempty}@*/
     /*@{cutempty}*/(upper(o1.street_name) like '%'||upper(:street_name)||'%') and/*{cutempty}@*/
     /*@{cutempty}*/((o1.holding_id = nvl(to_number(regexp_substr(:hldname, '^\d+$')), 0)) or (upper(o1.hldname) like '%'||upper(:hldname)||'%')) and/*{cutempty}@*/
     /*@{cutempty}*/(((o1.id_org = nvl(to_number(regexp_substr(:orgname, '^\d+$')), 0)) or (upper(o1.orgname) like '%'||upper(:orgname)||'%')) or
    (o1.id_org in (select nvl(to_number(regexp_substr(item, '^\d+$')), 0) from table(biosys.ai_utl.trans_list(:orgname, ','))))) and/*{cutempty}@*/
     /*@{cutempty}*/((upper(nvl(o1.email, o1.email_hld)) like '%'||upper(:email)||'%') or (upper(o1.emails) like '%'||upper(:email)||'%')) and/*{cutempty}@*/
     /*@{cutempty}*/(upper(o1.orgname_jur) like '%'||upper(:orgname_jur)||'%') and/*{cutempty}@*/
     /*@{cutempty}*/(upper(o1.inn) like '%'||upper(:inn)||'%') and/*{cutempty}@*/

     /*@{cutempty}*/(nvl(o1.id_vnd, -1) = :id_vnd) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.id_prop, -1) = :id_prop) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.orgtype, '-x') = :orgtype) and/*{cutempty}@*/

     /*@{cutempty}*/(nvl(o1.test, 0) = :test) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.liquidate, 0) = :liquidate) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.automatic, 0) = :automatic) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.original_dog, 0) = :original_dog) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.isreported, 0) = :isreported) and/*{cutempty}@*/
     /*@{cutempty}*/(nvl(o1.whereisdata, 0) = :whereisdata) and/*{cutempty}@*/
     /*@{cutempty}*/(o1.dataisvalid in (select item from table(biosys.ai_utl.trans_list(:dataisvalid, ';')))) and/*{cutempty}@*/
     /*@{cutempty}*/(o1.operator_state_id = :operator_state_id) and/*{cutempty}@*/

     /*@{cutempty}*/(upper(o1.mngmntform_caption) like '%'||upper(:mngmntform_caption)||'%') and/*{cutempty}@*/
     /*@{cutempty}*/(upper(o1.subsidyear) like '%'||upper(:subsidyear)||'%') and/*{cutempty}@*/
    1=1
    )
  AND (
--( (:registred is null) or (o1.registred = :registred) ) and
     /*@{cutempty}*/(o1.isonline = :isonline) and/*{cutempty}@*/
     /*@{cutempty}*/(o1.fksupported = :fksupported) and/*{cutempty}@*/
     /*@{cutempty}*/(o1.npculture = :npculture) and/*{cutempty}@*/
--     /*@{cutempty}*/(o1.modern = :modern) and/*{cutempty}@*/
     /*@{cutempty}*/(o1.verstate in (select item from table(biosys.ai_utl.trans_list(:verState, ';')))) and/*{cutempty}@*/
    1=1
    )
ORDER BY o1.orgname

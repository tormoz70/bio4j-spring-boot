SELECT * FROM (
        SELECT pgng_wrpr0.*, ROWNUM rnum_pgng
          FROM ( select *
      from table(ADSS_API09_DEV.getCampaignDays(
        p_rlocale=>:rlocale, p_currid=>:currId, p_campaignid=>:id, p_periodstart=>:dateStart, p_periodend=>:dateEnd,
        p_userorgid=>:p_sys_curusr_org_uid, p_userrole=>:p_sys_curusr_roles, p_usergrants=>:p_sys_curusr_grants)) ) pgng_wrpr0
    ) pgng_wrpr WHERE pgng_wrpr.rnum_pgng BETWEEN
        :pagination_offset + 1
        AND
        :pagination_offset+:pagination_limit
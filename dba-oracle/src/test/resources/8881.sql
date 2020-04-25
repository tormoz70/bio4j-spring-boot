SELECT * FROM (
        SELECT pgng_wrpr0.*, ROWNUM rnum_pgng
          FROM ( select *
      from table(ADSS_API09_DEV.getCampaignDays(
        p_rlocale=>null, p_currid=>null, p_campaignid=>null, p_periodstart=>'2019.07.01', p_periodend=>'2019.08.01',
        p_userorgid=>'1009686', p_userrole=>'21', p_usergrants=>'151,152')) ) pgng_wrpr0
    ) pgng_wrpr WHERE pgng_wrpr.rnum_pgng BETWEEN
        0 + 1
        AND
        0+50
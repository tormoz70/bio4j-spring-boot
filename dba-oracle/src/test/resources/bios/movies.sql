select
    a.pu_num as id,
    a.name_rus as title,
    a.poster_url_big as posterUrl,
    a.pu_num as certificateNumber
from givc_cub6.kinos a
where 1=1
  /*@{cutempty}*/and a.pu_num in (select * from table(biosys.ai_utl.split_str(:ids, ',')))/*{cutempty}@*/
  /*@{cutempty}*/and lower(a.name_rus) like '%'||lower(:searchString)||'%'/*{cutempty}@*/

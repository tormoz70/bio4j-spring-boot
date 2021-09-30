drop table givc_tmp.test_tbl;
create table givc_tmp.test_tbl(fld1 varchar2(10), fld2 number, fld3 clob);

create or replace procedure givc_tmp.test_store_clob(p_param1 in varchar2, p_param2 in number, p_param3 in clob)
    is
begin
    delete from givc_tmp.test_tbl;
    insert into givc_tmp.test_tbl(fld1, fld2, fld3)
    values(p_param1, p_param2, p_param3);
end;
/

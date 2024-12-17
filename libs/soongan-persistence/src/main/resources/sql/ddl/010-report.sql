CREATE TABLE report
(
    id                  bigint primary key auto_increment,
    report_member_id    bigint          not null,
    target_member_id    bigint          not null,
    target_id           bigint          not null,
    target_type         varchar(20)     not null,
    report_type         varchar(50)     not null,
    reason              text            null,
    created_at          datetime        null,
    updated_at          datetime        null
);

create index report_idx_report_member_id on report (report_member_id);
create index report_idx_target_member_id on report (target_member_id);

CREATE TABLE notification
(
    id              bigint primary key auto_increment,
    member_id       bigint          not null,
    type            varchar(20)     not null,
    title           varchar(20)     not null,
    body            text            not null,
    link            varchar(255)    null,
    is_read         boolean         not null,
    created_at      datetime        null,
    updated_at      datetime        null
);

create index notification_idx_member_id on notification (member_id);

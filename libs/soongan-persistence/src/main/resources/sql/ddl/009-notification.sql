CREATE TABLE notification
(
    id              bigint primary key auto_increment,
    member_id       bigint          null,
    title           varchar(20)     not null,
    body            varchar(255)    not null,
    created_at      datetime        null,
    updated_at      datetime        null
);

create index notification_idx_member_id on notification (member_id);

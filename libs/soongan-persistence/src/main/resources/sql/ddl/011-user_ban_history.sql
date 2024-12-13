CREATE TABLE user_ban_history
(
    id                  bigint primary key auto_increment,
    member_id           bigint          not null,
    ban_until           datetime        not null,
    reason              text            not null,
    created_at          datetime        null,
    updated_at          datetime        null
);

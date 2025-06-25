CREATE TABLE noti_setting
(
    id                  bigint primary key auto_increment,
    member_id           bigint          not null,
    contest_push        boolean         not null,
    activity_push        boolean         not null,
    notice_push        boolean         not null,
    created_at          datetime        null,
    updated_at          datetime        null
);

CREATE INDEX idx_noti_setting_member_id ON noti_setting (member_id);

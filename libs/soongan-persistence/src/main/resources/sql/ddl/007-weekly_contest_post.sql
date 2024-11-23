create table weekly_contest_post
(
    id                bigint primary key auto_increment,
    weekly_contest_id bigint       not null,
    member_id         bigint       not null,
    image_url         varchar(255) not null,
    ranking           int          not null,
    like_count        int default 0,
    comment_count     int default 0,
    created_at        datetime     null,
    updated_at        datetime     null
);

create index weekly_contest_post_idx_weekly_contest_id_ranking on weekly_contest_post (weekly_contest_id, ranking);
create index weekly_contest_post_idx_member_id on weekly_contest_post (member_id);

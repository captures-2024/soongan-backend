create table weekly_contest_post
(
    id                bigint primary key auto_increment,
    weekly_contest_id bigint       not null,
    member_id         bigint       not null,
    image_url         varchar(255) not null,
    content           text         null,
    ranking           int          not null,
    like_count        int default 0
);

create index weekly_contest_post_idx_weekly_contest_id_ranking on weekly_contest_post (weekly_contest_id, ranking);
create index weekly_contest_post_idx_member_id on weekly_contest_post (member_id);

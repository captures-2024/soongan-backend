create table weekly_contest
(
    id            bigint primary key auto_increment,
    round         int          not null,
    subject       varchar(255) not null,
    start_at      datetime     not null,
    end_at        datetime     not null,
    vote_start_at datetime     not null,
    vote_end_at   datetime     not null,
    announced_at  datetime     not null,
    created_at    datetime     null,
    updated_at    datetime     null
);

create index weekly_contest_idx_round on weekly_contest (round);
create index weekly_contest_idx_start_at_end_at on weekly_contest (start_at, end_at);

CREATE TABLE channels
(
  id         bigint AUTO_INCREMENT,
  channel_id varchar(64)  NOT NULL,
  title      varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  updated_at datetime NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE videos
(
  id           bigint AUTO_INCREMENT,
  video_id     varchar(64)  NOT NULL,
  channel_id   varchar(64)  NOT NULL,
  title        varchar(255) NOT NULL,
  published_at datetime     NOT NULL,
  chat_id      varchar(64),
  start_time   datetime,
  end_time     datetime,
  created_at   datetime     NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE chat_pages
(
  id               bigint AUTO_INCREMENT,
  chat_id          varchar(64) NOT NULL,
  page_token       varchar(64),
  next_page_token  varchar(64),
  polling_interval bigint      NOT NULL,
  offline_at       datetime,
  created_at       datetime    NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE chat_messages
(
  id           bigint AUTO_INCREMENT,
  message_id   varchar(512) NOT NULL,
  chat_id      varchar(64)  NOT NULL,
  page_token   varchar(64),
  message      varchar(512) NOT NULL,
  amount       bigint,
  currency     varchar(8),
  published_at datetime     NOT NULL,
  created_at   datetime     NOT NULL,

  PRIMARY KEY (id)
);

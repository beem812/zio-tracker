--  CREATE EXTENSION IF NOT EXISTS "uuid-ossp"

CREATE TABLE tracker_user(
  id UUID DEFAULT random_uuid() NOT NULL PRIMARY KEY,
  email VARCHAR(255),
  auth0_id VARCHAR(255)
);

INSERT INTO tracker_user (email, auth0_id)
VALUES ('bleep@blop.com', 'anId');

CREATE TABLE trade(
  id UUID DEFAULT random_uuid() NOT NULL PRIMARY KEY,
  user_id UUID,
  ticker VARCHAR(9),
  action VARCHAR(50),
  date DATETIME,
  price_per_share Decimal,
  shares Int,
  credit_debit VARCHAR(20)
);
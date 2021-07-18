--  CREATE EXTENSION IF NOT EXISTS "uuid-ossp"

CREATE TABLE tracker_user(
  id VARCHAR(50) DEFAULT random_uuid() NOT NULL PRIMARY KEY,
  email VARCHAR(255),
  auth0_id VARCHAR(255)
);

INSERT INTO tracker_user (email, auth0_id)
VALUES ('bleep@blop.com', 'anId');
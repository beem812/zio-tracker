CREATE TABLE tracker_user(
  id INT IDENTITY PRIMARY KEY,
  name VARCHAR(255)
);

INSERT INTO tracker_user (id, name)
VALUES (1, 'james');
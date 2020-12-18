DROP DATABASE if exists gitproject;
CREATE DATABASE gitproject CHARSET utf8mb4;
USE gitproject;

CREATE TABLE project_table (
  name VARCHAR(50),
  url VARCHAR(1024),
  descrption VARCHAR(1024),
  starCount int(255),
  forkCount int(255),
  openIssueCount int(255),
  date varchar(128)
);
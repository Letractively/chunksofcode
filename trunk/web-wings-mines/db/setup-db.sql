DROP TABLE tminesweeperhighscore;

CREATE TABLE tminesweeperhighscore (
    player_name VARCHAR (15) NOT NULL,
    game_millisecs INT8 NOT NULL
);

INSERT INTO tminesweeperhighscore (player_name, game_millisecs) VALUES ('andre', 15000);
INSERT INTO tminesweeperhighscore (player_name, game_millisecs) VALUES ('andre', 14999);
INSERT INTO tminesweeperhighscore (player_name, game_millisecs) VALUES ('verena', 2555);
INSERT INTO tminesweeperhighscore (player_name, game_millisecs) VALUES ('andre', 99999999);

SELECT * FROM tminesweeperhighscore order by game_millisecs asc FETCH FIRST 10 rows only;

DELETE FROM tminesweeperhighscore where 1=1;
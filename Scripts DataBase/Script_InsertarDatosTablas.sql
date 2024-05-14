-- Insertar datos en la base de datos 
BEGIN;
-- Insertar datos en Players
INSERT INTO Players (first_name, last_name, gender, address) VALUES
('John', 'Doe', 'M', '1234 Elm St'),
('Jane', 'Smith', 'F', '5678 Maple Ave'),
('Luis', 'Sanchez', 'M', '1348 Arm Ct'),
('Maria', 'Garcia', 'F', '2468 Oak Lane'),
('David', 'Martinez', 'M', '1357 Pine Street');

-- Insertar datos en Games
INSERT INTO Games (game_name, game_description) VALUES
('Chess', 'Strategy board game'),
('Soccer', 'Team sport played with a spherical ball'),
('League of Legends', 'Multiplayer online battle arena video game'),
('FIFA', 'Soccer simulation video game'),
('Overwatch', 'Team-based multiplayer first-person shooter game');

-- Insertar datos en Leagues
INSERT INTO Leagues (league_name, league_details) VALUES
('Junior League', 'For players under 20 years old'),
('Women''s League', 'All female players'),
('Pro League', 'For professional gamers'),
('Amateur League', 'For amateur gamers'),
('Mixed League', 'Open to gamers of all skill levels');

-- Insertar datos en Leagues_Games
INSERT INTO Leagues_Games (league_id, game_code) VALUES
(1, 1),
(2, 2);
-- Insertar datos en Players_Game_Ranking
INSERT INTO Players_Game_Ranking (player_id, game_code, ranking) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 1, 3), 
(1, 2, 1), 
(2, 3, 2); 

-- Insertar datos en Teams
INSERT INTO Teams (created_by_player_id, team_name) VALUES
(1, 'The Eagles'),
(2, 'The Hawks'),
(3, 'The Wolves'), 
(1, 'The Lions'), 
(2, 'The Tigers'); 


-- Insertar datos en Team_Players
INSERT INTO Team_Players (team_id, player_id, date_from) VALUES
(1, 1, '2022-01-01'),
(2, 2, '2022-01-02'),
(3, 3, '2022-01-03'), 
(1, 3, '2022-01-01'), 
(2, 1, '2022-01-02'); 

-- Insertar datos en Matches
INSERT INTO Matches (game_code, player_1_id, player_2_id, result) VALUES
(1, 1, 2, 'win'),
(2, 2, 1, 'lose'),
(1, 1, 2, 'lose'), 
(2, 2, 3, 'win'), 
(3, 3, 1, 'draw'); 

INSERT INTO Matches_Teams (league_id, team1_id, team2_id, match_date, game_code) VALUES
(1, 1, 2, '2022-01-01', 1), 
(2, 2, 3, '2022-01-02', 2),
(1, 1, 3, '2022-01-03', 1), 
(2, 1, 2, '2022-01-04', 2), 
(1, 2, 3, '2022-01-05', 1); 

COMMIT;

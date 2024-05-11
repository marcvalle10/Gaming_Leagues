-- Insertar datos en tablas en PostgreSQL
BEGIN;
-- Insertar datos en Players
INSERT INTO Players (first_name, last_name, gender, address) VALUES
('John', 'Doe', 'M', '1234 Elm St'),
('Jane', 'Smith', 'F', '5678 Maple Ave');
-- Insertar datos en Games
INSERT INTO Games (game_name, game_description) VALUES
('Chess', 'Strategy board game'),
('Soccer', 'Team sport played with a spherical ball');
-- Insertar datos en Leagues
-- Insertar datos en Leagues
INSERT INTO Leagues (league_name, league_details) VALUES
('Junior League', 'For players under 20 years old'),
('Women''s League', 'All female players');
-- Insertar datos en Leagues_Games
INSERT INTO Leagues_Games (league_id, game_code) VALUES
(1, 1),
(2, 2);
-- Insertar datos en Players_Game_Ranking
INSERT INTO Players_Game_Ranking (player_id, game_code, ranking) VALUES
(1, 1, 1),
(2, 2, 2);
-- Insertar datos en Teams
INSERT INTO Teams (created_by_player_id, team_name) VALUES
(1, 'The Eagles'),
(2, 'The Hawks');
-- Insertar datos en Team_Players
INSERT INTO Team_Players (team_id, player_id, date_from) VALUES
(1, 1, '2022-01-01'),
(2, 2, '2022-01-02');
-- Insertar datos en Matches
INSERT INTO Matches (game_code, player_1_id, player_2_id, result) VALUES
(1, 1, 2, 'win'),
(2, 2, 1, 'lose');
COMMIT;

-- Creación de tablas en PostgreSQL
BEGIN;

-- Jugadores
CREATE TABLE Players (
    player_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')),  -- M = Male, F = Female, O = Other
    address VARCHAR(255)
);

-- Juegos
CREATE TABLE Games (
    game_code SERIAL PRIMARY KEY,
    game_name VARCHAR(100) NOT NULL,
    game_description TEXT
);

-- Ligas
CREATE TABLE Leagues (
    league_id SERIAL PRIMARY KEY,
    league_name VARCHAR(100) NOT NULL,
    league_details TEXT
);

-- Relación entre Ligas y Juegos
CREATE TABLE Leagues_Games (
    league_id INTEGER NOT NULL,
    game_code INTEGER NOT NULL,
    PRIMARY KEY (league_id, game_code),
    FOREIGN KEY (league_id) REFERENCES Leagues(league_id) ON DELETE CASCADE,
    FOREIGN KEY (game_code) REFERENCES Games(game_code) ON DELETE CASCADE
);

-- Ranking de Jugadores en Juegos
CREATE TABLE Players_Game_Ranking (
    player_id INTEGER NOT NULL,
    game_code INTEGER NOT NULL,
    ranking INTEGER CHECK (ranking > 0),
    PRIMARY KEY (player_id, game_code),
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (game_code) REFERENCES Games(game_code) ON DELETE CASCADE
);

-- Equipos
CREATE TABLE Teams (
    team_id SERIAL PRIMARY KEY,
    created_by_player_id INTEGER NOT NULL,
    team_name VARCHAR(100) NOT NULL,
    date_created DATE DEFAULT CURRENT_DATE,
    date_disbanded DATE,
    FOREIGN KEY (created_by_player_id) REFERENCES Players(player_id) ON DELETE SET NULL
);

-- Relación entre Equipos y Jugadores
CREATE TABLE Team_Players (
    team_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE,
    PRIMARY KEY (team_id, player_id, date_from),
    FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE
);

-- Partidos
CREATE TABLE Matches (
    match_id SERIAL PRIMARY KEY,
    game_code INTEGER NOT NULL,
    player_1_id INTEGER NOT NULL,
    player_2_id INTEGER NOT NULL,
    match_date DATE DEFAULT CURRENT_DATE,
    result VARCHAR(10) CHECK (result IN ('win', 'lose', 'draw')),
    FOREIGN KEY (game_code) REFERENCES Games(game_code),
    FOREIGN KEY (player_1_id) REFERENCES Players(player_id),
    FOREIGN KEY (player_2_id) REFERENCES Players(player_id)
);

COMMIT;

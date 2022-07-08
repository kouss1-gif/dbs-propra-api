PRAGMA auto_vacuum = 1;
PRAGMA encoding ="UTF-8";
PRAGMA foreign_keys = 1;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;


CREATE TABLE Nutzer(Email VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(Email)>0 AND Email LIKE "%@%.%" AND
                                                                    substr(Email,1,(instr(Email,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                    substr(Email,(instr(Email,"@")+1),(instr(Email,".")-instr(Email,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                    substr(Email,(instr(Email,".")+1)) NOT GLOB "*[^A-Za-z]*"),
                    Passwort VARCHAR(120) NOT NULL CHECK (Passwort GLOB "*[A-Z]*" AND Passwort GLOB "*[0-9]*" AND Passwort NOT GLOB "*[0-9][0-9]*" AND length(Passwort)>=3 AND length(Passwort)<=9 ),
                    PRIMARY KEY (Email));

CREATE TABLE Premiumnutzer(Nutzeremail VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(Nutzeremail)>0 AND Nutzeremail LIKE "%@%.%" AND
                                                                                 substr(Nutzeremail,1,(instr(Nutzeremail,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                 substr(Nutzeremail,(instr(Nutzeremail,"@")+1),(instr(Nutzeremail,".")-instr(Nutzeremail,"@")-1))NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                 substr(Nutzeremail,(instr(Nutzeremail,".")+1))NOT GLOB "*[^A-Za-z]*"),
                           Zahlungsdatum DATE NOT NULL CHECK(Zahlungsdatum LIKE "____-__-__"),
                           PRIMARY KEY (Nutzeremail),
                           FOREIGN KEY (Nutzeremail) REFERENCES Nutzer(Email)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE);

CREATE TABLE Mitarbeiter(Nutzeremail VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(Nutzeremail)>0 AND Nutzeremail LIKE "%@%.%" AND
                                                                               substr(Nutzeremail,1,(instr(Nutzeremail,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                               substr(Nutzeremail,(instr(Nutzeremail,"@")+1),(instr(Nutzeremail,".")-instr(Nutzeremail,"@")-1))NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                               substr(Nutzeremail,(instr(Nutzeremail,".")+1))NOT GLOB "*[^A-Za-z]*"),
                         Personalnummer INTEGER NOT NULL CHECK(Personalnummer >= 0),
                         PRIMARY KEY (Nutzeremail),
                         FOREIGN KEY (Nutzeremail) REFERENCES Mitarbeiter(Nutzeremail)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE);

CREATE TABLE Fan(Nutzeremail VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(Nutzeremail)>0 AND Nutzeremail LIKE "%@%.%" AND
                                                                       substr(Nutzeremail,1,(instr(Nutzeremail,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                       substr(Nutzeremail,(instr(Nutzeremail,"@")+1),(instr(Nutzeremail,".")-instr(Nutzeremail,"@")-1))NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                       substr(Nutzeremail,(instr(Nutzeremail,".")+1))NOT GLOB "*[^A-Za-z]*"),
                 Username VARCHAR(80) NOT NULL CHECK(Username NOT GLOB "*[^ -~]*" AND length(Username)>0),
                 MannschaftsID INTEGER NOT NULL CHECK(MannschaftsID >= 0),
                 PRIMARY KEY (Nutzeremail),
                 FOREIGN KEY (Nutzeremail) REFERENCES Nutzer(Email)
                     ON DELETE CASCADE
                     ON UPDATE CASCADE,
                 FOREIGN KEY (MannschaftsID) REFERENCES Mannschaft(ID)
                     ON DELETE CASCADE
                     ON UPDATE CASCADE);

CREATE TABLE Spiel(ID INTEGER NOT NULL CHECK(ID >= 0),
                   Spieldatum DATE NOT NULL CHECK(Spieldatum LIKE "____-__-__"),
                   VODLink VARCHAR(80) NOT NULL CHECK(VODLink NOT GLOB "*[^ -~]*" AND length(VODLink) > 0),
                   LigaID INTEGER NOT NULL CHECK(LigaID >= 0),
                   PRIMARY KEY (ID),
                   FOREIGN KEY (LigaID) REFERENCES Liga(ID)
                       ON DELETE CASCADE
                       ON UPDATE CASCADE);

CREATE TABLE Schiedsrichter(ID INTEGER NOT NULL CHECK(ID >= 0),
                            Vorname VARCHAR(80) NOT NULL CHECK(Vorname NOT GLOB "*[^ -~]*" AND length(Vorname)>0),
                            Nachname VARCHAR(80) NOT NULL CHECK(Nachname NOT GLOB "*[^ -~]*" AND length(Nachname)>0),
                            PRIMARY KEY (ID));

CREATE TABLE Liga(ID INTEGER NOT NULL CHECK(ID >= 0),
                  Benennung VARCHAR(80) NOT NULL CHECK(Benennung NOT GLOB "*[^ -~]*" AND length(Benennung)>0),
                  PRIMARY KEY (ID));

CREATE TABLE Reportage(ID INTEGER NOT NULL CHECK(ID >= 0),
                       Bildone VARCHAR(80) NOT NULL CHECK (Bildone LIKE "%.jpg" AND length (Bildone) >= 6),
                       Bildtwo VARCHAR(80) NOT NULL CHECK (Bildtwo LIKE "%.jpg" AND length (Bildtwo) >= 6),
                       Text VARCHAR(120) NOT NULL CHECK(Text NOT GLOB "*[^ -~]*" AND length(Text)>0),
                       Ueberschrifft VARCHAR(120) NOT NULL CHECK(Ueberschrifft NOT GLOB "*[^ -~]*" AND length(Ueberschrifft)>0),
                       Mitarbeiternutzeremail VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(Mitarbeiternutzeremail)>0 AND Mitarbeiternutzeremail LIKE "%@%.%" AND
                                                                                        substr(Mitarbeiternutzeremail,1,(instr(Mitarbeiternutzeremail,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                        substr(Mitarbeiternutzeremail,(instr(Mitarbeiternutzeremail,"@")+1),(instr(Mitarbeiternutzeremail,".")-instr(Mitarbeiternutzeremail,"@")-1))NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                        substr(Mitarbeiternutzeremail,(instr(Mitarbeiternutzeremail,".")+1))NOT GLOB "*[^A-Za-z]*"),
                       SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                       PRIMARY KEY (ID),
                       FOREIGN KEY (Mitarbeiternutzeremail) REFERENCES Nutzer(Email)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE,
                       FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE);

CREATE TABLE Mannschaft(ID INTEGER NOT NULL CHECK(ID >= 0),
                        Mannschaftsname VARCHAR(80) NOT NULL CHECK(Mannschaftsname NOT GLOB "*[^ -~]*" AND length(Mannschaftsname)>0),
                        PRIMARY KEY (ID));

CREATE TABLE Spieler(ID INTEGER NOT NULL CHECK(ID >= 0),
                     Vorname VARCHAR(80) NOT NULL CHECK(Vorname NOT GLOB "*[^ -~]*" AND length(Vorname)>0),
                     Nachname VARCHAR(80) NOT NULL CHECK(Nachname NOT GLOB "*[^ -~]*" AND length(Nachname)>0),
                     Spitzname VARCHAR(80) NOT NULL CHECK(Spitzname NOT GLOB "*[^ -~]*" AND length(Spitzname)>0),
                     MannschaftID INTEGER NOT NULL CHECK(MannschaftID >= 0),
                     PRIMARY KEY (ID),
                     FOREIGN KEY (MannschaftID) REFERENCES Mannschaft(ID)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE);

CREATE TABLE Verletzung(ID INTEGER NOT NULL CHECK(ID >= 0),
                        Verletzungsart VARCHAR(80) NOT NULL CHECK(Verletzungsart NOT GLOB "*[^A-Za-z]*" AND length(Verletzungsart)>0),
                        Verletzungsdauer INTEGER NOT NULL CHECK(Verletzungsdauer >= 0),
                        SpielerID INTEGER NOT NULL CHECK(SpielerID >= 0),
                        PRIMARY KEY (ID),
                        FOREIGN KEY (SpielerID) REFERENCES Spieler(ID)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE);

CREATE TABLE Spielwert(ID INTEGER NOT NULL CHECK(ID >= 0),
                       Feldzeit INTEGER NOT NULL CHECK(Feldzeit >= 0),
                       Distanz INTEGER NOT NULL CHECK(Distanz >= 0),
                       Ballbesitz INTEGER NOT NULL CHECK(Ballbesitz >= 0),
                       Anzahlschuesse INTEGER NOT NULL CHECK(Anzahlschuesse >= 0),
                       PRIMARY KEY (ID));
CREATE TABLE Straffen(ID INTEGER NOT NULL CHECK(ID >= 0),
                      Beschreibung VARCHAR(80) NOT NULL CHECK(Beschreibung NOT GLOB "*[^ -~]*" AND length(Beschreibung)>0),
                      PRIMARY KEY (ID));

CREATE TABLE SchiedsrichterbetreutSpiel(SchiedsrichterID INTEGER NOT NULL CHECK(SchiedsrichterID >= 0),
                                        SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                                        PRIMARY KEY (SchiedsrichterID,SpielID),
                                        FOREIGN KEY (SchiedsrichterID) REFERENCES Schiedsrichter(ID)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE);

CREATE TABLE PremiumnutzeranschauenSpiel(angeschautbis TIME NOT NULL CHECK(angeschautbis LIKE "__:__:__"),
                                         PremiumnutzerNutzeremail VARCHAR(120) NOT NULL COLLATE NOCASE CHECK(length(PremiumnutzerNutzeremail)>0 AND PremiumnutzerNutzeremail LIKE "%@%.%" AND
                                                                                                            substr(PremiumnutzerNutzeremail,1,(instr(PremiumnutzerNutzeremail,"@")-1)) NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                                            substr(PremiumnutzerNutzeremail,(instr(PremiumnutzerNutzeremail,"@")+1),(instr(PremiumnutzerNutzeremail,".")-instr(PremiumnutzerNutzeremail,"@")-1))NOT GLOB "*[^A-Za-z0-9]*" AND
                                                                                                            substr(PremiumnutzerNutzeremail,(instr(PremiumnutzerNutzeremail,".")+1))NOT GLOB "*[^A-Za-z]*"),
                                         SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                                         PRIMARY KEY (PremiumnutzerNutzeremail,SpielID),
                                         FOREIGN KEY (PremiumnutzerNutzeremail) REFERENCES Nutzer(Email)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                         FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE);

CREATE TABLE MannschaftteilnehmenanSpiel(Spielstand VARCHAR(80) NOT NULL CHECK(Spielstand NOT GLOB "*[^ -~]*" AND length(Spielstand)>0),
                                         MannschaftID INTEGER NOT NULL CHECK(MannschaftID >= 0),
                                         SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                                         PRIMARY KEY (MannschaftID,SpielID),
                                         FOREIGN KEY (MannschaftID) REFERENCES Mannschaft(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                         FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE);

CREATE TABLE SpielerspieltaktivSpiel(Spielzeitvon INTEGER NOT NULL CHECK(Spielzeitvon >= 0),
                                     Spielzeitbis INTEGER NOT NULL CHECK(Spielzeitbis >= 0),
                                     SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                                     SpielerID INTEGER NOT NULL CHECK(SpielerID >= 0),
                                     PRIMARY KEY (SpielID,SpielerID),
                                     FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,
                                     FOREIGN KEY (SpielerID) REFERENCES Spieler(ID)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE);

CREATE TABLE SpielerundSpielhatSpielwert(SpielwertID INTEGER NOT NULL CHECK(SpielwertID >= 0),
                                         SpielerID INTEGER NOT NULL CHECK(SpielerID >= 0),
                                         SpielID INTEGER NOT NULL CHECK(SpielID >= 0),
                                         PRIMARY KEY (SpielwertID,SpielerID,SpielID),
                                         FOREIGN KEY (SpielwertID) REFERENCES Spielwert(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                         FOREIGN KEY (SpielerID) REFERENCES Spieler(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                         FOREIGN KEY (SpielID) REFERENCES Spiel(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE);

CREATE TABLE ReportageempfehlenReportage(ReportageID1 INTEGER NOT NULL CHECK(ReportageID1 >= 0),
                                         ReportageID2 INTEGER NOT NULL CHECK(ReportageID2 >= 0
                                             AND ReportageID1 NOT LIKE ReportageID2),
                                         PRIMARY KEY (ReportageID1),
                                         FOREIGN KEY (ReportageID1) REFERENCES Reportage(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                         FOREIGN KEY (ReportageID2) REFERENCES Reportage(ID)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE);

CREATE TABLE StraffengehoertzuSpielwert(StraffenID INTEGER NOT NULL CHECK(StraffenID >= 0),
                                        SpielwertID INTEGER NOT NULL CHECK(SpielwertID >= 0),
                                        UNIQUE(StraffenID,SpielwertID),
                                        PRIMARY KEY (StraffenID,SpielwertID),
                                        FOREIGN KEY (StraffenID) REFERENCES Straffen(ID)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        FOREIGN KEY (SpielwertID) REFERENCES Spielwert(ID)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE);
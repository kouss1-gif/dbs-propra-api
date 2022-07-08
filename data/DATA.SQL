PRAGMA auto_vacuum = 1;
PRAGMA encoding ="UTF-8";
PRAGMA foreign_keys = 1;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

INSERT INTO Nutzer VALUES("danido44@gmail.com","2EE6");
INSERT INTO Nutzer VALUES("aaron22@gmail.com","F4F6WQ");
INSERT INTO Nutzer VALUES("dani3@gmail.com","3EE5");
INSERT INTO Nutzer VALUES("sl1@gmail.com","F4F6WQ");
INSERT INTO Nutzer VALUES("sam32@gmail.com","1EE4");
INSERT INTO Nutzer VALUES("yobo1@gmail.com","F4F6WQ");

INSERT INTO Premiumnutzer VALUES ("danido44@gmail.com","2022-05-22");
INSERT INTO Premiumnutzer VALUES ("aaron22@gmail.com","2022-11-01");
INSERT INTO Premiumnutzer VALUES ("dani3@gmail.com","2024-01-01");
INSERT INTO Premiumnutzer VALUES ("sl1@gmail.com","2024-01-11");

INSERT INTO Mitarbeiter VALUES ("danido44@gmail.com",111666);
INSERT INTO Mitarbeiter VALUES ("yobo1@gmail.com",234555);
INSERT INTO Mitarbeiter VALUES ("dani3@gmail.com",012244);
INSERT INTO Mitarbeiter VALUES ("sam32@gmail.com",123456);

INSERT INTO Mannschaft VALUES (24,"Giants");
INSERT INTO Mannschaft VALUES (25,"Eagels");
INSERT INTO Mannschaft VALUES (26,"Warriors");
INSERT INTO Mannschaft VALUES (27,"Celtics");


INSERT INTO Fan VALUES ("danido44@gmail.com","davido",24);
INSERT INTO Fan VALUES ("yobo1@gmail.com","yobo",25);
INSERT INTO Fan VALUES ("dani3@gmail.com","dani3",26);
INSERT INTO Fan VALUES ("sam32@gmail.com","sam32",27);

INSERT INTO Liga VALUES (05,"Liga1");
INSERT INTO Liga VALUES (06,"Liga2");
INSERT INTO Liga VALUES (07,"Liga3");
INSERT INTO Liga VALUES (08,"Liga4");


INSERT INTO Spiel VALUES (01,"2022-07-01","VOD-LINK-YSFVHF2w344",05);
INSERT INTO Spiel VALUES (02,"2022-08-21","VOD-LINK-SDFCHGR4562",06);
INSERT INTO Spiel VALUES (03,"2022-04-01","VOD-LINK-DSFFFF1w334",07);
INSERT INTO Spiel VALUES (04,"2022-06-01","VOD-LINK-TRP78FTH%55",08);

INSERT INTO Schiedsrichter VALUES (11,"Oli","Baas");
INSERT INTO Schiedsrichter VALUES (12,"Wilson","Daniels");
INSERT INTO Schiedsrichter VALUES (13,"Dams","Faad");
INSERT INTO Schiedsrichter VALUES (14,"Jack","Olsem");


INSERT INTO Reportage VALUES (20,"Bildone.jpg","Bildtwo.jpg","really big match today","Big match","danido44@gmail.com",01);
INSERT INTO Reportage VALUES (21,"Bildone.jpg","Bildtwo.jpg","the lights is on everybody ready","Lights on","yobo1@gmail.com",02);
INSERT INTO Reportage VALUES (22,"Bildone.jpg","Bildtwo.jpg","the league trophy on the line","trophy","dani3@gmail.com",03);
INSERT INTO Reportage VALUES (23,"Bildone.jpg","Bildtwo.jpg","the players are ready for next season ","Lets get it","sam32@gmail.com",04);

INSERT INTO Spieler VALUES (28,"Walid","John","Dizzy",24);
INSERT INTO Spieler VALUES (29,"John","Kurt","Searle",25);
INSERT INTO Spieler VALUES (30,"Marc","Oliver","Edmonds",26);
INSERT INTO Spieler VALUES (31,"Mustafa","Maja","Moses",27);

INSERT INTO Verletzung VALUES (32,"Kneeverletzung",24,28);
INSERT INTO Verletzung VALUES (33,"Muskelkater",3,29);

INSERT INTO Spielwert VALUES (40,33,500,22,5);
INSERT INTO Spielwert VALUES (41,12,123,12,2);
INSERT INTO Spielwert VALUES (42,52,620,26,13);
INSERT INTO Spielwert VALUES (43,62,720,20,20);

INSERT INTO Straffen VALUES (60,"Offensive Foul,Gelbe Karte");
INSERT INTO Straffen VALUES (70,"Deffensive Foul,Rote Karte");

INSERT INTO SchiedsrichterbetreutSpiel VALUES(11,01);
INSERT INTO SchiedsrichterbetreutSpiel VALUES(12,02);

INSERT INTO PremiumnutzeranschauenSpiel VALUES("00:13:55","danido44@gmail.com",03);
INSERT INTO PremiumnutzeranschauenSpiel VALUES("00:28:46","aaron22@gmail.com",04);

INSERT INTO MannschaftteilnehmenanSpiel VALUES("3-1",24,01);
INSERT INTO MannschaftteilnehmenanSpiel VALUES("0-0",27,02);

INSERT INTO SpielerspieltaktivSpiel VALUES(10,90,01,28);
INSERT INTO SpielerspieltaktivSpiel VALUES(33,88,02,30);

INSERT INTO SpielerundSpielhatSpielwert VALUES(40,28,01);
INSERT INTO SpielerundSpielhatSpielwert VALUES(43,30,02);

INSERT INTO ReportageempfehlenReportage VALUES(20,21);
INSERT INTO ReportageempfehlenReportage VALUES(22,23);

INSERT INTO StraffengehoertzuSpielwert VALUES(60,40);
INSERT INTO StraffengehoertzuSpielwert VALUES(70,40);

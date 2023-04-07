▀█▀ █ █▀▀   ▀█▀ ▄▀█ █▀▀   ▀█▀ █▀█ █▀▀   █▀▄▀█ ▄▀█ █▀ ▀█▀ █▀▀ █▀█
░█░ █ █▄▄   ░█░ █▀█ █▄▄   ░█░ █▄█ ██▄   █░▀░█ █▀█ ▄█ ░█░ ██▄ █▀▄

Tic Tac Toe Master

Java full edition

Created in 2019
Presented at Festival of science and technology AMAVET 2019
Participant of national round of Slovakia
Refactored, optimized and translated to English in 2023

Developed by Štefan Kando
Presented by Štefan Kando and Bára Elisabeth Dočkalová


Description:
Unbeatable AI in 3x3 Tic Tac Toe programmed with Java working with MySQL.
2019 project originally in Slovak, refactored, optimized and translated to English in 2023.
The project has elements of the server-client application and a MySQL server was hosted
during the project creation and presentation. 
The server is no longer available and the project needs
a running MySQL database to function. This database can be a localhost.
The code is undocumented because it is old and I was not as experienced when I was writing it.


Prerequisites:
You need Java installed along with a running MySQL database.
For the MySQL database, I recommend installing XAMPP.


Installation:
Run the .exe file or run the .jar file with 
"java -jar "TTTMRemastered.jar"" command to start the application.


How to play with AI:
- Have a MySQL database running
with either an empty database or a database with "tttm.sql" uploaded to it
- Start the application
- Press "Unbeatable AI - MySQL"
- Log in to the database and press Connect
If you have a localhost database with "root" without a password and the database
is called "tttm", press "Default" and the fields will get filled automatically
If the connection is successful, green "Connection was successful" text
will appear above the button and the "Next" button will be available
The program will create necessary tables if they do not exist in the database already
- Press Next
- If Generating combinations and Generating decisions do not have green outlines
on them, regenerate the database - first click on Generating combinations, and press START,
then do the same with Generating decisions - generating both databases should take
something around 5 minutes after the 2023 optimization
- When the database is ready, the "Play" button will be available - click "Play"
- Select your player (X or O)
- The program will ask you if you are okay with sharing your IP (this was used
to identify winners, but if you are using a localhost database, it does not matter what you pick)
- Try to beat the AI


If you beat the AI, you can get the bounty in the form you desire. In case you
manage to win over the AI, reach out to me through s.kando@azet.sk.
Since the AI is deterministic, you can just describe how you managed to beat it.
You must follow the rules of Tic Tac Toe, winnings over AI using glitches, hacks,
editing the code etc. will be disregarded.



File structure:


TTTMRemastered/
├─ lib/
│  ├─ mysql-connector-java-8.0.17.jar - MySQL connector
├─ SQL/
│  ├─ tttm.sql - SQL file for uploading the database to MySQL server
├─ src/
│  ├─ AIGame.java - Main panel for playing against the AI
│  ├─ AIPlayerSelect.java - Panel for selecting the player (X or O) for the game against the AI
│  ├─ AIvsAI.java - Panel for trying out what would happen if two AIs would go against each other
│  ├─ DBConnect.java - Panel for connecting to the database
│  ├─ DBPreview.java - Panel for visualisation of the database (what the AI sees when it's deciding)
│  ├─ Feedback.java - Panel for providing user feedback and sending it to the database
│  ├─ GenerationCombinations.java - Panel for generating possible combinations of the game into the 'combinations' table
│  ├─ GenerationDecisions.java - Panel for generating decisions for each branch into the 'decisions' table
│  ├─ MenuAI.java - Menu regarding the AI after connecting to the database
│  ├─ MenuMain.java - Main menu that shows after starting the application
│  ├─ Multiplayer.java - Game for two human players to play locally against each other
│  ├─ TTTM.java - Main class containing main game logic and main function
├─ README - you are reading this
├─ TTTMRemastered.exe - Wrapped Java build of the application
├─ TTTMRemastered.ico - Original 2019 icon of the application
├─ TTTMRemastered.jar - Java build of the application
├─ Poster.pdf - Original 2019 poster presented at the national round of Slovakia - FoSaT AMAVET 2019
		^	More information about the project can be found here


Database structure:

tttm/ - Default name of the database
├─ combinations/ - Table of possible final combinations of the game
│  ├─ combination - Combinations of the game in form of 5-9 digits long numbers
├─ decisions/ - Statistics table to help the AI decide the next move
│  ├─ combination - Branches of the game in form of 1-9 digit long numbers
│  ├─ xwin - % chance of win of X
│  ├─ owin - % chance of win of O
│  ├─ draw - % chance of a draw
├─ exceptions/ - Table of exceptions to fix the "statistical holes"
│  ├─ combination - What combination should be exchanged
│  ├─ exception - What the decision should be exchanged with
├─ feedback/ - Table of user-provided feedback
│  ├─ nickname - Nickname of the feedback sender
│  ├─ email - Email of the feedback sender
│  ├─ text - Text of the feedback
│  ├─ important - Bool if the feedback is important
│  ├─ happenedAt - When the feedback was sent
│  ├─ beenRead - Bool if the feedback was read
├─ mistakes/ - Table for storing the AI mistakes
│  ├─ mistake - Combination of AI losing
│  ├─ AIPlayer - What player the AI was (0 - X, 1 - O)
│  ├─ happenedAt - Date and time when it happened
│  ├─ publicIP - Public IP of the human player (if they wish to share it - for identification)
│  ├─ localIP - Local IP of the human player (if they wish to share it - for identification)
│  ├─ fixed - Bool if the mistake has been fixed
├─ testers/ - Table of registered testers (if they do not wish to see the message of IP sharing approval)
│  ├─ nickname - Nickname of the tester
│  ├─ publicIP - Public IP of the tester
│  ├─ localIP - Local IP of the tester

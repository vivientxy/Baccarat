## Baccarat

To compile:
```
javac -d classes -cp lib/junit-4.13.2.jar;classes src/client/* src/server/* src/test/BaccaratTest.java
```

To run server code:
```
java -cp classes server.ServerApp <port> <number_of_decks>
```

To run client code:
```
java -cp classes client.ClientApp <host>:<port>
```

To run unit tests:
```
java -cp lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore classes/BaccaratTest
```

To view on Vercel:
```
https://baccarat-git-main-viviens-projects-fd817419.vercel.app/
```

## Description

This project is a client-server Baccarat game.

Client has to send one of the following commands to server:
- login `username` `topUpAmount`
- bet `amount`
- deal `B` 
- deal `P`
- exit

The program will output winning sides (B, P or D for Broker, Player or Draw respectively) onto the following webpage:
```
https://baccarat-git-main-viviens-projects-fd817419.vercel.app/
```
Choose the csv file generated by the client app to display the game history.

## Java Version

This project runs on Java 21.
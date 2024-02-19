## Baccarat

To compile server code:
```
javac --source-path src -d classes src/server/*
```

To compile client code:
```
javac --source-path src -d classes src/client/*
```

To run server code:
```
java -cp classes server.ServerApp <port> <number_of_decks>
```

To run client code:
```
java -cp classes client.ClientApp <host>:<port>
```

## Description

This project is a client-server Baccarat game.

Client has to send one of the following commands to server:
- login `username` `walletAmount`
- bet `amount`
- deal `B`
- deal `P`

The program will output winning sides (B, P or D for Broker, Player or Draw respectively) onto the following webpage:
```
<vercel link here>
```

## Java Version

This project runs on Java 21.
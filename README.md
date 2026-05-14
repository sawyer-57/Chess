# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```



## PHASE 2 DIAGRAM
 https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowGKEpujKcplu8So3gFDpJr6zpdhu7pbiqwYagAkmgVAmkg676TA0AwGpOwwNu7kWf6zLTJe0BIAAXigHBRjGcaFFpCXwMgqYwOmACMBE5qoebzNBRYlvUPi1Xq9VNbsdFNm5grDvy9SHnIKDPvE56Xteq2LpUy4BmuAZ7Vuy1dTppYOeKGSqABmDXSB1S6YR5afJCsGXlR9aQppr3wsmPXYTAuH4aM73RZ9MHkT9iF-bRjYMZ43h+P4XgoOgMRxIkmPYw5vhYKJgqgfUDTSBG-ERu0EbdD0cmqApwwUb9HWA9pZn+qzCOFM9FlWTZ9hE-ZQlE05aguRVK23mtMCMmA227fBvP+bygVHcFoVPud8iyvKPNIZg6XqjA2W5cg66G+gsXq-FAsujAPZ9uVl1VaWNUUXNzWtSgsYKehQOVKJaZOINkPDaNewTdAU0zfE3sLcjtsLgKXVufU22vklMtxfSRgoNwx6XsrlGq+VB1p5r9QZDMEA0GdL4XTnV1c6WD37k9bcvRJYEA73mEg2AOF4Vmi0o0x6Mouu-jYOKGr8WiMAAOJKhoJPu+Ty80-T9hKiz8NG51lTXfU1t893DvJcgOSrzm9lonfagSyS0seXb+cK0r59q6n95a+KHWTd5DG1VFlHKeUraH2xinfcPdLKO2dggV2LdN6SnjonX2-t4ydWBiUYefUw5DX5FHRYMdSzTS9lARqzUGz0TflSWW+cb5gCfqoDEv84FBRrkyahKAG4mmQU-D0ld4FWUXjkAAPMI7Q5Q34fjbvUCRrC173UevzNB4w945gWA0Fw+jOj90THg3q4MCLaLULo-RLhDHJ1RsxfwHAADsbgnAoCcDECMwQ4BcQAGzwAnIYJ+5UN6JV7uTVoHRd773jmzLMFiABySojEYQUbCbm0DFKjESckru6T4RhIQclDa6In4YjgIEp+L8paXUYXnUc8smTf0yZwjWQp6ja0bleNKYCzYQMtsaTJsDApX27L2fsbtCnVQwdQ+aWD2qB0Hvg0O4dswkPzGQ4sscFQzJoUnehtSZBMIaSUlAZSLGzgrsc-+mdToyJAaI9OSUGRKkytIeRJ9FEBKPGcpUaiECAUvposYFi3kLH6uEYIgQUlB26sssGo9IagukOCyF0K7GTwCJYQuNlNg4yQAkMA2K+wQDxQAKQgOKFeFZ-DJFAGqIoQ9SaAwicyGSPQLEHxVkhLM2AEDAGxVAOAEAbJQDWMimFnN8ln0yby-lgrhWivFa86Qpl8nMs7PUAAVpStAZSKXiiqSgQkktXItzqR-BpX8S4-yufUm5IVAFdI9CbcBFt8qDO5TbR5oynbjJQZ2eB0yqF7PmQpANxjg5DxWcQ3MGyxjkLjiG+adClrmqOfU7yKi5gYmRa0+K1dHUSnucAYZ9tClWRLaAjK1K5hGAgGoJ2EBmBWiXmWxcaCkERsHGggAQiGapYacEcy6iHQhEM1lxrGgmrZpY9DrhRCanIqaGKuvgCK4qJozQ1nDOzQNTzwkBirKGXdQ60ARtSSY0G6ZMwR3WdOxNlZt0wFrPWceDCM2WqmtgLQpSlS5qVPta53DJQ-s2rWlAMoPkPGlTADuI48lfiDX3RZo7o0Ione++x6MvACvxYS3D8pEDBlgMAbAfLCB5AKCEplaCKZUxpnTXoxhUOfNg4h8yFbHYgG4HgDhH7K7rV41ADhdrLUOtrvXQwgijDN33Wkr89R4MOg45eqZKHcFRvhWY0Y76gA 
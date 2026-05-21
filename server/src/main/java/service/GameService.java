package service;

import dataaccess.*;
import model.*;
import service.requests.*;
import service.results.*;
import exception.*;

import java.util.ArrayList;
import java.util.List;
import chess.ChessGame;

public class GameService {
    private final GameDAO gameDAO; 
    private final AuthDAO authDAO;

    public GameService(
            GameDAO gameDAO,
            AuthDAO authDAO) {

        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(
            ListGamesRequest request)
            throws Exception{

        if(authDAO.getAuth(request.authToken())==null){
            throw new UnauthorizedException(
                "Error: unauthorized");
        }

        List<GameData> games=
                new ArrayList<>(gameDAO.listGames());

        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(
            CreateGameRequest request)
            throws Exception{

        if(authDAO.getAuth(request.authToken())==null){
            throw new UnauthorizedException(
                "Error: unauthorized");
        }

        ChessGame chessGame=new ChessGame();

        GameData game = new GameData(
            0, 
            null, 
            null, 
            request.gameName(), 
            chessGame
        ); 

        int gameID = gameDAO.createGame(game);

        return new CreateGameResult(gameID);
    }

    public void joinGame(
            JoinGameRequest request)
            throws Exception{

        AuthData auth=
                authDAO.getAuth(
                        request.authToken());

        if(auth == null){
            throw new UnauthorizedException(
                "Error: unauthorized");
        }

        GameData game=
                gameDAO.getGame(
                        request.gameID());

        if(game == null){
            throw new BadRequestException(
                "Error: bad request");
        }

        if(request.playerColor()
                .equals("WHITE")){

            if(game.whiteUsername()!=null){

                throw new AlreadyTakenException(
                    "Error: already taken");
            }

            game=new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game());

        }

        else if(request.playerColor()
                .equals("BLACK")){

            if(game.blackUsername()!=null){
                throw new AlreadyTakenException(
                    "Error: already taken");
            }

            game=new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game());
        }

        gameDAO.updateGame(game);
    }
}

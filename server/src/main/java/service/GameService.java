package service;

import dataaccess.*;
import model.*;
import chess.ChessGame;
import service.requests.*;
import service.results.*;
import exception.*;

import java.util.List;


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

        if(authDAO.getAuth(
                request.authToken())==null){

            throw new UnauthorizedException();
        }

        List<GameData> games=
                gameDAO.listGames();

        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(
            CreateGameRequest request)
            throws Exception{

        if(authDAO.getAuth(
                request.authToken())==null){

            throw new UnauthorizedException();
        }

        GameData game=
                gameDAO.createGame(
                        request.gameName());

        return new CreateGameResult(
                game.gameID());
    }

    public void joinGame(
            JoinGameRequest request)
            throws Exception{

        AuthData auth=
                authDAO.getAuth(
                        request.authToken());

        if(auth==null){

            throw new UnauthorizedException();
        }

        GameData game=
                gameDAO.getGame(
                        request.gameID());

        if(game==null){

            throw new BadRequestException();
        }

        if(request.playerColor()
                .equals("WHITE")){

            if(game.whiteUsername()!=null){

                throw new AlreadyTakenException();
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

                throw new AlreadyTakenException();
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

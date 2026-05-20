package dataaccess;

public interface GameDAO {

    int createGame(GameData game)
            throws DataAccessException;

    GameData getGame(int id)
            throws DataAccessException;

    Collection<GameData> listGames()
            throws DataAccessException;

    void updateGame(GameData game)
            throws DataAccessException;

    void clear()
            throws DataAccessException;
}

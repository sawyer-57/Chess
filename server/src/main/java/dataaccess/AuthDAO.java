package dataaccess;

import model.AuthData;
import exception.DataAccessException;

public interface AuthDAO {

    void createAuth(AuthData auth)
            throws DataAccessException;

    AuthData getAuth(String token)
            throws DataAccessException;

    void deleteAuth(String token)
            throws DataAccessException;

    void clear()
            throws DataAccessException;
}

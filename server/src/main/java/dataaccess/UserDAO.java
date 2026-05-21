package dataaccess;

import model.UserData; 
import dataaccess.DataAccessException;

public interface UserDAO {

    void createUser(UserData user)
            throws DataAccessException;

    UserData getUser(String username)
            throws DataAccessException;

    void clear()
            throws DataAccessException;
}

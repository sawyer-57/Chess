package service;

import dataaccess.*;
import exception.*;
import model.*;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import service.results.LoginResult;
import service.results.RegisterResult;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO,
                       AuthDAO authDAO){

        this.userDAO=userDAO;
        this.authDAO=authDAO;
    }

    public RegisterResult register(RegisterRequest request)
        throws Exception {

        if(request.username()==null ||
        request.password()==null ||
        request.email()==null){

            throw new BadRequestException();
        }

        if(userDAO.getUser(request.username()) != null){

            throw new AlreadyTakenException();
        }

        String hashedPassword =
                BCrypt.hashpw(
                        request.password(),
                        BCrypt.gensalt());

        UserData user = new UserData(
                request.username(),
                hashedPassword,
                request.email());

        userDAO.createUser(user);

        String token=UUID.randomUUID().toString();

        AuthData auth=
                new AuthData(token,
                        request.username());

        authDAO.createAuth(auth);

        return new RegisterResult(
                request.username(),
                token);
    }

    public LoginResult login(LoginRequest request)
                throws Exception {

        if(request.username()==null ||
        request.password()==null){

                throw new BadRequestException();
        }

        UserData user=
                userDAO.getUser(
                        request.username());

        if(user == null ||
                !BCrypt.checkpw(
                        request.password(),
                        user.password())) {

            throw new UnauthorizedException();
        }

        String token=
                UUID.randomUUID().toString();

        AuthData auth=
                new AuthData(token,
                        user.username());

        authDAO.createAuth(auth);

        return new LoginResult(
                user.username(),
                token);
        } 

    public void logout(
        LogoutRequest request)
        throws Exception{

        AuthData auth=
                authDAO.getAuth(
                        request.authToken());

        if(auth==null){

            throw new UnauthorizedException();
        }

        authDAO.deleteAuth(
                request.authToken());
    }
}
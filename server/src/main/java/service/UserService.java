package service;

import dataaccess.*;
import exception.*;
import model.*;
import service.requests.*;
import service.results.*;

import java.util.UUID;


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

        UserData user=new UserData(
                request.username(),
                request.password(),
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

        if(user==null ||
        !user.password()
                .equals(request.password())){

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
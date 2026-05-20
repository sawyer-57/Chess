package service;

import dataaccess.*;
import model.*;
import service.requests.*;
import service.results.*;

import exception.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setup(){

        UserDAO userDAO =
                new MemoryUserDAO();

        AuthDAO authDAO =
                new MemoryAuthDAO();

        userService =
                new UserService(
                        userDAO,
                        authDAO);
    }

    @Test
    public void registerPositive()
            throws Exception{

        RegisterRequest request =
                new RegisterRequest(
                        "bob",
                        "password",
                        "email@test.com");

        RegisterResult result =
                userService.register(request);

        assertEquals(
                "bob",
                result.username());

        assertNotNull(
                result.authToken());
    }

    @Test
    public void registerNegative()
            throws Exception{

        RegisterRequest request =
                new RegisterRequest(
                        "bob",
                        "password",
                        "email@test.com");

        userService.register(request);

        assertThrows(
                AlreadyTakenException.class,
                () -> userService.register(request));
    }

    @Test
    public void loginPositive()
            throws Exception{

        userService.register(
                new RegisterRequest(
                        "bob",
                        "password",
                        "email"));

        LoginResult result =
                userService.login(
                        new LoginRequest(
                                "bob",
                                "password"));

        assertEquals(
                "bob",
                result.username());

        assertNotNull(
                result.authToken());
    }

    @Test
    public void loginNegative(){

        assertThrows(
                UnauthorizedException.class,
                () -> {

                    userService.login(
                            new LoginRequest(
                                    "bob",
                                    "wrongpassword"));
                });
    }

    @Test
    public void logoutPositive()
            throws Exception{

        RegisterResult result =
                userService.register(
                        new RegisterRequest(
                                "bob",
                                "password",
                                "email"));

        userService.logout(
                new LogoutRequest(
                        result.authToken()));

        assertTrue(true);
    }

    @Test
    public void logoutNegative(){

        assertThrows(
                UnauthorizedException.class,
                () -> {

                    userService.logout(
                            new LogoutRequest(
                                    "badtoken"));
                });
    }
}
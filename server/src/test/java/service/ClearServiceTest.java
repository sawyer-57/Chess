package service;

import dataaccess.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService clearService;

    @BeforeEach
    public void setup(){

        clearService =
                new ClearService(
                        new MemoryUserDAO(),
                        new MemoryAuthDAO(),
                        new MemoryGameDAO());
    }

    @Test
    public void clearPositive() throws DataAccessException {

        clearService.clear();

        assertTrue(true);
    }
}
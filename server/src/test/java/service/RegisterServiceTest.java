package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import service.requests.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterServiceTest {

    @Test
    public void registerPositiveTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);
        var result = service.register(new RegisterRequest("u1", "p1", "e1"));

        assertEquals("u1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);
        service.register(new RegisterRequest("u1", "p1", "e1"));

        assertThrows(ServiceException.class, () -> {
            service.register(new RegisterRequest("u1", "p2", "e2"));
        });
    }
}
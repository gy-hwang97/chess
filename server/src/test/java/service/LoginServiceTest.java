package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.requests.LoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginServiceTest {

    @Test
    public void loginPositiveTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        userDAO.createUser(new UserData("u1", "p1", "e1"));

        UserService service = new UserService(userDAO, authDAO);
        var result = service.login(new LoginRequest("u1", "p1"));

        assertEquals("u1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        userDAO.createUser(new UserData("u1", "p1", "e1"));

        UserService service = new UserService(userDAO, authDAO);

        assertThrows(ServiceException.class, () -> {
            service.login(new LoginRequest("u1", "wrong"));
        });
    }
}
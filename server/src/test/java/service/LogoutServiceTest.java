package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;
import service.requests.LogoutRequest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutServiceTest {

    @Test
    public void logoutPositiveTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));

        UserService service = new UserService(userDAO, authDAO);
        service.logout(new LogoutRequest("t1"));

        assertNull(authDAO.getAuth("t1"));
    }

    @Test
    public void logoutNegativeTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        assertThrows(ServiceException.class, () -> {
            service.logout(new LogoutRequest("bad"));
        });
    }
}
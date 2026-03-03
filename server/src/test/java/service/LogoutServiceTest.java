package service;

import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutServiceTest {

    @Test
    public void logoutPositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        authDAO.createAuth(new AuthData("t1", "u1"));

        LogoutService service = new LogoutService(authDAO);
        service.logout("t1");

        assertNull(authDAO.getAuth("t1"));
    }

    @Test
    public void logoutNegativeTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        LogoutService service = new LogoutService(authDAO);

        assertThrows(ServiceException.class, () -> {
            service.logout("bad");
        });
    }
}

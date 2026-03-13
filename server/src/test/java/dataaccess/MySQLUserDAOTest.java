package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {
    private MySQLUserDAO userDAO;

    @BeforeEach
    public void setup() throws Exception {
        MySQLDataAccess.configureDatabase();
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserPositive() throws Exception {
        UserData user = new UserData("u1", "p1", "e1@test.com");
        userDAO.createUser(user);

        UserData found = userDAO.getUser("u1");
        assertNotNull(found);
        assertEquals("u1", found.username());
    }

    @Test
    public void createUserNegativeDuplicate() throws Exception {
        UserData user = new UserData("u1", "p1", "e1@test.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void getUserPositive() throws Exception {
        userDAO.createUser(new UserData("u2", "p2", "e2@test.com"));

        UserData found = userDAO.getUser("u2");
        assertNotNull(found);
        assertEquals("u2", found.username());
    }

    @Test
    public void getUserNegativeNotFound() throws Exception {
        UserData found = userDAO.getUser("bad");
        assertNull(found);
    }

    @Test
    public void clearPositive() throws Exception {
        userDAO.createUser(new UserData("u3", "p3", "e3@test.com"));
        userDAO.clear();

        assertNull(userDAO.getUser("u3"));
    }

    @Test
    public void clearNegativeAlreadyEmpty() throws Exception {
        userDAO.clear();
        assertNull(userDAO.getUser("nobody"));
    }
}
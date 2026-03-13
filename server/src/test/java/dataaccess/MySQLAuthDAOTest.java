package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {
    private MySQLAuthDAO authDAO;

    @BeforeEach
    public void setup() throws Exception {
        MySQLDataAccess.configureDatabase();
        authDAO = new MySQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthPositive() throws Exception {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);

        AuthData found = authDAO.getAuth("token1");
        assertNotNull(found);
        assertEquals("user1", found.username());
    }

    @Test
    public void createAuthNegativeDuplicate() throws Exception {
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    public void getAuthPositive() throws Exception {
        authDAO.createAuth(new AuthData("token2", "user2"));

        AuthData found = authDAO.getAuth("token2");
        assertNotNull(found);
        assertEquals("user2", found.username());
    }

    @Test
    public void getAuthNegativeNotFound() throws Exception {
        assertNull(authDAO.getAuth("bad"));
    }

    @Test
    public void deleteAuthPositive() throws Exception {
        authDAO.createAuth(new AuthData("token3", "user3"));
        authDAO.deleteAuth("token3");

        assertNull(authDAO.getAuth("token3"));
    }

    @Test
    public void deleteAuthNegativeMissing() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("bad"));
    }

    @Test
    public void clearPositive() throws Exception {
        authDAO.createAuth(new AuthData("token4", "user4"));
        authDAO.clear();

        assertNull(authDAO.getAuth("token4"));
    }

    @Test
    public void clearNegativeAlreadyEmpty() throws Exception {
        authDAO.clear();
        assertNull(authDAO.getAuth("none"));
    }
}
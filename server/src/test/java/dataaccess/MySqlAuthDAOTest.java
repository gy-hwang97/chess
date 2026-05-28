package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTest {

    private MySqlAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token-abc", "alice");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token-abc");
        assertNotNull(retrieved);
        assertEquals("token-abc", retrieved.authToken());
        assertEquals("alice", retrieved.username());
    }

    @Test
    public void createAuthDuplicateThrows() throws DataAccessException {
        AuthData auth = new AuthData("token-xyz", "bob");
        authDAO.createAuth(auth);

        AuthData duplicate = new AuthData("token-xyz", "bob");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(duplicate));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token-1", "carol"));

        AuthData retrieved = authDAO.getAuth("token-1");
        assertNotNull(retrieved);
        assertEquals("token-1", retrieved.authToken());
        assertEquals("carol", retrieved.username());
    }

    @Test
    public void getAuthNotFoundReturnsNull() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("nonexistent");
        assertNull(retrieved);
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token-del", "dave"));

        authDAO.deleteAuth("token-del");

        assertNull(authDAO.getAuth("token-del"));
    }

    @Test
    public void deleteAuthNonexistentNoEffect() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("does-not-exist"));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token-1", "alice"));
        authDAO.createAuth(new AuthData("token-2", "bob"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token-1"));
        assertNull(authDAO.getAuth("token-2"));
    }
}
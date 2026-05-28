package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {

    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@byu.edu");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("alice");
        assertNotNull(retrieved);
        assertEquals("alice", retrieved.username());
        assertEquals("alice@byu.edu", retrieved.email());
        assertTrue(BCrypt.checkpw("password123", retrieved.password()));
    }

    @Test
    public void createUserDuplicateThrows() throws DataAccessException {
        UserData user = new UserData("bob", "secret", "bob@byu.edu");
        userDAO.createUser(user);

        UserData duplicate = new UserData("bob", "other", "bob2@byu.edu");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicate));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("carol", "mypass", "carol@byu.edu");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("carol");
        assertNotNull(retrieved);
        assertEquals("carol", retrieved.username());
        assertEquals("carol@byu.edu", retrieved.email());
    }

    @Test
    public void getUserNotFoundReturnsNull() throws DataAccessException {
        UserData retrieved = userDAO.getUser("nobody");
        assertNull(retrieved);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("dave", "pw", "dave@byu.edu"));
        userDAO.createUser(new UserData("eve", "pw", "eve@byu.edu"));

        userDAO.clear();

        assertNull(userDAO.getUser("dave"));
        assertNull(userDAO.getUser("eve"));
    }
}
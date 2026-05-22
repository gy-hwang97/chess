package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("alice", "pass123", "alice@email.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerAlreadyTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("alice", "pass123", "alice@email.com");
        userService.register(request);

        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    public void loginSuccess() throws Exception {
        userDAO.createUser(new UserData("alice", "pass123", "alice@email.com"));

        LoginRequest request = new LoginRequest("alice", "pass123");
        LoginResult result = userService.login(request);

        assertNotNull(result);
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginWrongPassword() throws Exception {
        userDAO.createUser(new UserData("alice", "pass123", "alice@email.com"));

        LoginRequest request = new LoginRequest("alice", "wrongpass");

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(request);
        });
    }

    @Test
    public void logoutSuccess() throws Exception {
        authDAO.createAuth(new AuthData("token123", "alice"));

        userService.logout("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    public void logoutInvalidToken() {
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout("badtoken");
        });
    }
}

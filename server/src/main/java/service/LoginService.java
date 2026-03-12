package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.request.LoginRequest;
import model.request.LoginResult;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        if (request == null ||
                request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            UserData user = userDAO.getUser(request.username());
            if (user == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            String storedHash = user.password();

            if (storedHash == null || storedHash.isBlank() || !storedHash.startsWith("$2")) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            boolean ok;
            try {
                ok = BCrypt.checkpw(request.password(), storedHash);
            } catch (IllegalArgumentException e) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            if (!ok) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            String token = UUID.randomUUID().toString();
            AuthData auth = new AuthData(token, request.username());
            authDAO.createAuth(auth);

            return new LoginResult(request.username(), token);

        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }
}

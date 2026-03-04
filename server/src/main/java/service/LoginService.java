package service;

import java.util.UUID;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.request.LoginRequest;
import model.result.LoginResult;

public class LoginService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, ServiceException {
        if (request == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (request.username() == null || request.password() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (request.username().isBlank() || request.password().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (!user.password().equals(request.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);

        return new LoginResult(request.username(), token);
    }
}

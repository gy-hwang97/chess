package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        if (request == null || isBlank(request.username()) || isBlank(request.password()) || isBlank(request.email())) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            if (userDAO.getUser(request.username()) != null) {
                throw new ServiceException(403, "Error: already taken");
            }

            UserData user = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(user);

            String token = generateToken();
            AuthData auth = new AuthData(token, request.username());
            authDAO.createAuth(auth);

            return new RegisterResult(request.username(), token);
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        if (request == null || isBlank(request.username()) || isBlank(request.password())) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            UserData user = userDAO.getUser(request.username());

            if (user == null || !user.password().equals(request.password())) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            String token = generateToken();
            AuthData auth = new AuthData(token, request.username());
            authDAO.createAuth(auth);

            return new LoginResult(request.username(), token);
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }

    public void logout(LogoutRequest request) throws ServiceException {
        if (request == null || isBlank(request.authToken())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        try {
            AuthData auth = authDAO.getAuth(request.authToken());

            if (auth == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            authDAO.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
package service;

import java.util.UUID;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.request.RegisterRequest;
import model.result.RegisterResult;

public class RegisterService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException, ServiceException {
        if (request == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        UserData oldUser = userDAO.getUser(request.username());
        if (oldUser != null) {
            throw new ServiceException(403, "Error: already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, request.username());
        authDAO.createAuth(auth);

        return new RegisterResult(request.username(), token);
    }
}

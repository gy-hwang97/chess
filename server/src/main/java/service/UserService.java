package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request)
            throws DataAccessException, AlreadyTakenException, BadRequestException {

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("bad request");
        }
        if (request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        UserData existing = userDAO.getUser(request.username());
        if (existing != null) {
            throw new AlreadyTakenException("already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), token);
    }
}

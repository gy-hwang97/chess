package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.request.RegisterRequest;
import model.request.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        if (request == null ||
                request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank() ||
                request.email() == null || request.email().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            UserData existing = userDAO.getUser(request.username());
            if (existing != null) {
                throw new ServiceException(403, "Error: already taken");
            }

            String hashed = BCrypt.hashpw(request.password(), BCrypt.gensalt());

            UserData user = new UserData(request.username(), hashed, request.email());
            userDAO.createUser(user);

            String token = UUID.randomUUID().toString();
            AuthData auth = new AuthData(token, request.username());
            authDAO.createAuth(auth);

            return new RegisterResult(request.username(), token);

        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }
}

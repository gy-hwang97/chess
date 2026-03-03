package dataaccess;

import java.util.HashMap;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> auths = new HashMap<>();

    public void clear() {
        auths.clear();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
}

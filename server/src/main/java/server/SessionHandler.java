package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ServiceException;
import service.UserService;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;

public class SessionHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public SessionHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            var result = userService.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (ServiceException e) {
            ctx.status(e.statusCode());
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);
            ctx.status(200);
            ctx.result(gson.toJson(new EmptyResponse()));
        } catch (ServiceException e) {
            ctx.status(e.statusCode());
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }
}
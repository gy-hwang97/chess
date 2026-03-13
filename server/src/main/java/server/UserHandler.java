package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ServiceException;
import service.UserService;
import service.requests.RegisterRequest;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            var result = userService.register(request);
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
}
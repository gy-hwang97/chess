package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;
import service.ServiceException;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        try {
            clearService.clear();
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
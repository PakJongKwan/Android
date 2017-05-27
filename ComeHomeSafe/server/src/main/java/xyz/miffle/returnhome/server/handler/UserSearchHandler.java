package xyz.miffle.returnhome.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import xyz.miffle.returnhome.server.comm.Commands;
import xyz.miffle.returnhome.server.database.Database;
import xyz.miffle.returnhome.server.util.Data;
import xyz.miffle.returnhome.server.util.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dsm on 2017-05-19.
 */
public class UserSearchHandler implements HttpHandler {

    private final String[] KEYS = new String[]{ Commands.ID};

    private static final String TAG = "UserSearchHandler";

    @Override
    public void handle(HttpExchange httpExchange) {
        HashMap<String, String> args = HandlerUtil.initializeHandler(httpExchange);

        if (args == null) {
            Logger.log(TAG, "not enough arguments");
            HandlerUtil.sendFail(httpExchange);
            return;
        }

        if (!Data.hasCorrectKeys(args, KEYS)) {
            Logger.log(TAG, "not enough arguments");
            HandlerUtil.sendFail(httpExchange);
            return;
        }

        String id = args.get(Commands.ID);
        JSONObject result = Database.userSearch(id);
        HandlerUtil.sendSuccess(httpExchange, result);
    }
}

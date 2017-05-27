package xyz.miffle.returnhome.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xyz.miffle.returnhome.server.comm.Commands;
import xyz.miffle.returnhome.server.database.Database;
import xyz.miffle.returnhome.server.util.Data;
import xyz.miffle.returnhome.server.util.Logger;

import java.util.HashMap;

/**
 * Created by dsm on 2017-04-28.
 */
public class FriendListHandler implements HttpHandler {

    private static final String TAG = "FriendListHandler";

    private final String[] KEYS = new String[]{Commands.ID, Commands.TYPE };
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

        String type = args.get(Commands.TYPE);
        String id = args.get(Commands.ID);

        if (Commands.ACCEPTED.equals(type)) {
            HandlerUtil.sendSuccess(httpExchange, Database.getFriends(id));
        } else if (Commands.UNACCEPTED.equals(type)) {
            HandlerUtil.sendSuccess(httpExchange, Database.getUnAcceptedFriendsList(id));
        } else {
            HandlerUtil.sendFail(httpExchange);
        }
    }
}

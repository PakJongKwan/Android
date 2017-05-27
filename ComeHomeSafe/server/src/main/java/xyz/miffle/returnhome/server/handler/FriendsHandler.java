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
public class FriendsHandler implements HttpHandler {
    private static final String TAG = "FriendsHandler";

    private final String[] KEYS = new String[]{Commands.ID, Commands.DATA, Commands.TYPE };

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
        String friendsId = args.get(Commands.DATA);
        String type = args.get(Commands.TYPE);

        if (Commands.ACCEPTED.equals(type)) {
            if (Database.acceptFriend(id, friendsId)) {
                HandlerUtil.sendSuccess(httpExchange);
            } else {
                HandlerUtil.sendFail(httpExchange);
            }
        } else if (Commands.UNACCEPTED.equals(type)) {
            if (Database.addFriend(id, friendsId)) {
                HandlerUtil.sendSuccess(httpExchange);
            } else {
                HandlerUtil.sendFail(httpExchange);
            }
        } else {
            HandlerUtil.sendFail(httpExchange);
        }

    }
}

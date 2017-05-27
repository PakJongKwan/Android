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
public class LocationHandler implements HttpHandler{
    private static final String TAG = "LocationHandler";

    private final String[] KEYS = new String[]{Commands.ID, Commands.TYPE};

    @Override
    public void handle(HttpExchange httpExchange) {
        HashMap<String, String> args = HandlerUtil.initializeHandler(httpExchange);
        if (args == null) {
            Logger.log(TAG, "args is null");
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

        if (Commands.GET_LOCATION.equals(type)) {
            JSONObject result = Database.getLastestLocation(id);

            System.out.println(result.toJSONString());

            if (result == null) {
                HandlerUtil.sendFail(httpExchange);
            } else {
                HandlerUtil.sendSuccess(httpExchange, result);
            }
        } else if (Commands.INSERT_LOCATION.equals(type)) {
            String x = args.get(Commands.X);
            String y = args.get(Commands.Y);

            if (x != null && y != null) {

                Logger.log(TAG, id + " : " + x + " : " + y);
                if (Database.insertLocation(id, x, y)) {
                    HandlerUtil.sendSuccess(httpExchange);
                } else {
                    HandlerUtil.sendFail(httpExchange);
                }
            } else {
                HandlerUtil.sendFail(httpExchange);
            }
        }
    }
}

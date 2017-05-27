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
 * Created by dsm on 2017-05-17.
 */
public class PersonalHandler implements HttpHandler {

    private static final String TAG = "PersonalHandler";

    private static final String[] KEYS = new String[]{ Commands.TYPE };

    private static final String[] GET_KEYS = new String[] { Commands.ID, Commands.COLUMN };
    private static final String[] EDIT_KEYS = new String[] { Commands.ID, Commands.COLUMN, Commands.DATA };

    @Override
    public void handle(HttpExchange httpExchange) {
        HashMap<String, String> args = HandlerUtil.initializeHandler(httpExchange);

        if (args == null) {
            Logger.log(TAG, "통신 실패!");
            HandlerUtil.sendFail(httpExchange);
            return;
        }

        String type = args.get(Commands.TYPE);

        System.out.println(type);

        if (type == null) {
            HandlerUtil.sendFail(httpExchange);
            return;
        }

        if (Commands.GET.equals(type)) {
            if (!Data.hasCorrectKeys(args, GET_KEYS)) {
                Logger.log(TAG, "인자 부족");
            }

            String id = args.get(Commands.ID);
            String column = args.get(Commands.COLUMN);

            JSONObject result = Database.getPersonal(id, column);

            if (result == null) {
                HandlerUtil.sendFail(httpExchange);
            } else {
                HandlerUtil.sendSuccess(httpExchange, result);
            }
        } else if (Commands.EDIT.equals(type)) {
            if (!Data.hasCorrectKeys(args, EDIT_KEYS)) {
                Logger.log(TAG, "인자 부족");
            }

            String id = args.get(Commands.ID);
            String data = args.get(Commands.DATA);
            String column = args.get(Commands.COLUMN);

            if (Database.updatePersonal(id, column, data)) {
                HandlerUtil.sendSuccess(httpExchange);
            } else {
                HandlerUtil.sendFail(httpExchange);
            }
        }

    }
}

package xyz.miffle.returnhome.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.miffle.returnhome.server.comm.Commands;
import xyz.miffle.returnhome.server.database.Database;
import xyz.miffle.returnhome.server.util.Logger;

/**
 * Created by dsm on 2017-04-28.
 */
public class LoginHandler implements HttpHandler {
    private static final String TAG = "LoginHandler";

    @Override
    public void handle(HttpExchange httpExchange)  {

        if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            int contentLength = HandlerUtil.getContentLength(httpExchange.getRequestHeaders());

            byte[] clientByteData = HandlerUtil.readStream(contentLength, httpExchange.getRequestBody());

            if (clientByteData == null) {
                Logger.log(TAG, "클라이언트에서 읽어올 수 없습니다.");
                HandlerUtil.sendSuccess(httpExchange);
                return;
            }

            JSONObject clientData;

            try {
                clientData = (JSONObject) new JSONParser().parse(new String(clientByteData));
            } catch (ParseException e) {
                Logger.log(TAG, "클라이언트 데이터가 JSON데이터가 아닙니다.");
                HandlerUtil.sendFail(httpExchange);
                return;
            }

            String id;
            String pw;

            try {
                id = clientData.get(Commands.ID).toString();
                pw = clientData.get(Commands.PW).toString();

                if (Database.login(id, pw)) {
                    HandlerUtil.sendSuccess(httpExchange);
                    return;
                } else {
                    HandlerUtil.sendFail(httpExchange);
                    return;
                }
            } catch (NullPointerException e) {
                Logger.log(TAG, "JSON에서 필요한 값을 얻어올 수 없습니다.");
                HandlerUtil.sendFail(httpExchange);
                return;
            }

        } else {
            Logger.log(TAG,"잘못된 요청입니다.");
        }
    }
}

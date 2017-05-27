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
public class RegisterHandler implements HttpHandler {
    private final String TAG = "RegisterHandler";

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
            String name;
            String address;
            String emergencyPhone;
            String phone;

            try {
                id = clientData.get(Commands.ID).toString();
                pw = clientData.get(Commands.PW).toString();
                name = clientData.get(Commands.NAME).toString();
                address = clientData.get(Commands.ADDRESS).toString();
                emergencyPhone = clientData.get(Commands.EMERGENCY_PHONE).toString();
                phone = clientData.get(Commands.PHONE).toString();

                if (Database.register(id, pw, name, address, emergencyPhone, phone)) {
                    HandlerUtil.sendSuccess(httpExchange);
                    return;
                } else {
                    HandlerUtil.sendFail(httpExchange);
                    return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Logger.log(TAG, "JSON에서 필요한 값을 얻어올 수 없습니다.");
                HandlerUtil.sendFail(httpExchange);
                return;
            }

        } else {
            Logger.log(TAG,"잘못된 요청입니다.");
        }
    }
}

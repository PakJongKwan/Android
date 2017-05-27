package xyz.miffle.returnhome.server.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.miffle.returnhome.server.comm.Commands;
import xyz.miffle.returnhome.server.util.Data;
import xyz.miffle.returnhome.server.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by dsm on 2017-04-28.
 */
public class HandlerUtil {
    private static final String TAG = "HandlerUtil";

    public static int getContentLength(Headers httpHeader) {
        if (httpHeader != null) {
            return Integer.parseInt(httpHeader.getFirst("Content-length"));
        }

        return -1;
    }

    public static byte[] readStream(int contentLength, InputStream inputStream) {
        byte[] data = new byte[contentLength];

        try {
            inputStream.read(data);
        } catch (IOException e) {
            Logger.log(TAG, "Failed to read on stream");
            return null;
        }

        return data;
    }

    public static boolean sendFail(HttpExchange httpExchange) {
        if (httpExchange != null) {
            return sendResult(httpExchange, Commands.NO_JSON);
        }

        return false;
    }

    public static boolean sendSuccess(HttpExchange httpExchange) {
        if (httpExchange != null) {
            return sendResult(httpExchange, Commands.OK_JSON);
        }

        return false;
    }

    public static boolean sendSuccess(HttpExchange httpExchange, JSONObject data) {
        if (httpExchange != null && data != null) {
            data.put(Commands.RESULT, Commands.OK);
            return sendResult(httpExchange, data.toJSONString());
        }

        return false;
    }

    public static HashMap<String, String> initializeHandler(HttpExchange httpExchange) {
        if (httpExchange == null) {
            Logger.log(TAG, "argument is not normal");
            return null;
        }

        if (!httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Logger.log(TAG, "This is not post access.");
            return null;
        }

        int contentLength = HandlerUtil.getContentLength(httpExchange.getRequestHeaders());

        byte[] clientByteData = HandlerUtil.readStream(contentLength, httpExchange.getRequestBody());

        if (clientByteData == null) {
            Logger.log(TAG, "Can't read on client.");
            HandlerUtil.sendSuccess(httpExchange);
            return null;
        }

        JSONObject clientData;

        try {
            clientData = (JSONObject) new JSONParser().parse(new String(clientByteData));
        } catch (ParseException e) {
            Logger.log(TAG, "Client data is not json data.");
            return null;
        }


        return Data.jsonToStrHashmap(clientData);
    }

    private static boolean sendResult(HttpExchange httpExchange, String result) {
        if (httpExchange != null) {
            try {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, result.getBytes().length);
                httpExchange.getResponseBody().write(result.getBytes());
                Logger.log(TAG, result);
            } catch (IOException e) {
                Logger.log(TAG, "Failed to send fail message to client.");
                return false;
            }

            return true;
        }

        return false;
    }
}
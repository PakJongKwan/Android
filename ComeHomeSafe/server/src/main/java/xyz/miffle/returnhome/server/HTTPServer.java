package xyz.miffle.returnhome.server;

import com.sun.corba.se.spi.activation.Server;
import com.sun.net.httpserver.HttpServer;
import xyz.miffle.returnhome.server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by dsm on 2017-04-28.
 */
public class HTTPServer {
    private HttpServer httpServer;

    public HTTPServer() {
        InetSocketAddress addr = new InetSocketAddress(ServerConfig.SERVER_PORT);
        try {
            httpServer = HttpServer.create(addr, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpServer.createContext(ServerConstants.LOGIN_HANDLER, new LoginHandler());
        httpServer.createContext(ServerConstants.REGISTER_HANDLER, new RegisterHandler());
        httpServer.createContext(ServerConstants.PERSONAL_HANDLER, new PersonalHandler());
        httpServer.createContext(ServerConstants.FRIENDS_LIST_HANDLER, new FriendListHandler());
        httpServer.createContext(ServerConstants.FRIENDS_HANDLER, new FriendsHandler());
        httpServer.createContext(ServerConstants.LOCATION_HANDLER, new LocationHandler());
        httpServer.createContext(ServerConstants.USERSEARCH_HANDLER, new UserSearchHandler());
        httpServer.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        httpServer.start();
    }
}

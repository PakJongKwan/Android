package xyz.miffle.returnhome;

import xyz.miffle.returnhome.server.HTTPServer;
import xyz.miffle.returnhome.server.database.Database;
import xyz.miffle.returnhome.server.util.Logger;

/**
 * Created by dsm on 2017-04-28.
 */
public class ReturnHomeServer extends Thread {
    private HTTPServer httpServer;

    public ReturnHomeServer() {
        httpServer = new HTTPServer();
    }

    @Override
    public void start() {
        if (!Database.open()) {
            Logger.log("데이터베이스를 열 수 없습니다.");
            //return;
        }

        httpServer.start();

        while(true) {}
    }

    public static void main(String... args) {
        ReturnHomeServer returnHomeServer = new ReturnHomeServer();
        returnHomeServer.start();
        try {
            returnHomeServer.join();
        } catch (InterruptedException e) {
            Logger.log("ReturnHomeServer join 실패");
        }

        Logger.log("프로그램이 정상종료합니다.");
    }
}

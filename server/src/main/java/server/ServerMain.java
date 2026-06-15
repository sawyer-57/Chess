package server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8000);

        System.out.println("♕ 240 Chess Server");
    }
}

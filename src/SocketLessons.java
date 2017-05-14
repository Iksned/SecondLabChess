import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Тестовый класс, не для финальной версии
public class SocketLessons {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Server side");
        BufferedReader in = null;
        PrintWriter out = null;

        ServerSocket servers = null;
        Socket fromclient = null;

        // create server socket
        try {
            servers = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println("Couldn't listen to port 4444");
            System.exit(-1);
        }

        try {
            System.out.print("Waiting for a client...");
            fromclient = servers.accept();
            System.out.println("Client connected");
        } catch (IOException e) {
            System.out.println("Can't accept");
            System.exit(-1);
        }

        in = new BufferedReader(new
                InputStreamReader(fromclient.getInputStream()));
        out = new PrintWriter(fromclient.getOutputStream(), true);
        String input, output;

        System.out.println("Wait for messages");
        while ((input = in.readLine()) != null) {
            if (input.equalsIgnoreCase("exit")) break;
            StringBuilder st = new StringBuilder(input);

            output =  st.reverse().toString();
            out.println(output);
            System.out.println(input);
        }
        out.close();
        in.close();
        fromclient.close();
        servers.close();
    }
}

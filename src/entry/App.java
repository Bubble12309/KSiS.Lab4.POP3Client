package entry;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        try (Socket socket  = SSLSocketFactory.getDefault().createSocket("pop.mail.ru", 995)) {
            socket.setSoTimeout(2*60*1000);

            String response;

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.US_ASCII);
            Scanner scannerUser = new Scanner(System.in);

            response = reader.readLine();

            System.out.println("< " + response);
            if ( response.charAt(0) != '+') {
                throw new RuntimeException("POP3 Server is not accessible");
            }

            String mail = "xxxxxxxxxxxxxx";//secret
            osw.write("USER "+mail+"\r\n");
            osw.flush();
            System.out.println("> USER " + mail);

            response = reader.readLine();
            System.out.println("< " + response);
            if ( response.charAt(0) != '+') {
                throw new RuntimeException("Invalid username");
            }

            String password = "xxxxxxxxxxxxxxxxxxx"; //secret
            osw.write("PASS "+password+"\r\n");
            osw.flush();
            System.out.println("> PASS " + password);

            response = reader.readLine();
            System.out.println("< " + response);
            if ( response.charAt(0) != '+') {
                throw new RuntimeException("Invalid password");
            }

            String userInput = "";
            while (!userInput.equalsIgnoreCase("QUIT")) {
                userInput = scannerUser.nextLine().trim();
                osw.write(userInput + "\r\n");
                osw.flush();
                response = reader.readLine();
                System.out.println("< " + response);
                if (!response.contains("-ERR") && (userInput.equalsIgnoreCase("LIST") || userInput.contains("RETR"))) {
                    do {
                        response = reader.readLine();
                        System.out.println("< " + response);
                    } while (!response.equals("."));
                }
            }
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host exception raised");
        } catch (IOException ioe) {
            System.out.println("IOException raised");
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
    }
}
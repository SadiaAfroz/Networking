/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserverskeleton;

/**
 *
 * @author Faiza
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Faiza
 */
public class HTTPServer {

    static final int PORT = 6789;
    public static int workerThreadCount = 0;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        int id = 1;
        //SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:Ss z");
        try {

            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            while (true) {
                Socket s = serverConnect.accept();	//TCP Connection
                WorkerThread wt = new WorkerThread(s, id);
                Thread t = new Thread(wt);
                t.start();
                workerThreadCount++;
               // System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
                id++;
            }

        } catch (Exception e) {
        }

    }

}

class WorkerThread implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:Ss z");

    private int id = 0;

    public WorkerThread(Socket s, int id) {
        this.socket = s;

        try {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        } catch (Exception e) {
          //  System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);

        String input = null;

        //while (true) {
        try {
            input = br.readLine() + "\n";
           
            if (input != null && !input.contains("favicon.ico")) {

                //System.out.println("Here Input :jgjj " + input);

                if (input.contains("GET")) {
                    String MIME = null;
                    
                    
                    //extracting filename
                    String fileName, extension;
                    String[] get_response = input.split("/");
                    String file_name_nosplit = get_response[1].replaceAll(" HTTP", "");

                    if (file_name_nosplit.length() == 0) {
                        fileName = "index";
                        extension = "html";
                    } else {
                        String[] file_name_split = file_name_nosplit.split("\\.");
                        fileName = file_name_split[0];
                        extension = file_name_split[1];
                    }
                    System.out.println("GET Request: Browser requested for "+fileName + "." + extension);

                    StringBuilder inputpayload = new StringBuilder();
                    while (br.ready()) {
                        inputpayload.append((char) br.read());
                    }
                   // System.out.println("Payload get data is: " + inputpayload.toString());

                   
                    //requested File check
                    File file = new File(fileName + "." + extension);

                    if (!file.exists()) {
                        System.out.println("404 not Found : Requested file doesn't exist:");
                        pr.println("HTTP/1.0 404  Not Found\n"
                                + "Server: HTTP server/1.1\n"
                                + "Date: " + format.format(new java.util.Date()) + "\n"
                                + "Content-type: text/html; charset=UTF-8\n"
                                + "Content-Length: 38\n\n"
                                + "404 Not Found");
                        pr.flush();
                        pr.close();
                    } else {
                        
                        //file read in a byte array
                        byte[] data;
                        try (FileInputStream fis = new FileInputStream(file)) {
                            data = new byte[(int) file.length()];
                            fis.read(data);
                        }

                        try (DataOutputStream binaryOut = new DataOutputStream(this.os)) {
                            binaryOut.writeBytes("HTTP/1.0 200 OK\r\n");
                            if (extension.matches("png")) {

                                MIME = "image/png";
                                binaryOut.writeBytes("Content-Type: " + MIME + "\r\n");

                            } else if (extension.matches("jpg")) {

                                MIME = "image/jpg";
                                binaryOut.writeBytes("Content-Type: " + MIME + "\r\n");

                            } else if (extension.matches("jpeg")) {

                                MIME = "image/jpeg";
                                binaryOut.writeBytes("Content-Type: " + MIME + "\r\n");

                            } else if (extension.matches("gif")) {

                                MIME = "image/gif";
                                binaryOut.writeBytes("Content-Type: " + MIME + "\r\n");

                            } else if (extension.matches("pdf")) {

                                MIME = "application/pdf";
                                binaryOut.writeBytes("Content-Type: " + MIME + "\r\n");
                            }
                            binaryOut.writeBytes("Content-Length: " + data.length);
                            binaryOut.writeBytes("\r\n\r\n");
                            binaryOut.write(data);
                            binaryOut.close();
                        }
                    }
                } // POST
                else {
                    //code to read the post payload data
                    StringBuilder payload = new StringBuilder();
                    String headerLine;
                    while ((headerLine = br.readLine()).length() != 0) {
                        //System.out.println(headerLine);
                    }
                    while (br.ready()) {
                        payload.append((char) br.read());
                    }
                    System.out.println("Payload post data is: " + payload.toString());

                    System.out.println("Here Input post: " + input);
                    pr.println("HTTP/1.0 200 OK\n"
                            + "Server: HTTP server/1.1\n"
                            + "Date: " + format.format(new java.util.Date()) + "\n"
                            + "Content-type: text/html; charset=UTF-8\n"
                            + "Content-Length:" + payload.toString().length() + "\n\n"
                            + "<html>\n" +
"	<head>\n" +
"		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
"	</head>\n" +
"	<body>\n" +
"		<h1> Welcome to CSE 322 Offline 1</h1>\n" +
"		<h2> HTTP REQUEST TYPE-> POST</h2>\n" +
"		<h2> Post-> "+payload.toString().substring(5)+"</h2> \n" +
"		<div id=\"h2add\"> </div>\n" +
"		<form name=\"input\" action=\"http://localhost:6789/form_submitted.html\" method=\"post\">\n" +
"		Your Name: <input type=\"text\" name=\"user\" value=\"hhhh\">\n" +
"		<input type=\"submit\" value=\"Submit\">\n" +
"		</form>\n" +
"	</body>\n" +
"</html>");
                    pr.flush();

                    pr.close();
                }
                br.close();
            } else {
                //System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
            
            }
        } catch (IOException e) {
           // System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
            
        }
        //}

        try {
            this.is.close();
            this.os.close();
            this.socket.close();
        } catch (IOException e) {

        }

        HTTPServer.workerThreadCount--;
     // System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "+ HTTPServer.workerThreadCount);
    }
}

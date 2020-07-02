package smtpskeleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Scanner;

public class smtpSkeleton3 {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String mailServer = "Enter your mail server address";
        InetAddress mailHost = InetAddress.getByName(mailServer);
        InetAddress localHost = InetAddress.getLocalHost();
        Socket smtpSocket = new Socket(mailHost, 25);
         smtpSocket.setSoTimeout(20 * 1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
        PrintWriter pr = new PrintWriter(smtpSocket.getOutputStream(), true);
        String initialID = in.readLine();
        System.out.println("S: " + initialID);
        Scanner sc = new Scanner(new InputStreamReader(System.in));
        String str = "";
        String rep = "";
        String mstr = "";
        int i = 0;
        String[] recpt = null;
        int recp_id = 0;
        String from = null;
        String extension = null, file_name = null,sub = null,frm=null,to=null,cc=null;
        String attachEncoded = null;

        int FileAddFlag = 0;
        System.out.println("Want to Send Attachment ? Y/N");
        String wan_file = sc.nextLine();
        if (wan_file.equalsIgnoreCase("Y")) {
            System.out.println("Enter File Name : ");
            file_name = sc.nextLine();

            File file = new File(file_name);
            if (file.exists()) {
//                System.out.println("File doesn't exist");
//                System.out.println("Enter Right FileName: ");
//                

                byte[] byte_attach = new byte[(int) file.length()];
                FileInputStream fi = new FileInputStream(file);
                fi.read(byte_attach);
                fi.close();

                // File encoding
                attachEncoded = Base64.getMimeEncoder().encodeToString(byte_attach);
                FileAddFlag = 1;
            }
        }

        System.out.println("Write : HELO <LocalHostName>");
        String prev=null;
        while (i < 6) {
            prev=str;
            str = sc.nextLine();
            if (str.startsWith("RCPT TO")) {
                i = 2;
            }
            if (i == 0) {
                if (str.startsWith("HELO") || str.startsWith("EHLO")) {
                    if(!str.contains("Faiza")){
                        System.out.println("LocalHost Name is not Correct: localhost name is Faiza");
                        i=0;
                        continue;
                    }
                    System.out.println("C: " + str);
                    pr.println(str);
                    try {
                        rep = in.readLine();
                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("250")) {
                        i++;
                    }
                } else {
                    System.out.println("Initiates your dialog by responding with a HELO command");
                }
            } else if (i == 1)//wait state in the class diagram
            {
                if (str.startsWith("MAIL FROM")) {
                    System.out.println("C: " + str);
                    pr.println(str);
                    try {
                        rep = in.readLine();

                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("250")) {
                        i++;
                        from = str.substring(str.indexOf("<") + 1, str.indexOf(">"));

                    } else {
                        System.out.println("Please use the command with format MAIL FROM:<sender_address>");
                    }
                }
            } else if (i == 2)//envelpoe created state
            {
                if (str.startsWith("RCPT TO")) {
                    System.out.println("C: " + str);
                    pr.println(str);
                    try {
                        rep = in.readLine();

                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("250")) {
//                        recpt[recp_id]=str.substring(str.indexOf("<") + 1, str.indexOf(">"));
//                        recp_id++;
                        i++;
                    } else {
                        System.out.println("Please use the command with format RCPT TO:<reciever_address>");
                    }
                } else if (str.startsWith("RSET")) {
                    pr.println(str);
                    try {
                        rep = in.readLine();

                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    i = 1;
                    System.out.println("Reset. Start from senderaddress.");
                }
            } else if (i == 3)//recipient set state
            {
                if (str.startsWith("DATA")) {
                    System.out.println("C: " + str);
                    pr.println(str);
                    try {
                        rep = in.readLine();

                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("354")) {
                        i++;
                    } else {
                        System.out.println("Use DATA command to start sending mail and to stop writing mail use a single \".\"");
                    }
                } else if (str.startsWith("RSET")) {
                    pr.println(str);
                     try {
                        rep = in.readLine();

                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    i = 1;
                    System.out.println("Reset. Start from senderaddress.");
                }
                else{
                   System.out.println("Command DATA or RSET ");
                   i=3;
                }
            } else if (i == 4) {
                String data=null;
                
                if (str.equalsIgnoreCase(".")) {
                    
                    if(!prev.isEmpty()){
                        //System.out.println("Right format"+prev+"kkkkk");
                         System.out.println("Enter Blankline and then Enter \\\".\\\" ");
                         i=4;
                         continue;
                    }
//                    else System.out.println("wrong format:"+prev+"hello" );
                    System.out.println("C: " + str+"  "+FileAddFlag);
                   
//                    mstr = "Subject: Iiii\nFrom: farhanferoz17@gmail.com\nTo: sadiafaiza30@gmail.com\n"
//                            + "Cc: wafizara68@gmail.com\n\n" + mstr;

                    if (FileAddFlag == 1) {

                         mstr = "To: "+to+"\n"
                                + "From: "+frm+"\n"
                                + "MIME-Version: 1.0\n"
                                + "Subject: "+sub+"\n"
                                + "Content-Type: multipart/mixed; boundary=12345\n\n"
                                + "--12345\n"
                                + mstr + "\n"
                                + "--12345\n"
                                + "Content--Type: application/octet-stream; name=\\\"" + file_name + "\\\"\n"
                                + "Content-Disposition: attachment; filename=\\\"" + file_name + "\\\"\n"
                                + "Content-Transfer-Encoding: base64\n"
                                + "\n"
                                + "\n"
                                + attachEncoded
                                + "--12345--\n\n"
                                + ".\n\n";
                    } else {
                        mstr = mstr + "\n" + str;
                    }

                    pr.println(mstr);
                    pr.flush();
                    try {
                        rep = in.readLine();
                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("250")) {
                        i++;
                    }
                } else {

                    System.out.println("C: " + str);
                    if (str.contains("Subject: ")) {
                        sub = str.substring(9);
                        if(FileAddFlag == 0)
                            mstr += "Subject: " + sub + "\n";
                    } else if (str.contains("From: ")) {
                        frm = str.substring(6);
                        if(FileAddFlag == 0)
                             mstr += "From: " + frm + "\n";
                    } else if (str.contains("To: ")) {
                        to = str.substring(4);
                        if(FileAddFlag == 0)
                            mstr += "To: " + to + "\n";
                    } else if (str.contains("CC: ")) {
                       cc = str.substring(4);
                        if(FileAddFlag == 0)
                             mstr += "Cc: " + cc + "\n";
                    } else {
                        mstr = mstr + "\n" + str;
                    }

                }
            } else if (i == 5) {
                if (str.equalsIgnoreCase("QUIT")) {
                    System.out.println("C: " + str);
                    pr.println(str);
                    try {
                        rep = in.readLine();
                        System.out.println("S: " + rep);
                    } catch (Exception e) {
                        System.err.println("Time out 20s");
                        return;
                    }
                    if (rep.startsWith("221")|| rep.startsWith("500")) {
                        i = 6;
                    }
                }
            }
        }

    }
}

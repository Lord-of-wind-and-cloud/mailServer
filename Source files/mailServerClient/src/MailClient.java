import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MailClient {

    private static BufferedReader KeyboardInput = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        System.out.print("the following is a simple mail server\n");

        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.print("you can choose:\n1.send email\n2.receive email\n3.exit");
        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~\n\n");


        while (true) {
            try {
                int input = Integer.parseInt(KeyboardInput.readLine());

                if (input == 1) {
                    sendChoice();
                } else if (input == 2) {
                    receiveChoice();
                } else if (input == 3) {
                    System.out.println("you will exit the program. Bye~\n");
                    KeyboardInput.close();
                    return;
                } else {
                    System.out.println("you input the wrong number, please re-choose the right number!\n");
                }

                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                System.out.print("you can choose:\n1.send mail\n2.receive email\n3.exit");
                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                System.out.println("error: NoSuchElementException!\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error: IOException!\n");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("error: throw exception, the wrong input format, it should be number , please re-ipnut!\n");
            }


        }

    }

    public static void sendChoice() {

        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.print("you can choose\n1.send qq email\n2.163 email\n3.return to main interface");
        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");


        while (true) {
            try {
                int input = Integer.parseInt(KeyboardInput.readLine());

                if (input == 1) {
                    send("smtp.qq.com", 25);
                    return;
                } else if (input == 2) {
                    send("smtp.163.com", 25);
                    return;
                } else if (input == 3) {

                    return;
                } else {
                    System.out.println("you input the wrong number, please re-choose the right number!\n");
                }

                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                System.out.print("you can choose\n1.qq email\n2.163 email\n3.return to main interface");
                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                System.out.println("error: NoSuchElementException!\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error: IOException!\n");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("error: the wrong input format, it should be number, please re-ipnut!\n");
            }


        }
    }


    public static void receiveChoice() {
        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.print("you can choose\n1.receive qq email\n2.receive 163 email\n3.return to main interfeace");
        System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");


        while (true) {
            try {
                int input = Integer.parseInt(KeyboardInput.readLine());

                if (input == 1) {
                    receive("pop.qq.com", 110);
                    return;
                } else if (input == 2) {
                    receive("pop.163.com", 110);
                    return;
                } else if (input == 3) {

                    return;
                } else {
                    System.out.println("wrong input, please select the right number, please re-input!\n");
                }
                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                System.out.print("you can choose\n1.receive qq email\n2.receive 163 email\n3.return to main interfeace");
                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                System.out.println("error: NoSuchElementException!\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error: IOException!\n");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("error: throw exception, the wrong input format, it should be number , please re-ipnut!\n");
            }


        }
    }


    public static void send(String mailbox, int port) {

        try {
            Socket client = new Socket(mailbox, port);
            System.out.println("\nconnecting to server...\n");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String feedback = bufferedreader.readLine();
            feedback = feedback.substring(0, 3);
            if (feedback.equals("220")) {
                //input EHLO order, need to input the user-name
                System.out.print("please input nickname(the part before @)\n");
                String nickname;
                nickname = KeyboardInput.readLine();

                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                System.out.print("\nverifying...\n");
                dataOutputStream.writeBytes("ehlo " + nickname + "\r\n");
                dataOutputStream.flush();

                feedback = bufferedreader.readLine();
                feedback = feedback.substring(0, 3);

                if (!feedback.equals("250")) {
                    System.out.println("error: wrong input! return to last level interface\n");
                    return;
                }
                if (mailbox == "smtp.163.com") {
                    for (int i = 0; i < 6; i++) {
                        feedback = bufferedreader.readLine();
                    }
                } else {
                    for (int i = 0; i < 7; i++) {
                        feedback = bufferedreader.readLine();
                    }
                }

                //input verifying order, nickname, keyword
                dataOutputStream.writeBytes("AUTH LOGIN\r\n");
                dataOutputStream.flush();
                feedback = bufferedreader.readLine();
                feedback = feedback.substring(0, 3);
                if (!feedback.equals("334")) {
                    System.out.println("error:wrong order! return last level menu\n");
                } else {
                    dataOutputStream.writeBytes(Base64.getEncoder().encodeToString(nickname.getBytes()) + "\r\n");
                    dataOutputStream.flush();
                    feedback = bufferedreader.readLine();
                    feedback = feedback.substring(0, 3);
                    if (!feedback.equals("334")) {
                        System.out.println("error: the wrong format of nickname! return to last level menu\n");
                    } else {
                        System.out.println("input(nickname) success\n");
                        System.out.print("please input authorization code(be sensitive of lower case or upper case)\n");
                        String authorizationCode;
                        authorizationCode = KeyboardInput.readLine();
                        System.out.print("\nverifying...\n");
                        dataOutputStream.writeBytes(Base64.getEncoder().encodeToString(authorizationCode.getBytes()) + "\r\n");
                        dataOutputStream.flush();
                        feedback = bufferedreader.readLine();
                        feedback = feedback.substring(0, 3);
                        if (!feedback.equals("235")) {
                            System.out.println("error: authorization code or keyword wrong! return to last level menu\n");

                        } else {
                            System.out.println("login success\n");
                            String userAddress;
                            if (mailbox == "smtp.163.com") {
                                userAddress = nickname + "@163.com";
                            } else {
                                userAddress = nickname + "@qq.com";
                            }
                            dataOutputStream.writeBytes("mail from:<" + userAddress + ">\r\n");
                            dataOutputStream.flush();
                            feedback = bufferedreader.readLine();
                            System.out.println("please input receiver's email\n");
                            String receiverAddress;
                            receiverAddress = KeyboardInput.readLine();
                            System.out.print("\nverifying...\n");
                            dataOutputStream.writeBytes("rcpt to:<" + receiverAddress + ">\r\n");
                            dataOutputStream.flush();
                            feedback = bufferedreader.readLine();
                            feedback = feedback.substring(0, 3);
                            if (!feedback.equals("250")) {
                                System.out.println("error: wrong email of receiver! return to last level menu\n");
                            } else {
                                System.out.println("please input the subject of email(one line):\n");
                                String subject;
                                subject = KeyboardInput.readLine();
                                System.out.print("\nverifying...\n");

                                System.out.println("please input the body of email(one line):");
                                String content;
                                content = KeyboardInput.readLine();
                                System.out.print("\nverifying...\n");

                                dataOutputStream.writeBytes("data\r\n");
                                dataOutputStream.flush();
                                feedback = bufferedreader.readLine();
                                System.out.println("please input the fake mail sender:\n");
                                String sender;
                                sender = KeyboardInput.readLine();
                                dataOutputStream.writeBytes("from:" + sender + ".com" + "\r\n");
                                dataOutputStream.writeBytes("to:" + receiverAddress + "\r\n");
                                dataOutputStream.writeBytes("subject:" + subject + "\r\n");
                                dataOutputStream.writeBytes("content:" + content + "\r\n");
                                dataOutputStream.flush();

                                System.out.println(bufferedreader.readLine() + "\nsending success!");
                                dataOutputStream.writeBytes("quit");
                                dataOutputStream.flush();
                                System.out.println("\ndisconnecting...");
                                bufferedreader.close();
                                dataOutputStream.close();
                            }
                        }
                    }
                }
            } else {
                System.out.println("\nerror: unknown error\n");
            }
            client.close();

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            System.out.println("\nerror: UnknownHostException!\n");
        } catch (IOException e2) {
            e2.printStackTrace();
            System.out.println("\nerror: IOException!\n");
        }
    }


    public static void receive(String mailbox, int port) {

        try {

            System.out.println("\nconnecting...\n");
            Socket client = new Socket(mailbox, port);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String feedback = bufferedreader.readLine();
            feedback = feedback.substring(0, 3);

            if (feedback.equals("+OK")) {
                System.out.println("client has connected to " + mailbox + " server");

                //input user order, please input nickname
                System.out.println("\nplease input nickname(the part before @)\n");
                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());


                String nickname;
                nickname = KeyboardInput.readLine();
                System.out.print("\nverifying...\n");
                dataOutputStream.writeBytes("user " + nickname + "\r\n");
                dataOutputStream.flush();
                feedback = bufferedreader.readLine();
                feedback = feedback.substring(0, 3);
                if (!feedback.equals("+OK")) {
                    System.out.println("error: wrong input! return to last level menu");
                    client.close();
                    return;
                }

                // input authorization code/keyword
                System.out.println("\nplease input authorization code (be sensitive of upper or lower case):\n");
                String keyword;
                keyword = KeyboardInput.readLine();
                System.out.print("\nverifying...\n");
                dataOutputStream.writeBytes("pass " + keyword + "\r\n");
                dataOutputStream.flush();
                feedback = bufferedreader.readLine();
                feedback = feedback.substring(0, 3);


                if (!feedback.equals("+OK")) {
                    System.out.println("error: the wrong authorization code! return to last level menu\n");
                    client.close();
                    return;
                }
                // inquire about the sum of mails and the size of space used
                dataOutputStream.writeBytes("stat\r\n");
                dataOutputStream.flush();
                feedback = bufferedreader.readLine();
                if (!feedback.substring(0, 3).equals("+OK")) {
                    System.out.println("error: connecting failed! return to last level menu\n");
                    client.close();
                    return;
                }
                StringTokenizer information = new StringTokenizer(feedback, " ");
                System.out.println(information.nextToken());
                int mailNumber = Integer.parseInt(information.nextToken());
                String mailMemory = information.nextToken();

                System.out.print("\nthe num of emails:" + mailNumber + "\n");
                System.out.print("\nthe space email occupied:" + mailMemory + "\n");

                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                System.out.print("\nyou can choose:\n1.list to see the minimum window of emails\n2.see the content of emails\n3.delete emails\n4.exit");
                System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

                int input = 0;
                input = Integer.parseInt(KeyboardInput.readLine());
                System.out.print("\nprocessing...\n");

                while (true) {
                    if (input == 1) {
                        dataOutputStream.writeBytes("list\r\n");
                        dataOutputStream.flush();
                        while (true) {
                            feedback = bufferedreader.readLine();
                            System.out.println(feedback);
                            if (feedback.charAt(0) == '.') {
                                break;
                            }
                        }
                    } else if (input == 2) {
                        System.out.print("\nthe num of emails:" + mailNumber);
                        System.out.print("\nplease input the number of the email you want to check\n");
                        int mailIndexing = Integer.parseInt(KeyboardInput.readLine());
                        System.out.print("\nprocessing...\n");
                        dataOutputStream.writeBytes("retr " + mailIndexing + "\r\n");
                        dataOutputStream.flush();
                        feedback = bufferedreader.readLine();
                        feedback = feedback.substring(0, 3);
                        if (!feedback.equals("+OK")) {
                            System.out.print("\nerror: wrong input number, return last level menu.\n");
                            break;
                        }
                        while (true) {
                            feedback = bufferedreader.readLine();
                            System.out.println(feedback);
                            if (feedback == null || feedback.length() == 0) {
                                continue;
                            } else if (feedback.charAt(0) == '.') {
                                break;
                            }
                        }
                    } else if (input == 3) {
                        System.out.print("\nthe number of emails:" + mailNumber);
                        System.out.print("\nplease input the number of email:\n");
                        int mailIndexing = Integer.parseInt(KeyboardInput.readLine());
                        System.out.print("\nprocessing...\n");
                        dataOutputStream.writeBytes("dele " + mailIndexing + "\r\n");
                        dataOutputStream.flush();
                        bufferedreader.readLine();
                        System.out.print("\ndelete successful\n");
                        mailNumber--;
                    } else if (input == 4) {
                        dataOutputStream.writeBytes("quit");
                        dataOutputStream.flush();

                        bufferedreader.close();
                        dataOutputStream.close();
                        client.close();
                        System.out.println("bye!");
                        return;
                    }

                    System.out.println("\nthe number of emails:" + mailNumber);
                    System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                    System.out.print("\nyou can choose:\n1.list to see the minimum window of emails\n2.see the content of emails\n3.delete emails\n4.exit\n");
                    System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                    input = Integer.parseInt(KeyboardInput.readLine());
                    System.out.print("\nprocessing...\n");
                }
            } else {
                System.out.println("unknown error!");
            }
            client.close();

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            System.out.println("error: UnknownHostException!\n");
        } catch (IOException e2) {
            e2.printStackTrace();
            System.out.println("error: IOException!\n");
        } catch (NoSuchElementException e3) {
            e3.printStackTrace();
            System.out.println("error: NoSuchElementException!\n");
        }
    }
}


import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
public class smtpServerSession implements Runnable {
	private Socket s;
	private BufferedReader br;
	private PrintStream printStream;
	private Base64 b64=new Base64();
	String username;
	private static Properties mail=new Properties();
	static{
		try {
			InputStream is=new FileInputStream("mail.properties");
			mail.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static  Properties user=new  Properties();
	static{
		try {
			InputStream is=new FileInputStream("user.properties");
			user.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String  str; //读取的内容
//	List<String> rcpttoaddress=new ArrayList<String>();

	String sendName=null;  //发件人的名字 xiaohong
	String SmailName=null;   //发件人的域名 zkh.com
	String RmailName=null;  //收件人的域名 dzz.com
	String	SallName=null;  //发送人的整个域名如：<xiaohong@zkh.com>
	List<String> RallName=new ArrayList<String>();  //把所有要发送的域名，保存在链表中。如<duan@dzz>,<leikai@lk.com>


	public smtpServerSession(Socket s) {
		this.s = s;
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			printStream = new PrintStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		try {
			printStream.println("220 wp's smtp server is ready!");
			String command=null;
			String  argument=null;
			while((command=br.readLine())!=null){
				int index=command.indexOf(" ");
				if(index>0){
					argument=command.substring(index+1);
					command=command.substring(0,index);
				}
				System.out.println(command+" "+argument);
				if(command.equalsIgnoreCase("HELO")){
					helo();

				}else if(command.equalsIgnoreCase("EHLO")){
					ehlo();
				}else if(command.equalsIgnoreCase("AUTH")){
					auth();
				}
				else if(command.equalsIgnoreCase("MAIL")){
					mail();   //<xiaohong@zkh.com>
					int index1=argument.indexOf("<");
					int index2=argument.indexOf("@");
					int index3=argument.indexOf(">");

					sendName=argument.substring(index1+1,index2);
					SmailName=argument.substring(index2+1,index3);

					SallName=argument.substring(index1, index3+1);
					// System.out.println(sendName+" "+SmailName+" "+SallName);
				}
				else if(command.equalsIgnoreCase("RCPT")){
					rcpt();   //<xiaohong@zkh.com>
					int index1=argument.indexOf("<");
					int index2=argument.indexOf("@");
					int index3=argument.indexOf(">");

					String RallName1=argument.substring(index1, index3+1);//<xiaohong@zkh.com>
					RallName.add(RallName1);
					//	 System.out.println(receiveName+" "+rmailName+" "+SallName);
				}
				else if(command.equalsIgnoreCase("DATA")){
					data();
					while((command=br.readLine())!=null){
						if(command.equals(".")) break;
						str=str+command+"\n";   //接受到的内容放在str中
					}
					System.out.println(str);
					printStream.println("250 ok");
				}
				else if(command.equalsIgnoreCase("QUIT")){
					quit();
					//判断域名
					YM();
					break;
				}else{
					printStream.println("500 error");
				}
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void YM(){
		for(int i=0;i<RallName.size();i++){
			String rallName=RallName.get(i);
			System.out.println(RallName.get(i));
			//<lifei@fly.com>
			//<duanzhuzhu@dzz.com>
			int index1=rallName.indexOf("<");
			int index2=rallName.indexOf("@");
			int index3=rallName.indexOf(">");
			String ym=rallName.substring(index2+1, index3);
			String receiveName=rallName.substring(index1+1, index2);
			String RallName=rallName.substring(index1, index3+1);
			if(doYM(ym)){     //域名如果是zkm.com,就进行保存内容。否则，就进行转发
				//保存文件
				saveFile(str,sendName,receiveName);
			}else{
				//进行转发
				String ip=getIp(ym);  //通过域名后缀（dzz.com）,得到对应的ip
				doZF(ip,SallName,RallName,str);
			}
		}
	}
	private String getIp(String ym) {
		String ip=mail.getProperty(ym);
		return ip;
	}
	private void doZF(String ip, String sallName2, String rallName2, String str2) {
		try {

			Socket s=new Socket(ip,25);
			System.out.println("retransmittng starting...");
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream printStream=new PrintStream(s.getOutputStream());
			br.readLine();
			printStream.println("helo ");
			//	System.out.println("helo");
			br.readLine();
			printStream.println("MAIL FROM: " +sallName2);
			//	System.out.println("mail");
			br.readLine();
			printStream.println("RCPT TO: "+rallName2);
			System.out.println("rcpt");
			printStream.println("data");
			//	System.out.println("data");
			br.readLine();
			printStream.println(str2);
			printStream.println(".");
			//	System.out.println(str2);
			//printStream.println(".");
			br.readLine();
			printStream.println("quit");
			if(br.readLine().substring(0, 4).equalsIgnoreCase("221 ")){
				s.close();
				printStream.close();
				br.close();
			}
			System.out.println("sent successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void saveFile(String str2, String sendName2, String receiveName2) {
		try {
			DateFormat df1 = new SimpleDateFormat("yyyyMMdd-HHmmss");
			String currenttime = df1.format(new Date());
			File file = new File("receive//"+receiveName2+"//"+currenttime+".txt");
			file.getParentFile().mkdirs();
			file.createNewFile();
			PrintStream printStream1=new PrintStream(file);
			printStream1.println(str2);
			printStream1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void data() {
		printStream.println("354 end with .");
	}
	private void quit() {
		printStream.println("221 byebye");
	}
	private void rcpt() {
		printStream.println("250 rcpt");
	}
	private void mail() {
		printStream.println("250 mail");
	}
	private void auth() {
		try {
			printStream.println("334 VXNlcm5hbWU6");
			String loginUsername=br.readLine();
			printStream.println("334 UGFzc3dvcmQ6");
			String loginPassword=br.readLine();
			byte[] b = Base64.decode(loginUsername);
			username=new String(b);
			byte[] b1=Base64.decode(loginPassword);
			String password=new String(b1);
			if(IsPass(password)){
				printStream.println("250 ok");
			}else{
				printStream.println("500 error");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void ehlo() {
		printStream.println("250 ehlo");
	}
	private void helo(){
		printStream.println("250 helo");
	}
	//判断域名是否是本机服务
	private Boolean doYM(String string){
		if(string.equals("zkh.com"))
			return true;
		return false;
	}
	//判断用户是否存在
	//判断密码是否正确
	boolean  IsPass(String password){
		if(password.equalsIgnoreCase(user.getProperty(username))){  //前提是用户名存在
			return true;
		}
		return false;
	}
}
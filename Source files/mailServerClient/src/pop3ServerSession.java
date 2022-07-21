import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class pop3ServerSession  implements Runnable{
	private Socket s=null;
	private BufferedReader br;
	private PrintStream printStream;
	private int FileList; //文件目录数量
	private int AllByte;   //所有目录文件的字节总数
	private int TotalByte;  //一个目录文件的字节数
	private static String username;  //收件人的名字
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
	public pop3ServerSession(Socket s){
		this.s = s;
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			printStream = new PrintStream(s.getOutputStream());
			//		    doFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		printStream.println("+OK wy's pop3 server is ready!");
		try {
			String command=null;
			while((command=br.readLine())!=null){
				int index=command.indexOf(" ");
				String argument=null;
				if(index>0){
					argument=command.substring(index+1);
					command=command.substring(0,index);
				}
				System.out.println(command+" "+argument);
				if(command.equalsIgnoreCase("USER")){
					doUser(argument);
				}
				else if(command.equalsIgnoreCase("PASS")){
					doPass(argument);
				}
				else if(command.equalsIgnoreCase("STAT")){
					doStat();
				}
				else if(command.equalsIgnoreCase("LIST")){
					doList();
					doFileByte();
					printStream.println(".");
				}
				else if(command.equalsIgnoreCase("RETR")){
					doRetr(argument);
				}else if(command.equalsIgnoreCase("DELE")){
					doDele(argument);
				}else if(command.equalsIgnoreCase("QUIT")){
					doQuit();
					break;
				}
				else if(command.equalsIgnoreCase("UIDL")){
					doUidl();

				}else if(command.equalsIgnoreCase("TOP")){
					doTop(argument);
				}else{
					printStream.println("-ERR Unknow");
				}
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void doTop(String string) {
		int index=string.indexOf(" ");
		//1 2    表示的是目录下的第几个文件的第几行
		try {
			String str=null;
			String string1=null;
			int count=0;
			String First=string.substring(0,index);
			int i=Integer.parseInt(First);
			System.out.println(First);
			String finl=string.substring(index+1);
			int i1=Integer.parseInt(finl);
			System.out.println(finl);
			File file=new File("receive//"+username);
			File[] file1=file.listFiles();
			BufferedReader bufferReader1=new BufferedReader(
					new InputStreamReader(new FileInputStream("receive//"+username+"//"+file1[i-1].getName())));
			while((str=bufferReader1.readLine())!=null){
				count++;
				string1=str;
				System.out.println(string1);
				if(count==i1)  break;
			}
			bufferReader1.close();
			printStream.println(str);
			printStream.println(".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void doUidl() {
		File file=new File("receive//"+username);
		File[] file1=file.listFiles();
		for(int i=0;i<file1.length;i++){
			printStream.println(i+1+" "+makeMD5(file1[i].getName()));
		}
		printStream.println(".");
	}
	private void doQuit() {
		printStream.println("+OK core mail");
	}
	private void doRetr(String str) {
		try {
			printStream.println("+OK "+doType(str));
			int i=Integer.parseInt(str);
			File file=new File("receive//"+username);
			File[] file1=file.listFiles();
			if (file1 != null) {
				BufferedReader bufferReader1=new BufferedReader(
						new InputStreamReader(new FileInputStream("receive//"+username+"//"+file1[i-1].getName())));
				String string1=null;
				while((string1=bufferReader1.readLine())!=null){
					printStream.println(string1);         //把文件内容写入
				}
				printStream.println(".");
				bufferReader1.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void doList() {
		printStream.println("+OK "+FileList+" "+AllByte);
	}
	private void doStat() {
		printStream.println("+OK "+FileList+" "+AllByte);
	}
	private void doPass(String str) {
		IsPass(str);
	}
	private void doUser(String str) {
		username=str;
		printStream.println("+OK USER");
	}
	private void doFile(){  //总目录下的字节数和单个目录下的字节数
		try {
			System.out.println(username);
			File file=new File("receive//"+username);
			File[] file1=file.listFiles();
			if (file1 != null) {
				FileList=file1.length;
			} else {
				return;
			}
			int allByte=0;
			for(int i=0;i<file1.length;i++){
				BufferedReader bufferReader1=new BufferedReader(
						new InputStreamReader(new FileInputStream("receive//"+username+"//"+file1[i].getName())));
				byte[] by=new byte[1024];
				String string1=null;
				int  sumOfBytes=0;
				while((string1=bufferReader1.readLine())!=null){
					by=string1.getBytes();
					sumOfBytes=sumOfBytes+by.length;
				}
				TotalByte=sumOfBytes;
				AllByte=AllByte+sumOfBytes;
				bufferReader1.close();
			}
			// System.out.println(FileList+" "+AllByte+" "+TotalByte);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	void doFileByte(){//单个目录下的字节数和目录数
		try {
			File file=new File("receive//"+username);
			File[] file1=file.listFiles();
			if (file1 != null) {
				FileList=file1.length;
			} else {
				return;
			}
			for(int i=0;i<file1.length;i++){
				BufferedReader bufferReader1=new BufferedReader(
						new InputStreamReader(new FileInputStream("receive//"+username+"//"+file1[i].getName())));
				byte[] by=new byte[1024];
				String string1=null;
				int  sumOfBytes=0;
				while((string1=bufferReader1.readLine())!=null){
					by=string1.getBytes();
					sumOfBytes=sumOfBytes+by.length;
				}
				TotalByte=sumOfBytes;
				printStream.print(i+1+" ");
				printStream.println(TotalByte);
				bufferReader1.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	int doType(String string){ //输入第几封信，就得到该文件的字节数
		int byteSum=0;
		try {
			int i=Integer.parseInt(string);
			File file=new File("receive//"+username);
			File[] file1=file.listFiles();
			AtomicReference<BufferedReader> bufferReader1= null;
			if (file1 != null) {
				bufferReader1 = new AtomicReference<>(new BufferedReader(
						new InputStreamReader(new FileInputStream("receive//" + username + "//" + file1[i - 1].getName()))));
				byte[] by=new byte[1024];
				int  sumOfBytes=0;
				String string1=null;
				while((string1= bufferReader1.get().readLine())!=null){
					by=string1.getBytes();
					sumOfBytes=sumOfBytes+by.length;
				}
				byteSum=sumOfBytes;
				bufferReader1.get().close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteSum;
	}
	void doDele(String string){
//	  int i=Integer.parseInt(string);
//	  File file=new File("receive//"+username);
//	  File[] file1=file.listFiles();
//	  File  file2=new File("receive//"+username+"//"+file1[i].getName());
//	  System.out.println(file2);
//	  file2.delete();
		printStream.println("+OK wy");
	}
	//判断密码是否正确
	void  IsPass(String password){
		if(password.equalsIgnoreCase(user.getProperty(username))){  //前提是用户名存在
			printStream.println("+OK PASS");
			doFile();
		}
		else{
			printStream.println("-ERR Unknown");
		}
	}
	public static String makeMD5(String password) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(password.getBytes());
			String pwd = new BigInteger(1, md.digest()).toString(16);
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}
}
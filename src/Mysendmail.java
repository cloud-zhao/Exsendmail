import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*; 

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.moyosoft.exchange.*;
import com.moyosoft.exchange.mail.*;
import com.moyosoft.exchange.item.BodyType;

public class Mysendmail {
	public static void main(String[] args) throws ExchangeServiceException
	{
		Options option=new Options();
		option.addOption("h","host",true,"Exchange hostname or url");
		option.addOption("u","user",true,"Login uers");
		option.addOption("p","password",true,"Login password");
		option.addOption("t","address",true,"Mail addressee can be a comma separated list");
		option.addOption("cc",true,"Mail CC addressee list");
		option.addOption("j","subject",true,"Mail subject");
		option.addOption("c",true,"Mail body can be a text or html string");
		option.addOption("f",true,"Mail body send a file");
		option.addOption("H",false,"Print help info");

		String helpstr="sendMail -h host -u user -p passwd -t xx@xx.com [-cc][-j][-c|-f]";

		HelpFormatter helpformat=new HelpFormatter();
		CommandLineParser parser=new PosixParser();
		CommandLine cl=null;

		if(args.length == 0){
			System.err.println("[ERROR] Please enter parameter");
			helpformat.printHelp(helpstr,option);
			return;
		}

		try{
			cl=parser.parse(option,args,false);
		}catch(ParseException e){
			helpformat.printHelp(helpstr,option);
		}

		if(cl.hasOption("H")){
			helpformat.printHelp(helpstr,option);
			return;
		}
		if((! cl.hasOption("h"))||(! cl.hasOption("u"))||(! cl.hasOption("p"))||(! cl.hasOption("t"))){
			System.err.println("[ERROR] Please enter necessary parameter");
			//helpformat.printHelp(helpstr,option);
			return;
		}

		String host=cl.getOptionValue("h");
		String user=cl.getOptionValue("u");
		String password=cl.getOptionValue("p");
		String addres=cl.getOptionValue("t");
		String copyadd=cl.getOptionValue("cc");
		String subject=cl.getOptionValue("j");
		String body=null;
		if(cl.hasOption("c")&&(!cl.hasOption("f"))){
			body=cl.getOptionValue("c","Hello");
		}else if(cl.hasOption("f")){
			body=cl.getOptionValue("f");
			body=fileread(body);
		}else{
			System.err.println("[WARNNING] not set mail body");
		}

		//System.out.println(host+"\t"+user+"\t"+password+"\t"+addres+"\t"+body);
		
		
		List<String> mailaddress=new ArrayList<String>();
		for(String mailadd : addres.split(",")){
			mailaddress.add(mailadd);
		}
		List<String> copyaddress=new ArrayList<String>();
		if(cl.hasOption("cc")){
			for(String copy : copyadd.split(",")){
				copyaddress.add(copy);
			}
		}
		
		String[] domains=host.split("\\.");
		String domain=domains[1];
		
		//mail
		Exchange exchange=new Exchange(host,user,password,domain,true);
		if(exchange == null){
			System.out.print("FAILED!!!\n");
			return;
		}
		
		BodyType type=BodyType.valueOf("Html");
		ExchangeMail mail=exchange.createMail();
		mail.setToRecipients(mailaddress);
		mail.setCcRecipients(copyaddress);
		mail.setSubject(subject);
		mail.setBody(body,type);
		    
		// Send the message:
		mail.send();
		System.out.print("Sendmail success\n");
	}
	
	//read file content to string
	static String fileread(String fp){
		File file=new File(fp);
		BufferedReader reader=null;
		String rString=null;
		try{
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine()) != null){
				rString+=tempString;
			}
			reader.close();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e1){
				}
			}
		}
		return rString;
	}
}

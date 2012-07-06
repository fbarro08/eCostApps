package mall.platinum;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mall.bean.PlatinumMember;
import mall.dao.PlatinumDao;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mall.util.EmailUtility;
import com.mall.util.ResourceUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SendActivateNotification {
	private static Logger logger = Logger.getLogger(SendActivateNotification.class);
	
	public final static String EMAIL_TEMPLATE = "platinum.activation.email";
	public final static String EMAIL_TITLE = "platinum.activation.email.title";
	public final static String EMAIL_SENDING_ENABLED = "platinum.activation.email.enabled";
	public final static String EMAIL_SENDING_ENABLED_TRUE = "1";
	
	private Properties prop = new Properties();	
	private ApplicationContext context = new ClassPathXmlApplicationContext("/context.xml", SendActivateNotification.class);
	private PlatinumDao platinumDao;
	
	private final ResourceUtil resourceUtil = ResourceUtil.getInstance();
	private final EmailUtility emailUtility = EmailUtility.getInstance();
	private String sendingEnabled = "";
	
	public SendActivateNotification() {	
		
		platinumDao = (PlatinumDao)context.getBean("platinumDao");
		try {
			prop.load(SendActivateNotification.class.getClassLoader().getResourceAsStream("platinum.conf"));
			
			setSendingEnabled(resourceUtil.getKeyValue(EMAIL_SENDING_ENABLED, prop));
		} catch (IOException e) {			
			logger.error(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("-- Start SendActivateNotification --");
		
		SendActivateNotification sendActivateNotification = new SendActivateNotification();
		
		//check if email sending for platinum activation is enabled
		if(EMAIL_SENDING_ENABLED_TRUE.equals(sendActivateNotification.getSendingEnabled())){
			sendActivateNotification.processConfirmationEmail();
		}
		
		logger.info("-- End SendActivateNotification --");
		
		System.exit(0);
	}

	private void processConfirmationEmail(){
		logger.info("Start sending email in processConfirmationEmail.");
		
		try {			
			List<PlatinumMember> platinumMembersList = platinumDao.getNewlyActivatedPlatinumMembers();
			
			if(platinumMembersList!=null) {
				for (PlatinumMember platinumMember : platinumMembersList) {
					
					if(platinumMember!=null){
						if(sendConfirmationEmail(platinumMember)){
							platinumDao.insertActivatedPlatinumMembers(platinumMember);
						}
					}
					
				}	
			}
		} catch(TemplateException e){
			logger.error(e);
		} catch(Exception e){
			logger.error(e);
		}
		
		logger.info("End sending email in processConfirmationEmail.");
	}
	
	private boolean sendConfirmationEmail(PlatinumMember platinumMember) throws TemplateException, Exception {		
		
		// Add the values in the datamodel
		Map<String, Object> datamodel = new HashMap<String, Object>();
		datamodel.put("firstname", platinumMember.getFirstName());
		datamodel.put("lastname", platinumMember.getLastName());
		
		String emailTitle = resourceUtil.getKeyValue(EMAIL_TITLE, prop);
		String emailBody = freeMarkerProcess(datamodel, resourceUtil.getKeyValue(EMAIL_TEMPLATE, prop));		
		
		return emailUtility.sendHTMLMail(platinumMember.getEmail(), emailTitle, emailBody);
	}
		
	private String freeMarkerProcess(Map datamodel, String template) throws TemplateException, Exception	{
		Configuration cfg = new Configuration();
		Template tpl = cfg.getTemplate(template);
		StringWriter sw = new StringWriter();
		
		tpl.process(datamodel, sw);
		
		return sw.toString();
	}
	

	public String getSendingEnabled() {
		return sendingEnabled;
	}

	public void setSendingEnabled(String sendingEnabled) {
		this.sendingEnabled = sendingEnabled;
	}

}


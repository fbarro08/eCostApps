package mall.dao;

import java.util.List;
import java.util.Map;

import mall.bean.PlatinumMember;

public interface PlatinumDao {	
	public static final String SP_NEWPLATINUMMEMBERS_DATA = "{call dbo.usp_GET_Membership_New_Email() }";
	public static final String SP_INSERTMEMBER_EMAIL_SENT = "{call dbo.usp_Insert_Membership_Email_Sent(?, ?, ?) }";
	
	public List<PlatinumMember> getNewlyActivatedPlatinumMembers(); 
	public void insertActivatedPlatinumMembers(PlatinumMember member); 
	
}

package mall.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mall.bean.PlatinumMember;
import mall.dao.PlatinumDao;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;


public class PlatinumDaoImpl extends SimpleJdbcDaoSupport implements PlatinumDao { 

	// get gets email list of activated platinum members
	@Override
	public List<PlatinumMember> getNewlyActivatedPlatinumMembers() {
		List<Map<String, Object>> resultset = getJdbcTemplate().queryForList(SP_NEWPLATINUMMEMBERS_DATA);
		
		List<PlatinumMember> memberList = null;
		if(resultset!=null && !resultset.isEmpty()){
			memberList = new ArrayList<PlatinumMember>();

			for (Map<String, Object> memberRow: resultset) {
				PlatinumMember member = new PlatinumMember();
				
				member.setFirstName(memberRow.get("FirstName").toString());
				member.setLastName(memberRow.get("LastName").toString());
				member.setEmail(memberRow.get("Email").toString());	
				member.setOrderNo(memberRow.get("Order_No").toString());
				member.setShopperId(memberRow.get("Shopper_ID").toString());
				member.setDateCreated(memberRow.get("DateCreated").toString());
				
				memberList.add(member);
			}
		}
		
		return memberList;
	}

	//saves email of activated member (called after email notification)	
	@Override
	public void insertActivatedPlatinumMembers(PlatinumMember member) {
		int affRow = getJdbcTemplate().update(SP_INSERTMEMBER_EMAIL_SENT,
					new Object[] {
					member.getEmail(),
					member.getShopperId(),
					member.getOrderNo()
					});
		
		if(affRow==1) {
				logger.info("Membership_Email_Sent inserted/updated successfully for ."+member.getEmail());
		}
		
	}
}

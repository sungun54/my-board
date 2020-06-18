package com.sbs.example.demo.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.demo.db.DBConnection;
import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;

public class MemberDao {	
	DBConnection dbConn;
	public MemberDao() {
		dbConn = Factory.getDBConnection();
	}	
	
	public List<Member> getMembers() {
		List<Map<String, Object>> rows = dbConn.selectRows("SELECT * FROM `Member` ORDER by id DESC");
		List<Member> members = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			members.add(new Member(row));
		}

		return members;
	}

	public Member getMemberByLoginId(String loginId) {
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM `Member` WHERE loginId = '" + loginId + "'");
		if(row.isEmpty()) {
			Member member = null;
			return member;
		}
		Member member = new Member(row);
		return member;		
	}

	public String getMember(int id) {		
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM `Member` WHERE id = " + id);
		if(row.isEmpty()) {
			return null;
		}
		Member member = new Member(row);
		return member.getName();
	}

	public int save(Member member) {
		String sql = "";
		sql += "INSERT INTO Member ";
		sql += String.format("SET regDate = '%s'", member.getRegDate());
		sql += String.format(", loginPw = '%s'", member.getLoginId());
		sql += String.format(", loginId = '%s'", member.getLoginPw());
		sql += String.format(", `name` = '%s'", member.getName());	
		return dbConn.insert(sql);
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM `Member` WHERE loginId = '" + loginId + "' AND loginPw = '" + loginPw + "'");
		if(row.isEmpty()) {
			Member member = null;
			return member;
		}
		Member member = new Member(row);
		return member;		
	}
}
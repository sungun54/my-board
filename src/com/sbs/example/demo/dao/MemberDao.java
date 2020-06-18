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
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format("ORDER by id DESC"));
		//String sql = "SELECT * FROM `Member` ORDER by id DESC"
		List<Map<String, Object>> rows = dbConn.selectRows(sb.toString());
		List<Member> members = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			members.add(new Member(row));
		}

		return members;
	}

	public Member getMemberByLoginId(String loginId) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format(" WHERE loginId = '%s'", loginId));		
		//String sql ="SELECT * FROM `Member` WHERE loginId = '" + loginId + "'";
		Map<String, Object> row = dbConn.selectRow(sb.toString());
		if(row.isEmpty()) {
			Member member = null;
			return member;
		}
		Member member = new Member(row);
		return member;		
	}

	public String getMemberName(int id) {	
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `member` "));
		sb.append(String.format(" WHERE id = %d", id));	
		//String sql = "SELECT * FROM `Member` WHERE id = " + id;
		Map<String, Object> row = dbConn.selectRow(sb.toString());
		if(row.isEmpty()) {
			return null;
		}
		Member member = new Member(row);
		return member.getName();
	}

	public int save(Member member) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("INSERT INTO `Member` "));
		sb.append(String.format("SET regDate = '%s'", member.getRegDate()));
		sb.append(String.format(", loginPw = '%s'", member.getLoginId()));
		sb.append(String.format(", loginId = '%s'", member.getLoginPw()));	
		sb.append(String.format(", `name` = '%s'", member.getName()));	
//		String sql = "";
//		sql += "INSERT INTO Member ";
//		sql += String.format("SET regDate = '%s'", member.getRegDate());
//		sql += String.format(", loginPw = '%s'", member.getLoginId());
//		sql += String.format(", loginId = '%s'", member.getLoginPw());
//		sql += String.format(", `name` = '%s'", member.getName());	
		return dbConn.insert(sb.toString());
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * FROM `Member` "));
		sb.append(String.format("WHERE loginId = '%s' ", loginId));
		sb.append(String.format("AND loginPw = '%s'", loginPw));
		//String sql = "SELECT * FROM `Member` WHERE loginId = '" + loginId + "' AND loginPw = '" + loginPw + "'";
		Map<String, Object> row = dbConn.selectRow(sb.toString());
		if(row.isEmpty()) {
			Member member = null;
			return member;
		}
		Member member = new Member(row);
		return member;		
	}
}
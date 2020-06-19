package com.sbs.example.demo.dto;

import java.util.Map;

public class ArticleReply extends Dto{
	private String body;
	private int memberId;
	private int articleId;	
		
	public ArticleReply(int memberId, int articleId, String body) {
		this.body = body;
		this.memberId = memberId;
		this.articleId = articleId;
	}

	public ArticleReply(Map<String, Object> row) {
		super((int)row.get("id"), (String)row.get("regDate"));
		this.setBody((String) row.get("body"));
		this.setMemberId((int)row.get("memberId"));
		this.setArticleId((int)row.get("articleId"));
	}

	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	
	
}


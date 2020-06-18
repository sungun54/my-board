package com.sbs.example.demo.dto;
import java.util.Map;

public class Article extends Dto {
	private int boardId;
	private int memberId;
	private int hit;
	private String title;
	private String body;

	public Article() {
	}

	public Article(int boardId, int memberId, String title, String body) {
		this.boardId = boardId;
		this.memberId = memberId;
		this.title = title;
		this.body = body;
		this.hit = 0;
	}

	public Article(Map<String, Object> row) {
		this.setId((int) (long) row.get("id"));
		String regDate = row.get("regDate") + "";
		this.setRegDate(regDate);
		this.setHit((int)(long) row.get("hit"));
		this.setTitle((String) row.get("title"));
		this.setBody((String) row.get("body"));
		this.setMemberId((int) (long) row.get("memberId"));
		this.setBoardId((int) (long) row.get("boardId"));
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}
}
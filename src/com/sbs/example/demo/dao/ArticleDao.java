package com.sbs.example.demo.dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.demo.db.DBConnection;
import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;

// Dao
public class ArticleDao {
	DBConnection dbConn;
	public ArticleDao() {
		dbConn = Factory.getDBConnection();
	}	
	
	
	public void modifyHit(Article article) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("UPDATE Article "));
		sb.append(String.format("SET hit = %d", article.getHit()));
		sb.append(String.format(" WHERE id = %d", article.getId()));
		//String sql = "UPDATE Article SET hit = " + article.getHit() + " WHERE id = " + article.getId();
		dbConn.update(sb.toString());
	}


	public int modify(Article article) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("UPDATE Article "));
		sb.append(String.format("SET title = '%s'", article.getTitle()));
		sb.append(String.format(", `body` = '%s'", article.getBody()));
		sb.append(String.format(" WHERE id = %d", article.getId()));
		//String sql = "UPDATE Article SET title = '" + article.getTitle() + "', `body` = '" + article.getBody() + "' WHERE id = " + article.getId();
		return dbConn.update(sb.toString());	
	}


	public List<Article> getArticlesByBoardCode(String code) {		
		StringBuilder boardSb = new StringBuilder();
		boardSb.append(String.format("SELECT id "));
		boardSb.append(String.format("FROM Board "));
		boardSb.append(String.format("WHERE `code` = '%s'", code));		
		//String boardSql = "SELECT id FROM Board WHERE `code` = '" + code + "'";
		StringBuilder articleSb = new StringBuilder();
		boardSb.append(String.format("SELECT id "));
		boardSb.append(String.format("FROM Article "));
		boardSb.append(String.format("ORDER by id DESC"));	
		//String articleSql = "SELECT id FROM Board WHERE `code` = '" + code + "'";
		List<Map<String, Object>> boardRows = dbConn.selectRows(boardSb.toString());
		List<Map<String, Object>> articleRows = dbConn.selectRows(articleSb.toString());
		List<Article> articles = new ArrayList<>();
//		for(int i = 0; i < boardRows.size(); i++) {
//			for (Map<String, Object> row : articleRows) {
//				if(boardRows.get(i).get("id") == row.get("boardId"))
//				articles.add(new Article(row));
//			}
//		}
		for(Map<String, Object> boardRow : boardRows) {
			for (Map<String, Object> articleRow : articleRows) {
				if(boardRow.get("id") == articleRow.get("boardId"))
				articles.add(new Article(articleRow));
			}
		}
		return articles;
		
	}

	public int deleteBoard(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("DELETE FROM Board "));
		sb.append(String.format("WHERE id = '%d'", id));
		//String sql = "DELETE FROM Board WHERE id = " + id;
		return dbConn.deleteRow(sb.toString());
	}

	public List<Board> articleListBoard() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM Board "));
		sb.append(String.format("ORDER by id DESC"));	
		//String sql = "SELECT * FROM Board ORDER by id DESC";
		List<Map<String, Object>> rows = dbConn.selectRows(sb.toString());
		List<Board> Boards = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			Boards.add(new Board(row));
		}
		
		return Boards;
	}

	public int articleDelete(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("DELETE FROM Article "));
		sb.append(String.format("WHERE id = '%d'", id));
		//String sql = "DELETE FROM Article WHERE id = " + id;
		return dbConn.deleteRow(sb.toString());
	}

	public Article getArticle(int id) {		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM Article "));
		sb.append(String.format("WHERE id = '%d'", id));
		//String sql = "SELECT * FROM Article WHERE id = " + id";
		Map<String, Object> row = dbConn.selectRow(sb.toString());
//		if(row.get("title") == null) {
//			Article article = null;
//			return article;
//		}
		if(row.isEmpty()) {
			Article article = null;
			return article;
		}
		
		Article article = new Article(row);
		return article;
	}

	public Board getBoardByCode(String code) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM Board "));
		sb.append(String.format("WHERE `code` = '%s'", code));
		//String sql = "SELECT * FROM Board WHERE `code` = '" + code + "'";
		Map<String, Object> row = dbConn.selectRow(sb.toString());
//		if(row.get("name") == null) {
//			Board board = null;
//			return board;
//		}
		if(row.isEmpty()) {
			Board board = null;
			return board;
		}
		Board board = new Board(row);
		return board;
		//return db.getBoardByCode(code);
	}

	public int saveBoard(Board board) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("INSERT INTO Board "));
		sb.append(String.format("SET `name` = '%s'", board.getName()));
		sb.append(String.format(", regDate = '%s'", board.getRegDate()));
		sb.append(String.format(", `code` = '%s'", board.getCode()));
//		String sql = "";		
//		sql += "INSERT INTO Board ";
//		sql += String.format("SET `name` = '%s'", board.getName());
//		sql += String.format(", regDate = '%s'", board.getRegDate());
//		sql += String.format(", `code` = '%s'", board.getCode());
		return dbConn.insert(sb.toString());
	}

	public int save(Article article) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("INSERT INTO Article "));
		sb.append(String.format("SET regDate = '%s'", article.getRegDate()));
		sb.append(String.format(", title = '%s'", article.getTitle()));
		sb.append(String.format(", hit = '%s'", article.getHit()));
		sb.append(String.format(", `body` = '%s'", article.getBody()));		
		sb.append(String.format(", memberId = '%d'", article.getMemberId()));			
		sb.append(String.format(", boardId = '%d'", article.getBoardId()));		
//		String sql = "";
//		sql += "INSERT INTO Article ";
//		sql += String.format("SET regDate = '%s'", article.getRegDate());
//		sql += String.format(", title = '%s'", article.getTitle());
//		sql += String.format(", hit = '%s'", article.getHit());
//		sql += String.format(", `body` = '%s'", article.getBody());
//		sql += String.format(", memberId = '%d'", article.getMemberId());
//		sql += String.format(", boardId = '%d'", article.getBoardId());		
		return dbConn.insert(sb.toString());
	}

	public Board getBoard(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM Board "));
		sb.append(String.format("WHERE id = %d", id));
		//String sql = "SELECT * FROM Board WHERE id = " + id;
		Map<String, Object> row = dbConn.selectRow(sb.toString());
		if(row.isEmpty()) {
			return null;
		}
		Board board = new Board(row);
		return board;
	}

	public List<Article> getArticles() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM Article "));
		sb.append(String.format("ORDER by id DESC"));
		//String sql = "SELECT * FROM Article ORDER by id DESC";
		List<Map<String, Object>> rows = dbConn.selectRows(sb.toString());
		List<Article> articles = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			articles.add(new Article(row));
		}

		return articles;
	}

}
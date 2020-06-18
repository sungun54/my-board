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
		String sql = "UPDATE Article SET hit = " + article.getHit() + " WHERE id = " + article.getId();
		dbConn.update(sql);
	}


	public int modify(Article article) {
		String sql = "UPDATE Article SET title = '" + article.getTitle() + "', `body` = '" + article.getBody() + "' WHERE id = " + article.getId();
		return dbConn.update(sql);	
	}


	public List<Article> getArticlesByBoardCode(String code) {		
		
		List<Map<String, Object>> boardRows = dbConn.selectRows("SELECT id FROM Board WHERE `code` = '" + code + "'");
		List<Map<String, Object>> articleRows = dbConn.selectRows("SELECT * FROM Article ORDER by id DESC");
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
		String sql = "DELETE FROM Board WHERE id = " + id;
		return dbConn.deleteRow(sql);
	}

	public List<Board> articleListBoard() {
		
		List<Map<String, Object>> rows = dbConn.selectRows("SELECT * FROM Board ORDER by id DESC");
		List<Board> Boards = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			Boards.add(new Board(row));
		}
		
		return Boards;
	}

	public int articleDelete(int id) {
		String sql = "DELETE FROM Article WHERE id = " + id;
		return dbConn.deleteRow(sql);
	}

	public Article getArticle(int id) {		
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM Article WHERE id = " + id);
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
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM Board WHERE `code` = '" + code + "'");
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
		String sql = "";		
		sql += "INSERT INTO Board ";
		sql += String.format("SET `name` = '%s'", board.getName());
		sql += String.format(", regDate = '%s'", board.getRegDate());
		sql += String.format(", `code` = '%s'", board.getCode());
		return dbConn.insert(sql);
	}

	public int save(Article article) {
		String sql = "";
		sql += "INSERT INTO Article ";
		sql += String.format("SET regDate = '%s'", article.getRegDate());
		sql += String.format(", title = '%s'", article.getTitle());
		sql += String.format(", hit = '%s'", article.getHit());
		sql += String.format(", `body` = '%s'", article.getBody());
		sql += String.format(", memberId = '%d'", article.getMemberId());
		sql += String.format(", boardId = '%d'", article.getBoardId());		
		return dbConn.insert(sql);
	}

	public Board getBoard(int id) {
		Map<String, Object> row = dbConn.selectRow("SELECT * FROM Board WHERE id = " + id);
		if(row.isEmpty()) {
			return null;
		}
		Board board = new Board(row);
		return board;
	}

	public List<Article> getArticles() {
		
		List<Map<String, Object>> rows = dbConn.selectRows("SELECT * FROM Article ORDER by id DESC");
		List<Article> articles = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			articles.add(new Article(row));
		}

		return articles;
	}

}
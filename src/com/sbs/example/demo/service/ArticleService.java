package com.sbs.example.demo.service;
import java.util.List;

import com.sbs.example.demo.dao.ArticleDao;
import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;

public class ArticleService {
	private ArticleDao articleDao;
	private MemberService memberService;

	public ArticleService() {
		articleDao = Factory.getArticleDao();
		memberService = Factory.getMemberService();
	}

	public void modifyHit(Article article) {
		articleDao.modifyHit(article);		
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return articleDao.getArticlesByBoardCode(code);
	}

	public int deleteBoard(int id) {
		return articleDao.deleteBoard(id);
	}

	public List<Board> articleListBoard() {
		return articleDao.articleListBoard();
	}

	public int articledelete(int id) {
		return articleDao.articleDelete(id);
	}

	public String getMemberName(int id) {
		return memberService.getMemberName(id);
	}

	public int modify(Article article) {
		return articleDao.modify(article);
	}

	public Article getArticle(int id) {
		return articleDao.getArticle(id);
	}

	public int makeBoard(String name, String code) {
		Board oldBoard = articleDao.getBoardByCode(code);

		if (oldBoard != null) {
			return -1;
		}

		Board board = new Board(name, code);
		return articleDao.saveBoard(board);
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public int write(int boardId, int memberId, String title, String body) {
		Article article = new Article(boardId, memberId, title, body);
		return articleDao.save(article);
	}

	public List<Article> getArticles() {
		return articleDao.getArticles();
	}

}
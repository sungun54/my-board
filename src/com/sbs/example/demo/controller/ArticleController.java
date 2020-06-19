package com.sbs.example.demo.controller;
import java.util.ArrayList;
import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.ArticleReply;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.ArticleService;

public class ArticleController extends Controller {
	private ArticleService articleService;

	public ArticleController() {
		articleService = Factory.getArticleService();
	}

	public void setArticleService(ArticleService articleService) {
		this.articleService = articleService;
	}

	public void doAction(Request request) {
		if (request.getActionName().equals("list")) {
			actionList(request);
		} else if (request.getActionName().equals("write")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionWrite(request);
		} else if (request.getActionName().equals("modify")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionModify(request);
		} else if (request.getActionName().equals("detail")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionDetail(request);
		} else if (request.getActionName().equals("delete")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionDelete(request);
		} else if (request.getActionName().equals("createBoard")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionCreateBoard(request);
		} else if (request.getActionName().equals("listBoard")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionListBoard(request);
		} else if (request.getActionName().equals("changeBoard")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionChangeBoard(request);
		} else if (request.getActionName().equals("deleteBoard")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionDeleteBoard(request);
		} else if (request.getActionName().equals("listArticle")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionListArticle(request);
		} else if (request.getActionName().equals("currentBoard")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionCurrentBoard(request);
		} else if (request.getActionName().equals("replyWrite")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionReplyWrite(request);
		} else if (request.getActionName().equals("replyModify")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionReplyModify(request);
		} else if (request.getActionName().equals("replyDelete")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionReplyDelete(request);
		}
	}

	private void actionReplyDelete(Request request) {
		if(request.getArg1() == null) {
			System.out.println("댓글을 삭제하실 댓글 번호를 입력해주세요.");
			return;
		}
		ArticleReply articleReply = Factory.getArticleService().getArticleReply(Integer.parseInt(request.getArg1()));
		
		if(articleReply == null) {
			System.out.println("해당 댓글은 존재하지 않습니다.");
		}		
		
		if (articleReply.getMemberId() != Factory.getSession().getLoginedMember().getId()) {
			System.out.println("권한이 없습니다.");
			return;
		}

		articleService.deleteArticleReply(articleReply.getId());
		System.out.println(articleReply.getId() + "번 댓글이 삭제 되었습니다.");		
	}

	private void actionReplyModify(Request request) {
		if(request.getArg1() == null) {
			System.out.println("댓글을 수정하실 댓글 번호를 입력해주세요.");
			return;
		}
		ArticleReply articleReply = Factory.getArticleService().getArticleReply(Integer.parseInt(request.getArg1()));
		if(articleReply == null) {
			System.out.println("해당 댓글은 존재하지 않습니다.");
		}		
		if (articleReply.getMemberId() != Factory.getSession().getLoginedMember().getId()) {
			System.out.println("권한이 없습니다.");
			return;
		}
		System.out.print("댓글내용 : ");
		String body = Factory.getScanner().nextLine();

		articleReply.setBody(body);
		articleService.articleReplymodify(articleReply);
		System.out.println(articleReply.getId() + "번 댓글이 수정되었습니다.");
		
	}

	private void actionReplyWrite(Request request) {
		if(request.getArg1() == null) {
			System.out.println("댓글을 작성하실 게시물 번호를 입력해주세요.");
			return;
		}
		Article article = Factory.getArticleService().getArticle(Integer.parseInt(request.getArg1()));
		if(article == null) {
			System.out.println("해당 게시물은 존재하지 않습니다.");
		}
		String body;
		while (true) {
			System.out.printf("댓글 작성 : ");
			body = Factory.getScanner().nextLine();

			if (body.length() < 2) {
				System.out.println("두 글자 이상 입력해주세요.");
				continue;
			}
			break;
		}

		// 현재 게시판 id 가져오기
		int boardId = Factory.getSession().getCurrentBoard().getId();

		// 현재 로그인한 회원의 id 가져오기
		int memberId = Factory.getSession().getLoginedMember().getId();
		articleService.replyWrite(memberId, article.getId(), body);
		System.out.printf("%d번 글의 댓글이 작성되었습니다.\n", article.getId());
	}

	private void actionListArticle(Request request) {
		if (request.getArg1() == null) {
			System.out.println("페이지를 입력해주세요.");
			return;
		}
		System.out.println("==========================================================");
		int paging = 5;
		int page = Integer.parseInt(request.getArg1());
		int currentBoardId = Factory.getSession().getCurrentBoard().getId();
		List<Article> articleList = articleService.getArticles();
		List<Article> divideArticle = new ArrayList<>();

		if (request.getArg2() == null) {
			for (Article article : articleList) {
				if (article.getBoardId() == currentBoardId) {
					divideArticle.add(article);
				}
			}

			if (divideArticle.size() == 0) {
				System.out.println("게시물이 존재하지 않습니다.");
				return;
			}

			if (divideArticle.size() < paging * (page - 1)) {
				System.out.println("게시물이 존재하지 않습니다.");
				return;
			}

			for (int i = (page - 1) * paging; i < (page) * paging; i++) {
				if (i == divideArticle.size()) {
					break;
				}
					System.out.printf("No : %d, RegDate : %s, Title : %s, Writer : %s, Hit : %s%n", divideArticle.get(i).getId(), divideArticle.get(i).getRegDate(), divideArticle.get(i).getTitle(), articleService.getMemberName(divideArticle.get(i).getMemberId()), divideArticle.get(i).getHit());				
			}
		} else {
			List<Article> searchArticle = new ArrayList<>();
			for (Article article : articleList) {
				if (article.getBoardId() == currentBoardId) {
					if (article.getTitle().contains(request.getArg2())) {
						searchArticle.add(article);
					}
				}
			}

			if (searchArticle.size() == 0) {
				System.out.println("게시물이 존재하지 않습니다.");
				return;
			}

			if (searchArticle.size() < paging * (page - 1)) {
				System.out.println("게시물이 존재하지 않습니다.");
				return;
			}

			for (int i = (page - 1) * paging; i < (page) * paging; i++) {
				if (i == searchArticle.size()) {
					break;
				}
				System.out.println(searchArticle.get(i).getTitle());
			}
		}
		System.out.println("==========================================================");
	}

	private void actionDeleteBoard(Request request) {
		if (Factory.getSession().getLoginedMember().getId() != 1) {
			System.out.println("권한이 없습니다.");
			return;
		}

		if (request.getArg1() == null) {
			System.out.println("삭제할 게시판 코드를 입력해주세요.");
			return;
		}
		Board removeBoard = null;
		List<Board> boards = articleService.articleListBoard();
		for (Board board : boards) {
			if (board.getCode().equals(request.getArg1())) {
				removeBoard = board;
				break;
			}
		}

		if (removeBoard == null) {
			System.out.println("해당 게시판은 존재하지 않습니다.");
			return;
		}

		if (removeBoard.getId() == 1) {
			System.out.println("해당 게시판은 삭제 할 수 없습니다.");
			return;
		}
		int removeBoardName = articleService.deleteBoard(removeBoard.getId());
		Board board = articleService.getBoard(removeBoardName);
		System.out.println(board.getName() + "이 삭제 되었습니다.");
	}

	private void actionChangeBoard(Request request) {
		if (request.getArg1() == null) {
			System.out.println("게시판 코드를 입력해주세요.");
			return;
		}

		List<Board> boards = articleService.articleListBoard();
		Board changeBoard = null;

		for (Board board : boards) {
			if (board.getCode().equals(request.getArg1())) {
				changeBoard = board;
				break;
			}
		}

		if (changeBoard == null) {
			System.out.println("해당 게시판이 존재하지 않습니다.");
			return;
		}

		if (changeBoard.getCode().equals(Factory.getSession().getCurrentBoard().getCode())) {
			System.out.printf("이미 %s이 선택되어있습니다.", changeBoard.getName());
			return;
		}

		Factory.getSession().setCurrentBoard(changeBoard);
		System.out.println(changeBoard.getName() + "으로 이동합니다.");
	}

	private void actionListBoard(Request request) {
		List<Board> boards = articleService.articleListBoard();
		System.out.println("==========================================================");
		for (Board board : boards) {
			System.out.printf("No : %d, Name : %s, Code : %s%n", board.getId(), board.getName(), board.getCode());
		}
		System.out.println("==========================================================");
	}

	private void actionCurrentBoard(Request request) {
		System.out.println(Factory.getSession().getCurrentBoard().getName());
	}

	private void actionCreateBoard(Request request) {
		if (Factory.getSession().getLoginedMember().getId() != 1) {
			System.out.println("권한이 없습니다.");
			return;
		}

		System.out.print("게시판 이름 : ");
		String boardName = Factory.getScanner().nextLine();
		System.out.print("게시판 코드 : ");
		String boardCode = Factory.getScanner().nextLine();

		Factory.getArticleService().makeBoard(boardName, boardCode);
		System.out.println(boardName + "이 생성되었습니다.");
	}

	private void actionDelete(Request request) {
		if (request.getArg1() == null) {
			System.out.println("삭제할 게시물의 번호를 입력해주세요.");
			return;
		}
		Article article = articleService.getArticle(Integer.parseInt(request.getArg1()));

		if (article == null) {
			System.out.println("해당 게시물은 존재하지 않습니다.");
			return;
		}

		if (article.getMemberId() != Factory.getSession().getLoginedMember().getId()) {
			System.out.println("권한이 없습니다.");
			return;
		}

		int removedArticleId = articleService.articledelete(Integer.parseInt(request.getArg1()));
		System.out.println(request.getArg1() + "번 게시물이 삭제 되었습니다.");
	}

	private void actionDetail(Request request) {
		if (request.getArg1() == null) {
			System.out.println("열람할 게시물의 번호를 입력해주세요.");
			return;
		}

		Article article = articleService.getArticle(Integer.parseInt(request.getArg1()));

		if (article == null) {
			System.out.println("해당 게시물은 존재하지 않습니다.");
			return;
		}
		List<ArticleReply> articleReplys = articleService.getArticleReplyList(article.getId());	
		if(request.getArg2() == null ){		
			System.out.println("==========================================================");
			System.out.printf("번호 : %d%n", article.getId());
			System.out.printf("날짜 : %s%n", article.getRegDate());
			System.out.printf("제목 : %s%n", article.getTitle());
			System.out.printf("내용 : %s%n", article.getBody());
			System.out.printf("작성자 : %s%n", articleService.getMemberName(article.getMemberId()));
			System.out.printf("조회수 : %d%n", article.getHit());
			System.out.println("==========================================================");
			System.out.println("전체 댓글 수 : " + articleReplys.size());
			System.out.println("=== 댓글 리스트 ===");
			if(articleReplys.size() == 0) {
				System.out.println("작성된 댓글이 없습니다.");
			}
			for(ArticleReply articleReply : articleReplys) {
				System.out.printf("Name : %s, body : %s, regDate%n", articleService.getMemberName(articleReply.getMemberId()), articleReply.getBody(), articleReply.getRegDate());
			}
			article.setHit(article.getHit()+1);
			articleService.modifyHit(article);
		} 
		else if(request.getArg2().equals("reply")) {
			if(articleReplys.size() == 0) {
				System.out.println("작성된 댓글이 없습니다.");
			}
			for(ArticleReply articleReply : articleReplys) {
				System.out.printf("Name : %s, body : %s, regDate%n", articleService.getMemberName(articleReply.getMemberId()), articleReply.getBody(), articleReply.getRegDate());
			}
		}
	}

	private void actionModify(Request request) {
		if (request.getArg1() == null) {
			System.out.println("수정할 게시물의 번호를 입력해주세요.");
			return;
		}
		Article article = articleService.getArticle(Integer.parseInt(request.getArg1()));

		if (article == null) {
			System.out.println("해당 게시물은 존재하지 않습니다.");
			return;
		}

		if (article.getMemberId() != Factory.getSession().getLoginedMember().getId()) {
			System.out.println("권한이 없습니다.");
			return;
		}

		System.out.print("제목 : ");
		String title = Factory.getScanner().nextLine();
		System.out.print("내용 : ");
		String body = Factory.getScanner().nextLine();

		article.setTitle(title);
		article.setBody(body);
		articleService.modify(article);
		System.out.println(article.getId() + "번 게시물이 수정되었습니다.");
	}

	private void actionList(Request request) {
		List<Article> articles = articleService.getArticles();
	}

	private void actionWrite(Request request) {
		if (Factory.getSession().getCurrentBoard().getId() == 1) {
			if (Factory.getSession().getLoginedMember().getId() != 1) {
				System.out.println("권한이 없습니다.");
				return;
			}
		}
		String title;
		String body;
		while (true) {
			System.out.printf("제목 : ");
			title = Factory.getScanner().nextLine();

			if (title.length() < 2) {
				System.out.println("두 글자 이상 입력해주세요.");
				continue;
			}
			break;
		}

		while (true) {
			System.out.printf("내용 : ");
			body = Factory.getScanner().nextLine();

			if (body.length() < 2) {
				System.out.println("두 글자 이상 입력해주세요.");
				continue;
			}
			break;
		}

		// 현재 게시판 id 가져오기
		int boardId = Factory.getSession().getCurrentBoard().getId();

		// 현재 로그인한 회원의 id 가져오기
		int memberId = Factory.getSession().getLoginedMember().getId();
		int newId = articleService.write(boardId, memberId, title, body);

		System.out.printf("%d번 글이 생성되었습니다.\n", newId);
	}
}
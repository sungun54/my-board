import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Main {
	public static void main(String[] args) {
		App app = new App();
		app.start();
	}
}

// Session
// 현재 사용자가 이용중인 정보
// 이 안의 정보는 사용자가 프로그램을 사용할 때 동안은 계속 유지된다.
class Session {
	private Member loginedMember;
	private Board currentBoard;

	public Member getLoginedMember() {
		return loginedMember;
	}

	public void setLoginedMember(Member loginedMember) {
		this.loginedMember = loginedMember;
	}

	public Board getCurrentBoard() {
		return currentBoard;
	}

	public void setCurrentBoard(Board currentBoard) {
		this.currentBoard = currentBoard;
	}

	public boolean isLogined() {
		return loginedMember != null;
	}
}

// Factory
// 프로그램 전체에서 공유되는 객체 리모콘을 보관하는 클래스

class Factory {
	private static Session session;
	private static DB db;
	private static BuildService buildService;
	private static ArticleService articleService;
	private static ArticleDao articleDao;
	private static MemberService memberService;
	private static MemberDao memberDao;
	private static Scanner scanner;

	public static Session getSession() {
		if (session == null) {
			session = new Session();
		}

		return session;
	}

	public static Scanner getScanner() {
		if (scanner == null) {
			scanner = new Scanner(System.in);
		}

		return scanner;
	}

	public static DB getDB() {
		if (db == null) {
			db = new DB();
		}

		return db;
	}

	public static ArticleService getArticleService() {
		if (articleService == null) {
			articleService = new ArticleService();
		}

		return articleService;
	}

	public static ArticleDao getArticleDao() {
		if (articleDao == null) {
			articleDao = new ArticleDao();
		}

		return articleDao;
	}

	public static MemberService getMemberService() {
		if (memberService == null) {
			memberService = new MemberService();
		}
		return memberService;
	}

	public static MemberDao getMemberDao() {
		if (memberDao == null) {
			memberDao = new MemberDao();
		}

		return memberDao;
	}

	public static BuildService getBuildService() {
		if (buildService == null) {
			buildService = new BuildService();
		}

		return buildService;
	}
}

// App
class App {
	private Map<String, Controller> controllers;

	// 컨트롤러 만들고 한곳에 정리
	// 나중에 컨트롤러 이름으로 쉽게 찾아쓸 수 있게 하려고 Map 사용
	void initControllers() {
		controllers = new HashMap<>();
		controllers.put("article", new ArticleController());
		controllers.put("member", new MemberController());
		controllers.put("build", new BuildController());
	}

	public App() {
		// 컨트롤러 등록
		initControllers();

		// 관리자 회원 생성
		Factory.getMemberService().join("admin", "admin", "관리자");
		// 공지사항 게시판 생성
		Factory.getArticleService().makeBoard("공지시항", "notice");
		Factory.getArticleService().makeBoard("자유게시판", "free");

		// 현재 게시판을 1번 게시판으로 선택
		Factory.getSession().setCurrentBoard(Factory.getArticleService().getBoard(1));

	}

	public void start() {

		while (true) {
			System.out.printf("명령어 : ");
			String command = Factory.getScanner().nextLine().trim();

			if (command.length() == 0) {
				continue;
			} else if (command.equals("exit")) {
				break;
			}

			Request request = new Request(command);

			if (request.isValidRequest() == false) {
				continue;
			}

			if (controllers.containsKey(request.getControllerName()) == false) {
				continue;
			}

			controllers.get(request.getControllerName()).doAction(request);
		}

		Factory.getScanner().close();
	}
}

// Request
class Request {
	private String requestStr;
	private String controllerName;
	private String actionName;
	private String arg1;
	private String arg2;
	private String arg3;

	boolean isValidRequest() {
		return actionName != null;
	}

	Request(String requestStr) {
		this.requestStr = requestStr;
		String[] requestStrBits = requestStr.split(" ");
		this.controllerName = requestStrBits[0];

		if (requestStrBits.length > 1) {
			this.actionName = requestStrBits[1];
		}

		if (requestStrBits.length > 2) {
			this.arg1 = requestStrBits[2];
		}

		if (requestStrBits.length > 3) {
			this.arg2 = requestStrBits[3];
		}

		if (requestStrBits.length > 4) {
			this.arg3 = requestStrBits[4];
		}
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public String getArg3() {
		return arg3;
	}

	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}
}

// Controller
abstract class Controller {
	abstract void doAction(Request request);
}

class BuildController extends Controller {
	private BuildService buildService;

	BuildController() {
		buildService = Factory.getBuildService();
	}

	@Override
	void doAction(Request reqeust) {
		if (reqeust.getActionName().equals("site")) {
			actionSite(reqeust);
		}
	}

	private void actionSite(Request reqeust) {
		buildService.buildSite();
	}
}

class ArticleController extends Controller {
	private ArticleService articleService;

	ArticleController() {
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
		}
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
		String removeBoardName = removeBoard.getName();
		articleService.deleteBoard(removeBoard.getId());
		System.out.println(removeBoardName + "이 삭제 되었습니다.");
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

		int removedArticleId = article.getId();
		articleService.articledelete(Integer.parseInt(request.getArg1()));
		System.out.println(removedArticleId + "번 게시물이 삭제 되었습니다.");
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

		System.out.println("==========================================================");
		System.out.printf("번호 : %d%n", article.getId());
		System.out.printf("날짜 : %s%n", article.getRegDate());
		System.out.printf("제목 : %s%n", article.getTitle());
		System.out.printf("내용 : %s%n", article.getBody());
		System.out.printf("작성자 : %s%n", articleService.getMemberName(Integer.parseInt(request.getArg1())));
		System.out.printf("조회수 : %d%n", article.getHit());
		System.out.println("==========================================================");
		
		article.setHit(article.getHit()+1);
		articleService.modify(article);
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

class MemberController extends Controller {
	private MemberService memberService;

	MemberController() {
		memberService = Factory.getMemberService();
	}

	public void setMembetService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void doAction(Request request) {
		if (request.getActionName().equals("logout")) {
			actionLogout(request);
		} else if (request.getActionName().equals("login")) {
			actionLogin(request);
		} else if (request.getActionName().equals("signup")) {
			actionSignup(request);
		} else if (request.getActionName().equals("whoami")) {
			if (Factory.getSession().getLoginedMember() == null) {
				System.out.println("로그인 후 이용해주세요.");
				return;
			}
			actionWhoAmI(request);
		}
	}

	private void actionWhoAmI(Request request) {
		System.out.println(Factory.getSession().getLoginedMember().getName());
	}

	private void actionLogout(Request request) {
		if (Factory.getSession().isLogined()) {
			Factory.getSession().setLoginedMember(null);
			System.out.println("로그아웃 성공");
		} else {
			System.out.println("이미 로그아웃 하셨습니다.");
		}
	}

	private void actionLogin(Request request) {
		if (Factory.getSession().isLogined()) {
			System.out.println("이미 로그인 하셨습니다.");
			return;
		}

		System.out.printf("로그인 아이디 : ");
		String loginId = Factory.getScanner().nextLine().trim();

		System.out.printf("로그인 비번 : ");
		String loginPw = Factory.getScanner().nextLine().trim();

		Member member = memberService.getMemberByLoginIdAndLoginPw(loginId, loginPw);

		if (member == null) {
			System.out.println("일치하는 회원이 없습니다.");
		} else {
			System.out.println(member.getName() + "님 환영합니다.");
			Factory.getSession().setLoginedMember(member);
		}
	}

	private void actionSignup(Request request) {
		String name;
		String loginId;
		String loginPw;
		String loginPwConfirm;
		while (true) {
			System.out.print("이름 : ");
			name = Factory.getScanner().nextLine().trim();
			if (name.length() < 2) {
				System.out.println("두 글자 이상 입력해주세요.");
				continue;
			}
			break;
		}

		while (true) {
			System.out.print("아이디 : ");
			loginId = Factory.getScanner().nextLine().trim();
			if (loginId.length() < 2) {
				System.out.println("두 글자 이상 입력해주세요.");
				continue;
			}
			List<Member> members = memberService.getMembers();
			for (Member member : members)
				if (member.getLoginId().equals(loginId)) {
					System.out.println("입력하신 아이디가 이미 존재합니다.");
					continue;
				}
			break;
		}

		while (true) {
			boolean loginPwValid = true;
			while (true) {
				System.out.print("비밀번호 : ");
				loginPw = Factory.getScanner().nextLine().trim();
				if (loginPw.length() < 2) {
					System.out.println("두 글자 이상 입력해주세요.");
					continue;
				}
				break;
			}
			while (true) {
				System.out.print("비밀번호 확인 : ");
				loginPwConfirm = Factory.getScanner().nextLine().trim();
				if (loginPwConfirm.length() < 2) {
					System.out.println("두 글자 이상 입력해주세요.");
					continue;
				}
				if (loginPw.equals(loginPwConfirm) == false) {
					System.out.println("로그인 비밀번호와 비밀번호확인이 일치하지 않습니다.");
					loginPwValid = false;
					break;
				}
				break;
			}

			if (loginPwValid) {
				break;
			}
		}

		memberService.join(loginId, loginPw, name);
		System.out.println(name + "님 환영합니다.");
	}
}

// Service
class BuildService {
	ArticleService articleService;
	MemberService memberService;

	BuildService() {
		articleService = Factory.getArticleService();
		memberService = Factory.getMemberService();
	}

	public void buildSite() {
		Util.makeDir("site");
		Util.makeDir("site/article");
		Util.makeDir("site/home");
		Util.makeDir("site/stat");

		String head = Util.getFileContents("site_template/part/head.html");
		String foot = Util.getFileContents("site_template/part/foot.html");
		int paging = 5;
		// 각 게시판 별 게시물리스트 페이지 생성
		List<Board> boards = articleService.articleListBoard();
		for (Board board : boards) {
			int page = 1;
			int i = 0;
			double j = 0;
			while(true) {				
				
				String fileName = board.getCode() + "-list-" + page + ".html";
	
				String html = "";
	
				List<Article> articles = articleService.getArticlesByBoardCode(board.getCode());
				for(j = 0; true; j++) {
					if(articles.size() == j) {
						j = Math.ceil((double)articles.size() / (double)paging);
						break;
					}
				}
				String template = Util.getFileContents("site_template/article/list.html");
				
				for (i = paging*(page-1); i < paging*page; i++) {
					if(articles.size() == i) {
						break;
					}
					html += "<tr>";
					html += "<td>" + articles.get(i).getId() + "</td>";
					html += "<td>" + articles.get(i).getRegDate() + "</td>";
					html += "<td>" + articleService.getMemberName(articles.get(i).getMemberId()) + "</td>";
					html += "<td><a href=\"" + articles.get(i).getId() + ".html\">" + articles.get(i).getTitle() + "</a></td>";
					html += "</tr>";
				}
				
				html = template.replace("${TR}", html);
				if (page != 1) {
					html += "<div><a href=\"" + board.getCode() + "-list-" + (page-1) + ".html\">이전 리스트</a></div>";
				}

				if (articles.size() != i) {
					html += "<div><a href=\"" + board.getCode() + "-list-" + (page+1) + ".html\">다음 리스트</a></div>";
				}
				html = head + html + foot;
				if (articles.size() == 0) {
					html = "<div>" + "게시물이 없습니다." + "</div>";
				}
				
				for(int k = 1; k <= j; k++) {
					html += "<span><a href=\"" + board.getCode() + "-list-" + k + ".html\"> [ " + k + " ]</a></span>";
				}
				
				Util.writeFileContents("site/article/" + fileName, html);
				page++;
				if(articles.size() == i) {
					break;
				}
			}
		}

		// 게시물 별 파일 생성
		List<Article> articles = articleService.getArticles();

		for (Article article : articles) {

			String html = "";

			html += "<div>제목 : " + article.getTitle() + "</div>";
			html += "<div>내용 : " + article.getBody() + "</div>";

			if (article.getId() != 1) {
				html += "<div><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></div>";
			}

			if (article.getId() != articles.size()) {
				html += "<div><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></div>";
			}
			html += "<input type=\"button\" value=\"이전 페이지로 이동\" onClick=\"history.go(-1)\">";
			html = head + html + foot;

			Util.writeFileContents("site/article/" + article.getId() + ".html", html);
		}
		
		//메인 홈페이지 생성
		String html = "";
		String template = Util.getFileContents("site_template/home/index.html");
		html = template;
		html = head + html + foot;

		Util.writeFileContents("site/home/index.html", html);
		
		//통계 페이지 생성
		//회원 수
		//전체 게시물 수
		//각 게시판별 게시물 수
		//전체 게시물 조회 수
		//각 게시판별 게시물 조회 수
		List<Member> members = memberService.getMembers();
		List<Article> freeArticles = articleService.getArticlesByBoardCode("free");
		List<Article> noticeArticles = articleService.getArticlesByBoardCode("notice");
		int allArticleHits = 0;
		int noticeArticleHits = 0;
		int freeArticleHits = 0;
		for(Article article : articles) {
			allArticleHits+=article.getHit();
		}
		for(Article article : freeArticles) {
			freeArticleHits+=article.getHit();
		}
		for(Article article : noticeArticles) {
			noticeArticleHits+=article.getHit();
		}
		html = "";
		html += "<tr>";
		html += "<th> 회원수 </th>";
		html += "<td>" + members.size() + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 전체 게시물 수 </th>";
		html += "<td>" + articles.size() + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 공지사항 게시판의 게시물 수 </th>";
		html += "<td>" + noticeArticles.size() + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 자유게시판의 게시물 수 </th>";
		html += "<td>" + freeArticles.size() + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 전체 게시물 조회수 </th>";
		html += "<td>" + allArticleHits + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 공지사항 조회수 </th>";
		html += "<td>" + noticeArticleHits + "</td>";
		html += "</tr>";
		html += "<tr>";
		html += "<th> 자유게시판 조회수 </th>";
		html += "<td>" + freeArticleHits + "</td>";
		html += "</tr>";
		template = Util.getFileContents("site_template/stat/index.html");		
		
		html = template.replace("${TR}", html);		
		
		html = head + html + foot;
		
		Util.writeFileContents("site/stat/index.html", html);
	}
}

class ArticleService {
	private ArticleDao articleDao;
	private MemberService memberService;

	ArticleService() {
		articleDao = Factory.getArticleDao();
		memberService = Factory.getMemberService();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return articleDao.getArticlesByBoardCode(code);
	}

	public void deleteBoard(int id) {
		articleDao.deleteBoard(id);
	}

	public List<Board> articleListBoard() {
		return articleDao.articleListBoard();
	}

	public void articledelete(int id) {
		articleDao.articleDelete(id);
	}

	public String getMemberName(int id) {
		return memberService.getMember(id);
	}

	public void modify(Article article) {
		articleDao.save(article);
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

class MemberService {
	private MemberDao memberDao;

	MemberService() {
		memberDao = Factory.getMemberDao();
	}

	public int join(String loginId, String loginPw, String name) {
		Member oldMember = memberDao.getMemberByLoginId(loginId);

		if (oldMember != null) {
			return -1;
		}

		Member member = new Member(loginId, loginPw, name);
		return memberDao.save(member);
	}

	public String getMember(int id) {
		return memberDao.getMember(id);
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return memberDao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}

	public List<Member> getMembers() {
		return memberDao.getMembers();
	}
}

// Dao
class ArticleDao {
	DB db;

	ArticleDao() {
		db = Factory.getDB();
	}

	public List<Article> getArticlesByBoardCode(String code) {
		return db.getArticlesByBoardCode(code);
	}

	public void deleteBoard(int id) {
		db.deleteBoard(id).delete();
	}

	public List<Board> articleListBoard() {
		return db.getBoards();
	}

	public void articleDelete(int id) {
		db.articleDelete(id).delete();
	}

	public Article getArticle(int id) {
		return db.getArticle(id);
	}

	public Board getBoardByCode(String code) {
		return db.getBoardByCode(code);
	}

	public int saveBoard(Board board) {
		return db.saveBoard(board);
	}

	public int save(Article article) {
		return db.saveArticle(article);
	}

	public Board getBoard(int id) {
		return db.getBoard(id);
	}

	public List<Article> getArticles() {
		return db.getArticles();
	}

}

class MemberDao {
	DB db;

	MemberDao() {
		db = Factory.getDB();
	}

	public List<Member> getMembers() {
		return db.getMembers();
	}

	public Member getMemberByLoginId(String loginId) {
		return db.getMemberByLoginId(loginId);
	}

	public String getMember(int id) {
		return db.getMember(id).getName();
	}

	public int save(Member member) {
		return db.saveMember(member);
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return db.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}
}

// DB
class DB {
	private Map<String, Table> tables;

	public DB() {
		String dbDirPath = getDirPath();
		Util.makeDir(dbDirPath);

		tables = new HashMap<>();

		tables.put("article", new Table<Article>(Article.class, dbDirPath));
		tables.put("board", new Table<Board>(Board.class, dbDirPath));
		tables.put("member", new Table<Member>(Member.class, dbDirPath));
	}

	public List<Article> getArticlesByBoardCode(String code) {
		Board board = getBoardByCode(code);
		// free => 2
		// notice => 1

		List<Article> articles = getArticles();
		List<Article> newArticles = new ArrayList<>();

		for (Article article : articles) {
			if (article.getBoardId() == board.getId()) {
				newArticles.add(article);
			}
		}

		return newArticles;
	}

	public File deleteBoard(int id) {
		String filePath = "db/board/" + id + ".json";
		File file = new File(filePath);
		return file;
	}

	public File articleDelete(int id) {
		String filePath = "db/article/" + id + ".json";
		File file = new File(filePath);
		return file;
	}

	public Article getArticle(int id) {
		return (Article) tables.get("article").getRow(id);
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		List<Member> members = getMembers();

		for (Member member : members) {
			if (member.getLoginId().equals(loginId) && member.getLoginPw().equals(loginPw)) {
				return member;
			}
		}

		return null;
	}

	public Member getMemberByLoginId(String loginId) {
		List<Member> members = getMembers();

		for (Member member : members) {
			if (member.getLoginId().equals(loginId)) {
				return member;
			}
		}

		return null;
	}

	public List<Member> getMembers() {
		return tables.get("member").getRows();
	}

	public Board getBoardByCode(String code) {
		List<Board> boards = getBoards();

		for (Board board : boards) {
			if (board.getCode().equals(code)) {
				return board;
			}
		}

		return null;
	}

	public List<Board> getBoards() {
		return tables.get("board").getRows();
	}

	public Member getMember(int id) {
		return (Member) tables.get("member").getRow(id);
	}

	public int saveBoard(Board board) {
		return tables.get("board").saveRow(board);
	}

	public String getDirPath() {
		return "db";
	}

	public int saveMember(Member member) {
		return tables.get("member").saveRow(member);
	}

	public Board getBoard(int id) {
		return (Board) tables.get("board").getRow(id);
	}

	public List<Article> getArticles() {
		return tables.get("article").getRows();
	}

	public int saveArticle(Article article) {
		return tables.get("article").saveRow(article);
	}

	public void backup() {
		for (String tableName : tables.keySet()) {
			Table table = tables.get(tableName);
			table.backup();
		}
	}
}

// Table
class Table<T> {
	private Class<T> dataCls;
	private String tableName;
	private String tableDirPath;

	public Table(Class<T> dataCls, String dbDirPath) {
		this.dataCls = dataCls;
		this.tableName = Util.lcfirst(dataCls.getCanonicalName());
		this.tableDirPath = dbDirPath + "/" + this.tableName;

		Util.makeDir(tableDirPath);
	}

	private String getTableName() {
		return tableName;
	}

	public int saveRow(T data) {
		Dto dto = (Dto) data;

		if (dto.getId() == 0) {
			int lastId = getLastId();
			int newId = lastId + 1;
			((Dto) data).setId(newId);
			setLastId(newId);
		}

		String rowFilePath = getRowFilePath(dto.getId());

		Util.writeJsonFile(rowFilePath, data);

		return dto.getId();
	};

	private String getRowFilePath(int id) {
		return tableDirPath + "/" + id + ".json";
	}

	private void setLastId(int lastId) {
		String filePath = getLastIdFilePath();
		Util.writeFileContents(filePath, lastId);
	}

	private int getLastId() {
		String filePath = getLastIdFilePath();

		if (Util.isFileExists(filePath) == false) {
			int lastId = 0;
			Util.writeFileContents(filePath, lastId);
			return lastId;
		}

		return Integer.parseInt(Util.getFileContents(filePath));
	}

	private String getLastIdFilePath() {
		return this.tableDirPath + "/lastId.txt";
	}

	public T getRow(int id) {
		return (T) Util.getObjectFromJson(getRowFilePath(id), dataCls);
	}

	public void backup() {

	}

	List<T> getRows() {
		int lastId = getLastId();

		List<T> rows = new ArrayList<>();

		for (int id = 1; id <= lastId; id++) {
			T row = getRow(id);

			if (row != null) {
				rows.add(row);
			}
		}

		return rows;
	};
}

// DTO
abstract class Dto {
	private int id;
	private String regDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	Dto() {
		this(0);
	}

	Dto(int id) {
		this(id, Util.getNowDateStr());
	}

	Dto(int id, String regDate) {
		this.id = id;
		this.regDate = regDate;
	}
}

class Board extends Dto {
	private String name;
	private String code;

	public Board() {
	}

	public Board(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

class Article extends Dto {
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

class ArticleReply extends Dto {
	private int id;
	private String regDate;
	private int articleId;
	private int memberId;
	private String body;

	ArticleReply() {

	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}

class Member extends Dto {
	private String loginId;
	private String loginPw;
	private String name;

	public Member() {

	}

	public Member(String loginId, String loginPw, String name) {
		this.loginId = loginId;
		this.loginPw = loginPw;
		this.name = name;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPw() {
		return loginPw;
	}

	public void setLoginPw(String loginPw) {
		this.loginPw = loginPw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}

// Util
class Util {
	// 현재날짜문장
	public static String getNowDateStr() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = Date.format(cal.getTime());
		return dateStr;
	}

	// 파일에 내용쓰기
	public static void writeFileContents(String filePath, int data) {
		writeFileContents(filePath, data + "");
	}

	// 첫 문자 소문자화
	public static String lcfirst(String str) {
		String newStr = "";
		newStr += str.charAt(0);
		newStr = newStr.toLowerCase();

		return newStr + str.substring(1);
	}

	// 파일이 존재하는지
	public static boolean isFileExists(String filePath) {
		File f = new File(filePath);
		if (f.isFile()) {
			return true;
		}

		return false;
	}

	// 파일내용 읽어오기
	public static String getFileContents(String filePath) {
		String rs = null;
		try {
			// 바이트 단위로 파일읽기
			FileInputStream fileStream = null; // 파일 스트림

			fileStream = new FileInputStream(filePath);// 파일 스트림 생성
			// 버퍼 선언
			byte[] readBuffer = new byte[fileStream.available()];
			while (fileStream.read(readBuffer) != -1) {
			}

			rs = new String(readBuffer);

			fileStream.close(); // 스트림 닫기
		} catch (Exception e) {
			e.getStackTrace();
		}

		return rs;
	}

	// 파일 쓰기
	public static void writeFileContents(String filePath, String contents) {
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(new FileOutputStream(filePath));
			bs.write(contents.getBytes()); // Byte형으로만 넣을 수 있음
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			try {
				bs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Json안에 있는 내용을 가져오기
	public static Object getObjectFromJson(String filePath, Class cls) {
		ObjectMapper om = new ObjectMapper();
		Object obj = null;
		try {
			obj = om.readValue(new File(filePath), cls);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public static void writeJsonFile(String filePath, Object obj) {
		ObjectMapper om = new ObjectMapper();
		try {
			om.writeValue(new File(filePath), obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void makeDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}
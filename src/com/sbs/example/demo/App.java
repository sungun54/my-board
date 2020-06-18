package com.sbs.example.demo;
import java.util.HashMap;
import java.util.Map;

import com.sbs.example.demo.controller.ArticleController;
import com.sbs.example.demo.controller.BuildController;
import com.sbs.example.demo.controller.Controller;
import com.sbs.example.demo.controller.MemberController;
import com.sbs.example.demo.controller.Request;
import com.sbs.example.demo.factory.Factory;

// App
class App {
	private Map<String, Controller> controllers;
	boolean workStarted = false;
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
		Factory.getDBConnection().connect();
		// 현재 게시판을 1번 게시판으로 선택
		Factory.getArticleService().makeBoard("공지사항", "notice");
		Factory.getArticleService().makeBoard("자유게시판", "free");
		Factory.getMemberService().join("admin", "admin", "관리자");
		Factory.getSession().setCurrentBoard(Factory.getArticleService().getBoard(1));
	}

	public void start() {		
		while (true) {
//			startWork();
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
	
//	public void startWork() {
//		workStarted = true;
//		new Thread(() -> {
//			while(workStarted) {
//				Factory.getBuildService().buildSite();
//				try {
//					Thread.sleep(10000);
//				}
//				catch ( InterruptedException e ) {
//				}
//			}
//		}).start();
//	}
}
package com.sbs.example.demo.service;
import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.util.Util;

// Service
public class BuildService {
	ArticleService articleService;
	MemberService memberService;

	public BuildService() {
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
				html += "<h1>" + board.getName() + "</h1>";
				List<Article> articles = articleService.getArticlesByBoardCode(board.getCode());
				for(j = 0; true; j++) {
					if(articles.size() == j) {
						j = Math.ceil((double)articles.size() / (double)paging);
						break;
					}
				}
				if ( articles.size() != 0 ) {
					
					String template = Util.getFileContents("site_template/article/list.html");
					for (i = paging*(page-1); i < paging*page; i++) {
						if(articles.size() == i) {
							break;
						}
						html += "<tr>";
						html += "<td>" + articles.get(i).getId() + "</td>";
						html += "<td class=\"title\"><a href=\"" + articles.get(i).getId() + ".html\">" + articles.get(i).getTitle() + "</a></td>";
						html += "<td>" + articleService.getMemberName(articles.get(i).getMemberId()) + "</td>";
						html += "<td>" + articles.get(i).getRegDate() + "</td>";
						html += "<td>" + articles.get(i).getHit() + "</td>";
						html += "</tr>";
					}
					
					html = template.replace("${TR}", html);
				}
//				if (page != 1) {
//					html += "<div><a href=\"" + board.getCode() + "-list-" + (page-1) + ".html\">이전 리스트</a></div>";
//				}
//
//				if (articles.size() != i) {
//					html += "<div><a href=\"" + board.getCode() + "-list-" + (page+1) + ".html\">다음 리스트</a></div>";
//				}
				
				String x = "";
				List<Board> boards2 = articleService.articleListBoard();
				for ( Board board2 : boards2 ) {
					x += "<li><a href=\"../article/" + board2.getCode() + "-list-1.html\">" + board2.getName() + "</a></li>";
				}
				head = head.replace("{$QQ}", x);
				
				html = head + html;
				if (articles.size() == 0) {
					html += "<h2>" + "게시물이 없습니다." + "</h2>";
				}
				html += "<div class=\"page\">";
				for(int k = 1; k <= j; k++) {
					html += "<span><a href=\"" + board.getCode() + "-list-" + k + ".html\"> [ " + k + " ]</a></span>";
				}
				html += "</div>";
				html += foot;
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
			
			html = "";
			html += "<tr>";
			html += "<th> 번호 </th>";
			html += "<td>" + article.getId() + "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<th> 제목 </th>";
			html += "<td>" + article.getTitle() + "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<th> 내용 </th>";
			html += "<td>" + article.getBody() + "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<th> 작성자 </th>";
			html += "<td>" + articleService.getMemberName(article.getMemberId()) + "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<th> 작성 날짜 </th>";
			html += "<td>" + article.getRegDate()+ "</td>";
			html += "</tr>";
			html += "<tr>";
			html += "<th> 조회수 </th>";
			html += "<td>" + article.getHit() + "</td>";
			html += "</tr>";						
			
			String template = Util.getFileContents("site_template/article/detail.html");	
			
			html = template.replace("${TR}", html);		
			html += "<div class=\"list\">";
			if (article.getId() != 1) {
				html += "<span><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></span>";
			}
			if (article.getId() != articles.size()) {
				html += "<span><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></span>";
			}
			html += "<div><input type=\"button\" value=\"글 목록으로 이동\" onclick=\"location.href='notice-list-1.html'\"></div>";
			html += "</div>";
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
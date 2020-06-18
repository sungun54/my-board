package com.sbs.example.demo.controller;
import java.util.List;

import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.MemberService;

public class MemberController extends Controller {
	private MemberService memberService;

	public MemberController() {
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
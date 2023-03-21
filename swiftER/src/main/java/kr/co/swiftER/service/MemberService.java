package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.MemberDAO;
import kr.co.swiftER.repo.MemberRepo;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;

@Service
public class MemberService {
	
	@Autowired MemberDAO dao;
	@Autowired MemberRepo repo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/* 회원 약관 불러오기 */
	public MemberTermsVO selectTerms() {
		return dao.selectTerms();
	}
	
	/* 회원가입 유효성 검사 */
	public int countUid(String uid) {
		return repo.countByUid(uid);
	}

	/* 회원가입 */
	public int insertMember(MemberVO vo) {
		vo.setPass(passwordEncoder.encode(vo.getPass()));
		int result = dao.insertMember(vo);
		return result;
	}
	
	/* 비밀번호 수정 */
	public int updatePass(String pass2, String uid) {
		String pass = passwordEncoder.encode(pass2);
		int result = dao.updatePass(pass, uid);
		return result;
	}

	/* 마이페이지 회원정보 */
	public MemberVO selectMember(String uid) {
		return dao.selectMember(uid);
	}
	
	/* 마이페이지 게시판 리스트 불러오기 */
	public List<CommunityArticleVO> selectCaList(String uid) {
		
		return dao.selectCaList(uid);
	}
	
	/* 마이페이지 게시판 리스트 전체 불러오기 */
	public List<CommunityArticleVO> selectCaListAll(String uid) {
		
		return dao.selectCaListAll(uid);
	}

	/* 마이페이지 리뷰 리스트 불러오기 */
	public List<ERReviewVO> selectErReviewList(String uid) {

		return dao.selectErReviewList(uid);
	}

	/* 내가 작성한 글 갯수 */
	public int countCa(String uid) {
		return dao.countCa(uid);
	}
	
}

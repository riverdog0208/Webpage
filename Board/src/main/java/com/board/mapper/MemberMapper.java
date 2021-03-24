package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.Dto.MemberDto;

@Mapper
public interface MemberMapper {

	public int insertMember(MemberDto params);

	public MemberDto selectMemberDetail(Long num);

	public List<MemberDto> selectMemberList();
	
	public int selectMemberTotalCount();

}
package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.domain.BoardDTO;
import com.board.paging.Criteria;

@Mapper
public interface BoardMapper {

	public int insertBoard(BoardDTO params);

	public BoardDTO selectBoardDetail(Long idx);

	public int updateBoard(BoardDTO params);

	public int deleteBoard(Long idx);

	public List<BoardDTO> selectBoardList();

	public int selectBoardTotalCount();
	
	public boolean cntPlus(Long idx);
	
	public List<BoardDTO> selectBoardList(BoardDTO params);

	public int selectBoardTotalCount(Criteria criteria);

}
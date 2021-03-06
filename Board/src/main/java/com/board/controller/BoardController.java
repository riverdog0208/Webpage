package com.board.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.board.constant.Method;
import com.board.domain.AttachDTO;
import com.board.domain.BoardDTO;
import com.board.service.BoardService;
import com.board.util.UiUtils;

@Controller
public class BoardController extends UiUtils {

	@Autowired
	private BoardService boardService;

	@GetMapping("/")
	public String index(@ModelAttribute("params") BoardDTO params,Model model,@RequestParam(value = "idx", required = false) Long idx) {
		model.addAttribute("board", new BoardDTO());
		return "home/index";
	}
	
	
	@GetMapping(value = "board/write.do")
	public String openBoardWrite(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx,String writer ,Model model,Authentication authentication) {
		if (idx == null) {
			model.addAttribute("board", new BoardDTO());
		} else {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			BoardDTO board = boardService.getBoardDetail(idx);
			if (board == null || "Y".equals(board.getDeleteYn())) {
				return showMessageWithRedirect("?????? ?????????????????? ?????? ????????? ??????????????????.", "list.do", Method.GET, null, model);
			}
			
			if(userDetails.getUsername().equals(board.getWriter()) || userDetails.getUsername()=="admin") {
			model.addAttribute("board", board);
			}
			else {
				model.addAttribute("msg", "?????? ???????????? ????????? ?????? ????????????.");
				model.addAttribute("url", "list.do");
				return "utils/message";
			}
			List<AttachDTO> fileList = boardService.getAttachFileList(idx);
			model.addAttribute("fileList", fileList);
		}

		return "board/write";
	}

	@PostMapping(value = "board/register.do")
	public String registerBoard(final BoardDTO params, final MultipartFile[] files, Model model) {

		Map<String, Object> pagingParams = getPagingParams(params);

		
		try {
			boolean isRegistered = boardService.registerBoard(params, files);
			if (isRegistered == false) {
				return showMessageWithRedirect("????????? ????????? ?????????????????????.", "list.do", Method.GET, pagingParams, model);
			}
		} catch (DataAccessException e) {
			return showMessageWithRedirect("?????????????????? ?????? ????????? ????????? ?????????????????????.", "list.do", Method.GET, pagingParams, model);

		} catch (Exception e) {
			return showMessageWithRedirect("????????? ?????????????????????.", "/", Method.GET, pagingParams, model);
		}

		return showMessageWithRedirect("????????? ????????? ?????????????????????.", "list.do", Method.GET, pagingParams, model);
	}
	
	
	
	@GetMapping(value = "board/list.do")
	public String openBoardList(@ModelAttribute("params") BoardDTO params, Model model) {
		List<BoardDTO> boardList = boardService.getBoardList(params);
		model.addAttribute("boardList", boardList);

		return "board/list";
	}
	
	@GetMapping(value = "board/view.do")
	public String openBoardDetail(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			return showMessageWithRedirect("???????????? ?????? ???????????????.", "list.do", Method.GET, null, model);
		}

		BoardDTO board = boardService.getBoardDetail(idx);
		if (board == null || "Y".equals(board.getDeleteYn())) {
			return showMessageWithRedirect("?????? ?????????????????? ?????? ????????? ??????????????????.", "list.do", Method.GET, null, model);
		}
		model.addAttribute("board", board);
		boardService.cntPlus(idx);
		List<AttachDTO> fileList = boardService.getAttachFileList(idx); 
		model.addAttribute("fileList", fileList); 

		return "board/view";
	}


	@RequestMapping(value = "board/delete.do", method = RequestMethod.GET)
	public String deleteBoard(Long idx, Model model){
		model.addAttribute("msg", "????????? ?????????????????????."); 
    	model.addAttribute("url", "list.do"); 
		boardService.deleteBoard(idx);
		return "utils/message";
	}
	
	@GetMapping("board/download.do")
	public void downloadAttachFile(@RequestParam(value = "idx", required = false) final Long idx, Model model, HttpServletResponse response) {

		if (idx == null) throw new RuntimeException("???????????? ?????? ???????????????.");

		AttachDTO fileInfo = boardService.getAttachDetail(idx);
		if (fileInfo == null || "Y".equals(fileInfo.getDeleteYn())) {
			throw new RuntimeException("?????? ????????? ?????? ??? ????????????.");
		}

		String uploadDate = fileInfo.getInsertTime().format(DateTimeFormatter.ofPattern("yyMMdd"));
		String uploadPath = Paths.get("C:", "develop", "upload", uploadDate).toString();

		String filename = fileInfo.getOriginalName();
		File file = new File(uploadPath, fileInfo.getSaveName());

		try {
			byte[] data = FileUtils.readFileToByteArray(file);
			response.setContentType("application/octet-stream");
			response.setContentLength(data.length);
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(filename, "UTF-8") + "\";");

			response.getOutputStream().write(data);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (IOException e) {
			throw new RuntimeException("?????? ??????????????? ?????????????????????.");

		} catch (Exception e) {
			throw new RuntimeException("???????????? ????????? ?????????????????????.");
		}
	}
	
}
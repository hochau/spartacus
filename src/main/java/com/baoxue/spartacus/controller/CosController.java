package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.service.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年3月14日 下午3:51:27
 */
@RestController
@RequestMapping("/cos")
public class CosController {
	
	@Autowired
	private CosService cosService;


	@RequestMapping("/search")
	public BaseResp search(@ModelAttribute BaseReq baseReq) throws BlogException {
		return cosService.search(baseReq.getSearchContent(), baseReq.getCosType(), baseReq.getRootPath(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}

	@RequestMapping(value = "/syncData")
	public BaseResp syncData() {
		return cosService.syncData();
	}

	@RequestMapping(value = "/batchMove")
	public BaseResp batchMove(@ModelAttribute BaseReq baseReq) {
		return cosService.batchMove(baseReq.getKeysStr(), baseReq.getDestDirPath(), baseReq.getSubAddress());
	}

	@RequestMapping(value = "/batchDelete")
	public BaseResp batchDelete(@ModelAttribute BaseReq baseReq) {
		return cosService.batchDelete(baseReq.getKeysStr(), baseReq.getSubAddress());
	}

	@RequestMapping(value = "/batchDownload")
	public BaseResp batchDownload(@ModelAttribute BaseReq baseReq) {
		return cosService.batchDownload(baseReq.getKeysStr());
	}

	@RequestMapping(value = "/rename")
	public BaseResp rename(@ModelAttribute BaseReq baseReq) {
		return cosService.rename(baseReq.getKey(), baseReq.getNewFileName(), baseReq.getSubAddress());
	}

	@RequestMapping(value = "/download")
	public BaseResp download(@ModelAttribute BaseReq baseReq) {
		return cosService.download(baseReq.getKey());
	}

	@RequestMapping(value = "/listTags")
	public BaseResp listTags() {
		return cosService.listTags();
	}

	@RequestMapping(value = "/webUploader", method = RequestMethod.POST/*, produces = "application/json;charset=utf8"*/)
	public BaseResp webUploader(@RequestParam("file") MultipartFile file, String parentDirPath, Long parentId, String tags) throws IOException {
		return cosService.webUploader(parentDirPath, parentId, file.getOriginalFilename(), tags, file.getBytes());
	}
	
	@RequestMapping("/listObjects")
	public BaseResp listObjects(@ModelAttribute BaseReq baseReq) {
		return cosService.listObjects(baseReq.getIsRecursive(), baseReq.getDirPath(), baseReq.getCurrentPage(), baseReq.getPageSize(), baseReq.getTag());
	}
	
	@RequestMapping("/batchSetObjectAcl")
	public BaseResp batchSetObjectAcl(@ModelAttribute BaseReq baseReq) {
		return cosService.batchSetObjectAcl(baseReq.getKeysStr(), baseReq.getAclFlag(), baseReq.getSubAddress());
	}
	
	@RequestMapping("/deleteDirectory")
	public BaseResp deleteDirectory(@ModelAttribute BaseReq baseReq) {
		return cosService.deleteDirectory(baseReq.getTargetDirPath(), baseReq.getSubAddress());
	}
	
	@RequestMapping("/createDirectory")
	public BaseResp createDirectory(@ModelAttribute BaseReq baseReq) {
		return cosService.createDirectory(baseReq.getParentDirPath(), baseReq.getParentId(), baseReq.getNewDirName());
	}
	
	@RequestMapping("/getDirectoryTree")
	public BaseResp getDirectoryTree(@ModelAttribute BaseReq baseReq) {
		return cosService.getDirectoryTree(baseReq.getRootDirPath());
	}
	
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST/*, produces = "application/json;charset=utf8"*/)
	public BaseResp fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
		return cosService.fileUpload(file.getOriginalFilename(), file.getBytes());
	}
	
}

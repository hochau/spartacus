package com.baoxue.spartacus.security.browser.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * 解决在iframe内嵌页中打开跳转页面的问题
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年12月25日 下午7:53:56
 */
@Component
public class RedirectHelper {

	public void jumpOutTheIFrameAndRedirectToLoginPage(HttpServletRequest request, HttpServletResponse response, String targetUrl) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
        StringBuilder builder = new StringBuilder();
        builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
        builder.append("parent.window.location.href='"+request.getContextPath()+targetUrl+"';");
        builder.append("</script>");
        response.getWriter().print(builder.toString());
        response.getWriter().close();
	}
	
}

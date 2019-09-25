package com.baoxue.spartacus.exception;

import javax.servlet.http.HttpServletRequest;

import com.baoxue.spartacus.globals.Globals;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BlogException.class)
    @ResponseBody
    public ErrorInfo jsonErrorHandler(HttpServletRequest req, BlogException e) throws Exception {
        ErrorInfo error = new ErrorInfo();
        error.setCode(Globals.CODE_500);
        error.setMsg(Globals.MSG_500);
        error.setData(e.getMessage());
        error.setUrl(req.getRequestURL().toString());
        return error;
    }

}


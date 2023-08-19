package io.kischang.readme.base.exception;

import io.jsonwebtoken.JwtException;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@ControllerAdvice(basePackages = "io.kischang")
public class HandlerException {

	@ExceptionHandler({ JwtException.class })
	@ResponseStatus(HttpStatus.OK)
	public R<String> processException(JwtException e) {
		log.error("JwtException err: {} ", e.getMessage(), e);
		return R.fail(403, e.getMessage());
	}

	@ExceptionHandler({ BaseException.class })
	@ResponseStatus(HttpStatus.OK)
	public R<String> processException(BaseException e) {
		log.error("BaseException code: {} ", e.getCode(), e);
		return R.fail(e.getCode(), e.getMessage());
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.OK)
	public R<String> processException(Exception e) {
		log.error("Exception", e);
		return R.fail("系统错误");
	}

}

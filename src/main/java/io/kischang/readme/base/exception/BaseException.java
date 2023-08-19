package io.kischang.readme.base.exception;

/**
 * 异常处理类
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 异常码 */
	protected int code;

	public BaseException() {
		super("系统异常");
		this.code = -1;
	}

	public BaseException(String message) {
		super(message);
		this.code = -1;
	}

	public BaseException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}

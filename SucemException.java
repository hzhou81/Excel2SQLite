package com.sucem.common;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>Title: 自定义异常类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: 上海二手车交易市场</p>
 *
 * @author 孙汇洲
 * @version 1.0
 */
public class SucemException
    extends Exception {
  /**
	 * 
	 */
	private static final long serialVersionUID = -1661147537033476999L;
private Exception _exp = null;
  /**
   * 构造一个自定义异常类
   */
  public SucemException() {
    super();
  }

  /**
   * 构造一个自定义异常类
   * @param ErrorMessage String         异常信息
   */
  public SucemException(String ErrorMessage) {
    super(ErrorMessage);
  }

  /**
   * 构造一个自定义异常类
   * @param exception Exception         常规异常类
   */
  public SucemException(Exception exception) {
    // super(exception.getMessage());
    super(exception);
    _exp = exception;
  }

  /**
   * 返回当前错误类的错误
   * @return Exception
   */
  public Exception getException() {
    return this._exp;
  }

  /**
   * 在控制台输出当前的错误信息
   */
  public void printStackTrace() {
    if (_exp != null) {
      _exp.printStackTrace();
    }
  }

  /**
   * 向一个打印流流输出当前错误
   * @param s PrintStream
   */
  public void printStackTrace(PrintStream s) {
    if (_exp != null) {
      _exp.printStackTrace(s);
    }
  }

  /**
   * 输出当前错误
   * @param pw PrintWriter
   */
  public void printStackTrace(PrintWriter pw) {
    if (_exp != null) {
      _exp.printStackTrace(pw);
    }
  }
}

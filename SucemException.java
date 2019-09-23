package com.sucem.common;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>Title: �Զ����쳣��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: �Ϻ����ֳ������г�</p>
 *
 * @author �����
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
   * ����һ���Զ����쳣��
   */
  public SucemException() {
    super();
  }

  /**
   * ����һ���Զ����쳣��
   * @param ErrorMessage String         �쳣��Ϣ
   */
  public SucemException(String ErrorMessage) {
    super(ErrorMessage);
  }

  /**
   * ����һ���Զ����쳣��
   * @param exception Exception         �����쳣��
   */
  public SucemException(Exception exception) {
    // super(exception.getMessage());
    super(exception);
    _exp = exception;
  }

  /**
   * ���ص�ǰ������Ĵ���
   * @return Exception
   */
  public Exception getException() {
    return this._exp;
  }

  /**
   * �ڿ���̨�����ǰ�Ĵ�����Ϣ
   */
  public void printStackTrace() {
    if (_exp != null) {
      _exp.printStackTrace();
    }
  }

  /**
   * ��һ����ӡ���������ǰ����
   * @param s PrintStream
   */
  public void printStackTrace(PrintStream s) {
    if (_exp != null) {
      _exp.printStackTrace(s);
    }
  }

  /**
   * �����ǰ����
   * @param pw PrintWriter
   */
  public void printStackTrace(PrintWriter pw) {
    if (_exp != null) {
      _exp.printStackTrace(pw);
    }
  }
}

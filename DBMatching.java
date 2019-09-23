package com.sucem.db;

import java.util.Vector;

import com.sucem.util.ViewerModel;
import com.sucem.common.SucemException;

/**
 * <p>Title: SQL��乹��ƥ��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author :�����
 * @version 1.0
 */
public class DBMatching {
  String _KeyName = "";
  ViewerModel _ViewModel = new ViewerModel();
  Vector _KeyNameVector = new Vector();
  Vector _KeyValueVector = new Vector();

  public DBMatching(String KeyName, ViewerModel ViewModel) {
    _KeyName = KeyName;
    _ViewModel = ViewModel;
    _KeyNameVector = parseKeyName(_KeyName); // ��˳�����е�SQL����Key����
    _KeyValueVector = parseKeyValue(_ViewModel); // ��˳�����е�SQL����Value����
  }

  private Vector parseKeyName(String KeyName) {
    String tmp = KeyName;
    String onekey = "";
    Vector v = new Vector();
    int i = tmp.indexOf(",");
    while (i != -1) {
      onekey = tmp.substring(0, i);
      if (_ViewModel.getValue(onekey.trim()) != null) {
        v.addElement(onekey);
      }
      tmp = tmp.substring(i + 1);
      i = tmp.indexOf(",");
    }
    if (_ViewModel.getValue(tmp.trim()) != null) {
      v.addElement(tmp);
    }
    return v;
  }

  private Vector parseKeyValue(ViewerModel ViewModel) {
    Vector v = new Vector();
    String KeyName = "";
    Object KeyValueTmp = null;
    String KeyValue = null;
    for (int i = 0; i < _KeyNameVector.size(); i++) {
      KeyName = (String) _KeyNameVector.elementAt(i);
      // KeyValue = (String) ViewModel.getValue(KeyName);
      KeyValueTmp = ViewModel.getValue(KeyName);
      if (KeyValueTmp != null) {
        KeyValue = KeyValueTmp.toString();
        v.addElement(KeyValue);
      }
      // ViewModel.getValue(KeyName);
    }
    return v;
  }

  public String ReturnKeyNames() {
    return _KeyName;
  }

  /**
   * ���ع��˹����ֶ���
   *
   * @return String
   */
  public String ReturnFilterKeyNames() {
    /*
         //  ���������ģ��������2���ǿ�ֵ��ʱ�򣬻�������
         String tmp = _KeyName;
         String onekey = "";
         String output = "";
         int i = tmp.indexOf(",");
         while (i != -1) {
      onekey = tmp.substring(0, i);
      if (_ViewModel.getValue(onekey) != null) {
        output += onekey;
        tmp = tmp.substring(i + 1);
        i = tmp.indexOf(",");
        if (i != -1) {
          output += ",";
        }
      }else{
        tmp = tmp.substring(i + 1);
        i = tmp.indexOf(",");
      }
         }
         return output;
     */
    String output = "";
    for (int i = 0; i < _KeyNameVector.size(); i++) {

      if (i < _KeyNameVector.size() - 1) {
        output += _KeyNameVector.elementAt(i).toString().trim() + ",";
      }
      else {
        output += _KeyNameVector.elementAt(i);
      }
    }
    return output;
  }

  public Vector ReturnKeyNamesVector() {
    return _KeyNameVector;
  }

  public ViewerModel ReturnViewerModel() {
    return _ViewModel;
  }

  public Vector ReturnViewerModelVector() {
    return _KeyValueVector;
  }

  public String ReturnKeyValues() {
    String TMP = "";
    String Key = "";
    for (int i = 0; i < _KeyValueVector.size() - 1; i++) {
      Key = (String) _KeyValueVector.elementAt(i);
      TMP += "'" + Key + "',";
    }
    TMP += "'" + (String) _KeyValueVector.lastElement() + "'";
    return TMP;
  }

  /**
   * ���ع��˹���ֵ
   * @return String
   */
  public String ReturnFilterKeyValue() {
    String output = "";
    for (int i = 0; i < _KeyValueVector.size(); i++) {
      if (i < _KeyValueVector.size() - 1) {
        output += "'" + _KeyValueVector.elementAt(i).toString().trim() + "',";
      }
      else {
        output += "'" + _KeyValueVector.elementAt(i).toString().trim() + "'";
      }
    }
    return output;

  }
  /**
   *
   * @return String
   * @throws SucemException
   */
  public String ReturnKeyNamesAndValues() throws SucemException {
    String TMP = " ";
    for (int i = 0; i < _KeyNameVector.size() - 1; i++) {
      TMP += _KeyNameVector.elementAt(i);
      if(_KeyValueVector.elementAt(i)==null){
        throw new SucemException("��������п�ֵ!");
      }
      TMP += "='" + _KeyValueVector.elementAt(i) + "',";
    }
    TMP += _KeyNameVector.lastElement() + "='" + _KeyValueVector.lastElement() + "'";
    return TMP;
  }
}

package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sucem.db.DataBase;
import com.sucem.db.DataNotFoundException;
import com.sucem.db.DataSet;

/**
 * <p>����: ������Դ������дExcel����</p>
 *
 * <p>����: ����ͳ�Ʊ���</p>
 *
 * <p>��Ȩ: Copyright (c) 2005</p>
 *
 * <p>��˾: �Ϻ����ֳ������г����޹�˾</p>
 *
 * @���� �����
 * @�汾 1.0.0
 */
public class PoiExcel {
  String _fileName = null; // Excel�ļ���
  InputStream _fs = null; // POI�ļ�ϵͳ
  Workbook _wb = null; // ������
  Row _row = null; // �����
  Cell _cell = null; // ��Ԫ��
  Sheet _sheet = null; // ���(������)

  /**
   * ����һ����дExcel����
   * @param FileName String     Excel�ļ���
   */
  public PoiExcel(String FileName) {
    _fileName = FileName;
    try {
      if (ExcelFileExists(_fileName)) {
    	String extString = _fileName.substring(_fileName.lastIndexOf("."));
        _fs = new FileInputStream(_fileName);
        if(".xls".equals(extString)){
        	_wb = new HSSFWorkbook(_fs);
        }else if(".xlsx".equals(extString)){
        	_wb=new XSSFWorkbook(_fs);
        }else{
        	_wb=null;
        }
      }
    }
    catch (IOException ex) {
    }
  }
  /**
   * ����һ����дExcel����
   * @param FileObject File     Excel�ļ�����
   */
  public PoiExcel(File FileObject){
    if(FileObject.exists()){
      try {
        _fs = new FileInputStream(FileObject);
        _wb=new HSSFWorkbook(_fs);
      }
      catch (IOException ex) {
      }
    }
  }
  /**
   * �ж�Excel�ļ��Ƿ��Ѿ�����
   * @param FileName String    Excel�ļ���
   * @return boolean           Excel�ļ��Ƿ���ڵı�־
   */
  private boolean ExcelFileExists(String FileName) {
    File file = new File(FileName);
    return file.exists();
  }

  /**
   * ���ݱ����ȡ�ñ��
   * @param SheetName String    �����
   * @return HSSFSheet          ������
   */
  public Sheet getSheet(String SheetName) {
    return _wb.getSheet(SheetName);
  }

  /**
   * ���ݱ��ı��ȡ�ñ��
   * @param SheetID int         �����
   * @return HSSFSheet          ������
   */
  private Sheet getSheet(int SheetID) {
    return _wb.getSheetAt(SheetID);
  }
  /**
   * ��DataSet��д��Excel���Ĺ̶�λ��
   * @param SheetName String   ��������(Sheet����)
   * @param ds DataSet         ����Դ
   * @param initRow int        ������ʼ�к�
   * @param initColumn int     �����ʼ���к�
   */
  public void ExportDataSet2Excel(String SheetName, DataSet ds, int initRow,
                                  int initColumn) {
    _sheet=getSheet(SheetName);
    int maxColumn=ds.getColumnCount();
    int maxRow=ds.getRowCount();
    for(int i=0;i<maxRow;i++){
      for(int j=0;j<maxColumn;j++){
        // Object tmp=ds.getValueAt(i,j);
        ExportString2Excel(_sheet,ds.getValueAt(i,j).toString(),initRow+i,initColumn+j);
      }
    }
  }
  /**
   * ��DataSet��д��Excel��һ�����Ĺ̶�λ��
   * @param SheetIndex int     ����λ��
   * @param ds DataSet         ����Դ
   * @param initRow int        ������ʼ�к�
   * @param initColumn int     �����ʼ���к�
   */
  public void ExportDataSet2Excel(int SheetIndex, DataSet ds, int initRow,int initColumn){
    String tmp="";
    _sheet=getSheet(SheetIndex);
    int maxColumn=ds.getColumnCount();
    int maxRow=ds.getRowCount();
    for(int i=0;i<maxRow;i++){
      for(int j=0;j<maxColumn;j++){
    	if(ds.getValueAt(i,j)==null || "".equals(ds.getValueAt(i,j))){
    		tmp="";
    	}else{
    		tmp=ds.getValueAt(i,j).toString();
    	}
    	// System.out.print(tmp+" ");
        ExportString2Excel(_sheet,tmp,initRow+i,initColumn+j);
      }
    }
  }
  /**
   * ���ñ����ָ�����е�����
   * @param sheet HSSFSheet    ������
   * @param Content String     ����ڵ�����
   * @param row int            ָ������
   * @param col int            ָ������
   */
  private void ExportString2Excel(Sheet sheet,String Content,int row,int col){
    _row=sheet.getRow(row);
    if(_row==null) return;
    _cell=_row.getCell((short)col);
    if(_cell==null) return;
    //_cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    _cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    // _cell.setCellValue(Integer.parseInt(Content));
    _cell.setCellValue(Content);
  }
  /**
   * ���ñ����ָ�����е�����
   * @param SheetIndex int     ������
   * @param Content String     ����ڵ�����
   * @param row int            ָ������
   * @param col int            ָ������
   */
  public void ExportString2Excel(int SheetIndex,String Content,int row,int col){
    _sheet=getSheet(SheetIndex);
    _row=_sheet.getRow(row);
    if(_row==null) return;
    _cell=_row.getCell((short)col);
    if(_cell==null) return;
    //_cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    _cell.setCellValue(Content);

  }
  /**
   * ������
   */
  public void addRow() {
    // _sheet=getSheet(0);
    _sheet = _wb.createSheet();
    //_wb.setSheetName(0, "fgd", HSSFWorkbook.ENCODING_UTF_16);
    _row = _sheet.createRow( (short) 1);
    _cell = _row.createCell( (short) 0);
    _cell.setCellValue("haha");
    _cell = _row.createCell( (short) 1);
    //_cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    _cell.setCellValue("�ڶ�����Ԫ��");
    _cell = _row.createCell( (short) 2);
    //_cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    _cell.setCellValue("��������Ԫ��");
  }
  /**
   * ���浽Excel�ļ�����
   * @param FileObject File
   */
  public void Save2File(File FileObject){
    try {
      FileOutputStream fileOut = new FileOutputStream(FileObject);
      _wb.write(fileOut);
      fileOut.close();
    }
    catch (FileNotFoundException ex) {
      System.out.println(ex.getMessage());
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }

  }
  /**
   * ����Excel�ļ�
   */
  public void Save2File() {
    try {
      FileOutputStream fileOut = new FileOutputStream(_fileName);
      _wb.write(fileOut);
      fileOut.close();
    }
    catch (FileNotFoundException ex) {
      System.out.println(ex.getMessage());
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }

  }

  public static void main(String[] args) {
    PoiExcel excel = new PoiExcel("c:\\1.xls");
    DataBase db;
	try {
		db = new DataBase("jdbc:oracle:thin:@db.sucem.com:1521:ora9","cl","cl");
		String sql="Select * From Table(f_tj_xztxcl('20060213','20060213'))";
	    String sql1="Select * From Table(f_tj_xztxcl('20060210','20060213'))";
	    DataSet ds = db.retrieveDataSet(sql);
	    excel.ExportDataSet2Excel(0,ds,5,1);
	    ds = db.retrieveDataSet(sql1);
	    excel.ExportDataSet2Excel(1,ds,5,1);
	    excel.Save2File();
	} catch (SQLException e) {
		e.printStackTrace();
		// TODO ��Ӵ��������
	} catch (DataNotFoundException e) {
		e.printStackTrace();
		// TODO ��Ӵ��������
	}
    // excel.addRow();
  }
}

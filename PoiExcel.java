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
 * <p>标题: 将数据源导出到写Excel的类</p>
 *
 * <p>描述: 用作统计报表</p>
 *
 * <p>版权: Copyright (c) 2005</p>
 *
 * <p>公司: 上海二手车交易市场有限公司</p>
 *
 * @作者 孙汇洲
 * @版本 1.0.0
 */
public class PoiExcel {
  String _fileName = null; // Excel文件名
  InputStream _fs = null; // POI文件系统
  Workbook _wb = null; // 工作簿
  Row _row = null; // 表格行
  Cell _cell = null; // 单元格
  Sheet _sheet = null; // 表格(工作表)

  /**
   * 构造一个读写Excel的类
   * @param FileName String     Excel文件名
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
   * 构造一个读写Excel的类
   * @param FileObject File     Excel文件对象
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
   * 判断Excel文件是否已经存在
   * @param FileName String    Excel文件名
   * @return boolean           Excel文件是否存在的标志
   */
  private boolean ExcelFileExists(String FileName) {
    File file = new File(FileName);
    return file.exists();
  }

  /**
   * 根据表格名取得表格
   * @param SheetName String    表格名
   * @return HSSFSheet          表格对象
   */
  public Sheet getSheet(String SheetName) {
    return _wb.getSheet(SheetName);
  }

  /**
   * 根据表格的编号取得表格
   * @param SheetID int         表格编号
   * @return HSSFSheet          表格对象
   */
  private Sheet getSheet(int SheetID) {
    return _wb.getSheetAt(SheetID);
  }
  /**
   * 将DataSet填写到Excel表格的固定位置
   * @param SheetName String   表格的名字(Sheet名字)
   * @param ds DataSet         数据源
   * @param initRow int        表格的起始行号
   * @param initColumn int     表格起始的列号
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
   * 将DataSet填写到Excel中一个表格的固定位置
   * @param SheetIndex int     表格的位置
   * @param ds DataSet         数据源
   * @param initRow int        表格的起始行号
   * @param initColumn int     表格起始的列号
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
   * 设置表格中指定行列的内容
   * @param sheet HSSFSheet    表格对象
   * @param Content String     表格内的内容
   * @param row int            指定的行
   * @param col int            指定的列
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
   * 设置表格中指定行列的内容
   * @param SheetIndex int     表格序号
   * @param Content String     表格内的内容
   * @param row int            指定的行
   * @param col int            指定的列
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
   * 测试用
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
    _cell.setCellValue("第二个单元格");
    _cell = _row.createCell( (short) 2);
    //_cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    _cell.setCellValue("第三个单元格");
  }
  /**
   * 保存到Excel文件对象
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
   * 保存Excel文件
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
		// TODO 添加错误处理代码
	} catch (DataNotFoundException e) {
		e.printStackTrace();
		// TODO 添加错误处理代码
	}
    // excel.addRow();
  }
}

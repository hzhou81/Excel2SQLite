package main;

/**
 * 这个数据库访问访问模块使用JDBC3.0
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import com.sucem.common.SucemException;
import com.sucem.db.DBMatching;
import com.sucem.db.DataNotFoundException;
import com.sucem.db.DataSet;
import com.sucem.io.ImgFile;
import com.sucem.util.LogicModel;
import com.sucem.util.ViewerModel;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;

public class SqliteDB {

  // 操作类型
  public static final int Update = 0;
  public static final int Insert = 1;
  public static final int NewInsert = 2;
  private String _url = null;
  private String _user = null;
  private String _password = null;
  // private Properties _info = new Properties();
  private Connection _connection = null;
  private Statement _statement = null;
  private CallableStatement _callablestatement = null;
  // private PreparedStatement _pstmt = null;
  private ResultSet _resultset = null;

  public SqliteDB() {}

  /**
   * 构造一个DataBase
   *
   * @param url String 地址
   * @param user String 用户名
   * @param password String 密码
   * @throws SQLException
   */
  public SqliteDB(String url) throws SQLException {
    _url = url;
    createConnection();
  }

  /**
   * 创建一个数据库链接
   * @param url String 地址
   * @param user String 用户名
   * @param password String 密码
   * @throws SQLException SQL错误
   */
  public void createConnection(String url) throws
      SQLException {
    _url = url;
    createConnection();
  }

  /**
   * 创建一个数据库链接
   * @throws SQLException 连接过程中的错误
   */
  public void createConnection() throws SQLException {
    _connection = DriverManager.getConnection("jdbc:sqlite:"+_url);
    if (_connection == null) {
      throw new SQLException("无法连接到数据库,请检查您的网络本地连接!");
    }
  }

  /**
   * 关闭数据库连接
   */
  public void closeConnection() {
    try {
      if (_resultset != null) {
        _resultset.close();
      }
      if (_statement != null) {
        _statement.close();
      }
      if (_callablestatement != null) {
        _callablestatement.close();
      }
      if (_connection != null) {
        _connection.close();
      }
    }
    catch (SQLException ex) {
    }
    finally {
      _resultset = null;
      _statement = null;
      _callablestatement = null;
      _connection = null;
    }
  }

  /**
   * 执行一句SQL语句(这个方法不建议使用)
   * @param sql String SQL命令
   * @return ResultSet 返回的结果集
   * @throws SQLException SQL错误
   */
  public ResultSet executeQuery(String sql) throws SQLException {
    _statement = _connection.createStatement();
    _resultset = _statement.executeQuery(sql);
    return _resultset;
  }

  /**
   * 从数据库中取值
   *
   * @param sql String SQL命令
   * @return String 返回的值
   * @throws SucemException
   */
  public String retrieveValue(String sql) throws SQLException {
    String value = null;
    _statement = _connection.createStatement();
    try{
    	_resultset = _statement.executeQuery(sql);
    	try{
    		if (_resultset.next()) {
    			value = _resultset.getString(1);
    		}
    		// this.closeStatement();
    		return value;
    	}finally{
    		_resultset.close();
    	}
    }finally{
    	_statement.close();
    }
  }
  /**
   * 判断从数据库中是否可以取出值
   * @param sql String
   * @return boolean
   */
  public boolean canRetrieveValue(String sql) throws SQLException {
    // boolean canretrieve=false;
    _statement=_connection.createStatement();
    try{
    	_resultset=_statement.executeQuery(sql);
    	try{
    		// canretrieve=_resultset.next();
    		// _resultset.close();
    		return _resultset.next();
    	}finally{
    		_resultset.close();
    	}
    }finally{
    	_statement.close();
    }
  }
  
  /**
   * 判断一个SQL会不会产生记录
   *
   * @param Sql SQL命令
   * @return boolean 是否有记录
   * @todo: test it!!
   * @throws SQLException
   */
  public boolean retrieveRecord(String Sql) throws SQLException {
    _statement = _connection.createStatement();
    _resultset = _statement.executeQuery(Sql);
    boolean hasRecord = false;
    if (!_resultset.wasNull()) {
      hasRecord = true;
    }
    closeStatement();
    return hasRecord;
  }

  /**
   * 从数据库中按照列名取值
   *
   * @param sql String SQL命令
   * @param ColumnName String 数据库列名
   * @return String 执行SQL命令返回的值
   * @throws SucemException
   */
  public String retrieveValue(String sql, String ColumnName) throws SQLException {
    String value = null;
    _statement = _connection.createStatement();
    try{
    	_resultset = _statement.executeQuery(sql);
    	try{
    		if (_resultset.next()) {
    			value = _resultset.getString(ColumnName);
    		}
    		//closeStatement();
    		return value;
    	}finally{
    		_resultset.close();
    	}
    }finally{
    	_statement.close();
    }
  }

  /**
   * 返回数据结果集
   * @param sql String      SQL语句
   * @return DataSet        返回的结果集
   * @throws SucemException 执行过程中的错误
   */
  public DataSet retrieveDataSet(String sql) throws DataNotFoundException,SQLException {
    DataSet ds = null;
    _statement = _connection.createStatement();
    try{
    	_resultset = _statement.executeQuery(sql);
    	try{
    		ds = new DataSet(_resultset);
    		return ds;
    	}finally{
    		_resultset.close();
    	}
    }finally{
    	_statement.close();
    }
  }

  // 返回一个小数
  public float retrieveFloat(String sql, String ColumnName) throws
      SucemException {
    float value = 0;
    try {
      _statement = _connection.createStatement();
      _resultset = _statement.executeQuery(sql);
      if (_resultset.next()) {
        value = _resultset.getFloat(ColumnName);
      }
    }
    catch (SQLException ex) {
      throw new SucemException(ex);
    }
    finally {
      closeStatement();
    }
    return value;
  }

  /**
   * 关闭Statement
   */
  
  /*
  public void closeStatement() {
    try {
      if (_resultset != null) {
        _resultset.close();
        _resultset = null;
      }
      if (_statement != null) {
        _statement.close();
        _statement = null;
      }
    }
    catch (SQLException e) {
    }
  }
  */
  
  /**
	 * 关闭Statement
	 * 
	 */
	public void closeStatement() {
		if (_resultset != null) {
			try{
				_resultset.close();
			}catch(SQLException ex){}
		}
		if(_statement!=null){
			try{
				_statement.close();
			}catch(SQLException ex){}
		}
	}

  /**
	 * 执行一组Transaction
	 * 
	 * @param s
	 *            String[] SQL命令数组
	 * @throws SQLException
	 *             SQL错误
	 * @todo: 测试这段代码,这段批处理SQL DML的语句还没有测试运行过
	 */
  public void executeTransaction(String[] s) throws SQLException {
    OracleSavepoint savepoint = null;
    try {
      _connection.setAutoCommit(false);
      //savepoint = (OracleSavepoint) _connection.setSavepoint();
      _statement = _connection.createStatement();
      for (int i = 0; i < s.length; i++) {
        _statement.addBatch(s[i]);
      }
      _statement.executeBatch();
      Commit();
    }
    catch (SQLException ex) {
      //_connection.rollback(savepoint);
      throw new SQLException("SQL异常代码:" + ex.getErrorCode() + "\nSQL异常状态:" +
                             ex.getSQLState() + "\nSQL异常具体信息:" + ex.getMessage() +
                             "\n");
    }
    finally {
      closeStatement();
      // _connection.releaseSavepoint(savepoint);
    }
  }

  /**
   * 执行一组Transaction
   * @param v Vector SQL命令向量
   * @throws SQLException SQL错误
   * @todo: 测试这个模块
   */
  public void executeTransaction(Vector v) throws SQLException {
    OracleSavepoint savepoint = null;
    try {
      _connection.setAutoCommit(false);
      //savepoint = (OracleSavepoint) _connection.setSavepoint();
      _statement = _connection.createStatement();
      for (int i = 0; i < v.size(); i++) {
        // _statement.executeUpdate(v.elementAt(i).toString());
        // System.out.println(v.elementAt(i).toString());
        _statement.addBatch(v.elementAt(i).toString());
      }
      _statement.executeBatch();
      Commit();
    }
    catch (SQLException ex) {
      //_connection.rollback(savepoint);
      throw new SQLException("SQL异常代码:" + ex.getErrorCode() + "\nSQL异常状态:" +
                             ex.getSQLState() + "\nSQL异常具体信息:" + ex.getMessage() +
                             "\n");
    }
    finally {
      closeStatement();
      // _connection.releaseSavepoint(savepoint);
    }
  }

  /**
   * 执行一个Transaction
   * @param s String SQL命令
   * @throws SQLException SQL错误
   */
  public void executeTransaction(String s) throws SQLException {
	// OracleSavepoint savepoint = null;
    try {
      _connection.setAutoCommit(false);
      // savepoint=(OracleSavepoint) _connection.setSavepoint();
      _statement = _connection.createStatement();
      _statement.executeUpdate(s);
      Commit();
    }
    catch (SQLException ex) {
      // _connection.rollback(savepoint);  // 还原操作前的状态并抛出异常
      throw new SQLException("SQL异常代码:" + ex.getErrorCode() + "\nSQL异常状态:" +
                             ex.getSQLState() + "\nSQL异常具体信息:" + ex.getMessage() +
                             "\n");
    }
    finally {
      closeStatement();
      // _connection.releaseSavepoint(savepoint);
    }
  }

  /**
   * 不提交的执行一个Transaction
   * @param s String
   * @throws SQLException
   */
  public void executeTransactionWithoutCommit(String s) throws SQLException {
    try {
    	_connection.setAutoCommit(false);
    	_statement = _connection.createStatement();
    	_statement.executeUpdate(s);
    }catch (SQLException ex) {
    	throw new SQLException("SQL异常代码:" + ex.getErrorCode() + "\nSQL异常状态:" +ex.getSQLState() + "\nSQL异常具体信息:" + ex.getMessage() +"\n");
    }finally{
    	closeStatement();
    }
  }

  /**
   * 执行一个过程
   * @param sql String SQL语句
   * @return int 返回SQL运行的结果成功否
   * @throws SQLException SQL错误
   */
  public int executeProcedure(String sql) throws SQLException {
    try {
      _callablestatement = _connection.prepareCall(sql);
      _callablestatement.registerOutParameter(1, OracleTypes.NUMBER, 0);
      _callablestatement.executeUpdate();
      final int result = _callablestatement.getInt(1);
      return result;
    }
    catch (SQLException ex) {
      throw new SQLException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
    }
  }

  /**
   * 从结果集里得到列名集合
   * @param resultset ResultSet
   * @return String[]
   * @throws SQLException
   */
  public String[] getColumnName(ResultSet resultset) throws SQLException {
    if (resultset == null) {
      return null;
    }
    ResultSetMetaData resultmetadata = resultset.getMetaData();
    String[] s = new String[resultmetadata.getColumnCount()];
    for (int i = 0; i < resultmetadata.getColumnCount(); i++) {
      s[i] = resultmetadata.getColumnName(i + 1);
    }
    return s;
  }

  /**
   * 从结果集里得到列类型名集合
   * @param resultset ResultSet
   * @return String[]
   * @throws SQLException
   */
  public String[] getColumnType(ResultSet resultset) throws SQLException {
    if (resultset == null) {
      return null;
    }
    ResultSetMetaData resultmetadata = resultset.getMetaData();
    String[] s = new String[resultmetadata.getColumnCount()];
    for (int i = 0; i < resultmetadata.getColumnCount(); i++) {
      s[i] = resultmetadata.getColumnTypeName(i + 1);
    }
    return s;
  }

  /**
   * 取得指定列的长度集合
   * @param resultset ResultSet 结果集
   * @return int[]
   * @throws SQLException
   */
  public int[] getColumnLength(ResultSet resultset) throws SQLException {
    if (resultset == null) {
      return null;
    }
    ResultSetMetaData resultmetadata = resultset.getMetaData();
    int[] l = new int[resultmetadata.getColumnCount()];
    for (int i = 0; i < resultmetadata.getColumnCount(); i++) {
      l[i] = resultmetadata.getPrecision(i + 1);
    }
    return l;
  }

  //取得指定列的小数点后长度集合
  public int[] getColumnScale(ResultSet resultset) throws SQLException {
    if (resultset == null) {
      return null;
    }
    ResultSetMetaData resultmetadata = resultset.getMetaData();
    int[] l = new int[resultmetadata.getColumnCount()];
    for (int i = 0; i < resultmetadata.getColumnCount(); i++) {
      l[i] = resultmetadata.getScale(i + 1);
    }
    return l;
  }

  /**
   * 将图片写入数据的blob字段
   * @param insertSqlWithEmptyBlob String 插入带数值的，但blob响应字段是空的一句insert语句
   * @param KeyName String 标识要插入blob的那条记录的主键的名称
   * @param KeyValue String 标识要插入blob的那条记录的主键的值
   * @param BlobColumnName String blob字段的名称
   * @param imageFile ImgFile 要插入blob字段的图像文件
   * @throws SQLException SQL错误
   * @throws IOException IO错误
   * @throws Exception 错误
   */
  public void writeImageBlob(String insertSqlWithEmptyBlob, String KeyName,
                             String KeyValue, String BlobColumnName,
                             ImgFile imageFile) throws SQLException,
      IOException, Exception {
    File file = imageFile.GetFileHandle();
    InputStream fin = new FileInputStream(file);
    int flength = (int) file.length();
    byte buf[];
    buf = new byte[flength];
    int i = 0;
    int itotal = 0;
    // 将文件读入字节数组
    for (; itotal < flength; itotal = i + itotal) {
      i = fin.read(buf, itotal, flength - itotal);
    }
    fin.close();
    // 插入一条空的blob的完整记录
    String sql = insertSqlWithEmptyBlob;
    _connection.setAutoCommit(false);
    _statement = _connection.createStatement();
    _statement.execute(sql);
    _statement.close();
    // 取得文件位置
    _statement = _connection.createStatement();
    String TableName = GetTableNameFromInsertSQL(insertSqlWithEmptyBlob);
    sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    _resultset = _statement.executeQuery(sql);
    if (_resultset.next()) {
      BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      _resultset.close();
      _resultset = null;
      OutputStream out = blob.getBinaryOutputStream();
      out.write(buf);
      out.close();
      out = null;
    }
    _connection.commit();
    closeStatement();
  }


  public void writeImageBlob(String insertSqlWithEmptyBlob, String KeyName,
		  String KeyValue, String KeyName1,String KeyValue1,String BlobColumnName,
		  ImgFile imageFile) throws SQLException,
		  IOException, Exception {
	  File file = imageFile.GetFileHandle();
	  InputStream fin = new FileInputStream(file);
	  int flength = (int) file.length();
	  byte buf[];
	  buf = new byte[flength];
	  int i = 0;
	  int itotal = 0;
//	  将文件读入字节数组
	  for (; itotal < flength; itotal = i + itotal) {
		  i = fin.read(buf, itotal, flength - itotal);
	  }
	  fin.close();
//	  插入一条空的blob的完整记录
	  String sql = insertSqlWithEmptyBlob;
	  _connection.setAutoCommit(false);
	  _statement = _connection.createStatement();
	  _statement.execute(sql);
	  _statement.close();
//	  取得文件位置
	  _statement = _connection.createStatement();
	  String TableName = GetTableNameFromInsertSQL(insertSqlWithEmptyBlob);
	  sql = "select " + BlobColumnName + " from " + TableName + " where " +
	  KeyName + "='" + KeyValue + "' and "+KeyName1+"='"+KeyValue1+"'";
	  _resultset = _statement.executeQuery(sql);
	  if (_resultset.next()) {
		  BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
		  _resultset.close();
		  _resultset = null;
		  OutputStream out = blob.getBinaryOutputStream();
		  out.write(buf);
		  out.close();
		  out = null;
	  }
	  _connection.commit();
	  closeStatement();
  }

  /**
   * 将图片写入数据的blob字段
   *
   * @param insertSqlWihEmptyBlob String
   *   插入带数值的，但blob响应字段是空的一句insert语句
   * @param KeyName String 标识要插入blob的那条记录的主键的名称
   * @param KeyValue String 标识要插入blob的那条记录的主键的值
   * @param BlobColumnName String blob字段的名称
   * @param imageFile ImgFile 要插入blob字段的图像文件
   * @throws SQLException SQL错误
 * @throws FileNotFoundException,IOException,Exception 
   * @throws IOException IO错误
   * @throws Exception 错误
   */
  public void writeImageBlobWithoutCommit(String insertSqlWihEmptyBlob,
                                          String KeyName, String KeyValue,
                                          String BlobColumnName,
                                          ImgFile imageFile) throws
                                          SQLException, FileNotFoundException,IOException,Exception {
	File file = imageFile.GetFileHandle();
	// 2011年2月23日为了推广无线照片传输增加了入库前对照片进行统一【1024*768】尺寸保存
	AffineTransform transform = new AffineTransform();
	BufferedImage bis = ImageIO.read(file);
	int old_width=bis.getWidth();
	int old_height=bis.getHeight();
	int new_width=1024;
	int new_height=768;
	double sx=(double)new_width/old_width;
	double sy=(double)new_height/old_height;
	transform.setToScale(sx, sy);
	AffineTransformOp ato = new AffineTransformOp(transform, null);
	BufferedImage bid = new BufferedImage(new_width, new_height, BufferedImage.TYPE_3BYTE_BGR);
	ato.filter(bis, bid);
	/*
    InputStream fin = new FileInputStream(file);
    int flength = (int) file.length();
    byte buf[];
    buf = new byte[flength];
    int i = 0;
    int itotal = 0;
    for (; itotal < flength; itotal = i + itotal) {
    	i = fin.read(buf, itotal, flength - itotal);
    }
    fin.close();
    */
    // 插入一条空的blob的完整记录
    String sql = insertSqlWihEmptyBlob;
    _connection.setAutoCommit(false);
    _statement = _connection.createStatement();
    _statement.execute(sql);
    _statement.close();
    // 取得文件位置
    _statement = _connection.createStatement();
    String TableName = GetTableNameFromInsertSQL(insertSqlWihEmptyBlob);
    sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    _resultset = _statement.executeQuery(sql);
    // log.addLine("执行SQL命令得到ResultSet"+Test.getInstance().End()+" ms");
    if (_resultset.next()) {
      BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      _resultset.close();
      _resultset = null;
      // OutputStream out = blob.getBinaryOutputStream();
      OutputStream out=blob.setBinaryStream(0);
      ImageIO.write(bid, "jpeg", out);
      out.flush();
      out.close();
      // out.write(buf);
      // out.close();
      // out = null;
    }
    if (_resultset != null) {
      _resultset.close();
    }
    if (_statement != null) {
      _statement.close();
    }
  }

  public void TMPwriteImageBlobWithoutCommit(String insertSqlWithEmptyBlob,
                                             String KeyName, String KeyValue,
                                             String BlobColumnName,
                                             BufferedImage image) throws
      SQLException,
      Exception { // 20050922新增加的一个测试模块,用于把Image直接读进来去添BLOB
    // 用于取代原来读入ImgFile写入BLOB的那个方法,重新写过了,需要测试一下
    String sql = insertSqlWithEmptyBlob;
    _connection.setAutoCommit(false);
    _statement = _connection.createStatement();
    _statement.execute(sql);
    _statement.close();
    // 取得文件位置
    _statement = _connection.createStatement();
    String TableName = GetTableNameFromInsertSQL(insertSqlWithEmptyBlob);
    sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    _resultset = _statement.executeQuery(sql);
    if (_resultset.next()) {
      BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      _resultset.close();
      _resultset = null;
      OutputStream out = blob.getBinaryOutputStream();
      ImageIO.write(image, "jpeg", out);
      // out.write(b);
      out.close();
      out = null;
    }
    if (_resultset != null) {
      _resultset.close();
    }
    if (_statement != null) {
      _statement.close();
    }
  }

  /**
   * 从数据库中读取照片
   * @param TMPFolder String 存放读取出来的blob数据的临时文件夹
   * @param TableName String 表的名称
   * @param KeyName String 用来标识要取blob的那条记录的主键的名称
   * @param KeyValue String 用来标识要取blob的那条记录的主键的值
   * @param BlobColumnName String blob字段的列名
   * @return ImgFile 图片
   * @throws SQLException SQL错误
 * @throws IOException 
 * @throws IOException IO错误
   */
  public ImgFile retrieveImageBlob(String TMPFolder, String TableName,
                                   String KeyName, String KeyValue,
                                   String BlobColumnName) throws SQLException, IOException{
	// System.out.println("get blob from : "+System.currentTimeMillis());
    this._connection.setAutoCommit(false);
    this._statement = _connection.createStatement();
    String sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    this._resultset = this._statement.executeQuery(sql);
    File binaryFile=null;
    if (this._resultset.next()) {
      Blob blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      InputStream inStream = blob.getBinaryStream();
      if (!TMPFolder.endsWith("\\")) {
        TMPFolder += "\\";
      }
      // System.out.println("new file : "+System.currentTimeMillis());
      binaryFile = new File(TMPFolder + KeyValue + ".jpg");
      FileOutputStream fileOutputStream = new FileOutputStream(binaryFile);
      //System.out.println("begin read blob : "+System.currentTimeMillis());
      /*
      int by = inStream.read();
      while (by != -1) {
        fileOutputStream.write(by);
        by = inStream.read();
      }
      */
      byte[] bs=new byte[((oracle.sql.BLOB)blob).getBufferSize()];
      //System.out.println(((oracle.sql.BLOB)blob).getBufferSize());
      //byte[] bs=new byte[10240];
      int by=inStream.read(bs);
      while(by!=-1){
    	  fileOutputStream.write(bs);
    	  by=inStream.read(bs);
      }  
     // System.out.println("flush file : "+System.currentTimeMillis());
      fileOutputStream.flush();
      inStream.close();
      fileOutputStream.close();
    }
    _resultset.close();
    _statement.close();
    //System.out.println("complete read blob : "+System.currentTimeMillis());
    if(binaryFile==null || !binaryFile.exists()){
    	return null;
    }else{
    	//System.out.println("new image file : "+System.currentTimeMillis());
    	return new ImgFile(TMPFolder + KeyValue + ".jpg");
    }
  }

  public ImgFile retrieveImageBlob(String TMPFolder, String TableName,
                                   String KeyName, String KeyValue,String KeyName1,String KeyValue1,
                                   String BlobColumnName) throws SQLException,
      IOException,FileNotFoundException {
    this._connection.setAutoCommit(false);
    this._statement = _connection.createStatement();
    String sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "' and "+KeyName1+"='"+KeyValue1+"'";
    this._resultset = this._statement.executeQuery(sql);
    if (this._resultset.next()) {
      Blob blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      InputStream inStream = blob.getBinaryStream();
      if (!TMPFolder.endsWith("\\")) {
        TMPFolder += "\\";
      }
      File binaryFile = new File(TMPFolder + KeyValue + ".jpg");
      FileOutputStream fileOutputStream = new FileOutputStream(binaryFile);
      /*
      byte[] bs=new byte[10240];
      int by=inStream.read(bs);
      while(by!=-1){
    	  fileOutputStream.write(bs);
    	  by=inStream.read(bs);
      }
      */
      
      int by = inStream.read();
      while (by != -1) {
        fileOutputStream.write(by);
        by = inStream.read();
      }
      fileOutputStream.flush();
      inStream.close();
      fileOutputStream.close();
    }
    _resultset.close();
    _statement.close();
    return new ImgFile(TMPFolder + KeyValue + ".jpg");
  }
  
  public Image retrieveImageBlob(String TableName,
                                 String KeyName, String KeyValue,
                                 String BlobColumnName) throws SQLException,
      IOException, Exception {
    Image im = null;
    this._connection.setAutoCommit(false);
    this._statement = _connection.createStatement();
    String sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    this._resultset = this._statement.executeQuery(sql);
    if (_resultset.next()) {
      BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      InputStream inStream = blob.getBinaryStream();
      JPEGImageDecoder decoderFile = JPEGCodec.createJPEGDecoder(inStream);
      im = (Image) decoderFile.decodeAsBufferedImage();
      inStream.close();
    }
    _resultset.close();
    _statement.close();
    return im;
  }

  public BufferedImage retrieveBufferedImageBlob(String TableName,
                                                 String KeyName,
                                                 String KeyValue,
                                                 String BlobColumnName) throws
      SQLException, IOException, Exception {
    BufferedImage im = null;
    this._connection.setAutoCommit(false);
    this._statement = _connection.createStatement();
    String sql = "select " + BlobColumnName + " from " + TableName + " where " +
        KeyName + "='" + KeyValue + "'";
    this._resultset = this._statement.executeQuery(sql);
    if (_resultset.next()) {
      BLOB blob = ( (OracleResultSet) _resultset).getBLOB(BlobColumnName);
      InputStream inStream = blob.getBinaryStream();
      JPEGImageDecoder decoderFile = JPEGCodec.createJPEGDecoder(inStream);
      im = decoderFile.decodeAsBufferedImage();
      inStream.close();
    }
    _resultset.close();
    _statement.close();
    return im;
  }
  /**
   * 修改BLOB对象
   * @param TableName
   * @param KeyName
   * @param KeyValue
   * @param BlobColumnName
   * @param filein
 * @throws SQLException 
 * @throws IOException 
   */
  public void modifyBlob(String TableName,String KeyName,String KeyValue,String BlobColumnName,File filein) throws Exception{
	  this._connection.setAutoCommit(false);
	  this._statement=this._connection.createStatement();
	  String sql="select "+BlobColumnName+" from "+TableName+" where "+KeyName+"='"+KeyValue+"' for update";
	  this._resultset=this._statement.executeQuery(sql);
	  try{
		  if(_resultset.next()){
			  BLOB blob=((OracleResultSet)_resultset).getBLOB(BlobColumnName);
			  BufferedOutputStream out=new BufferedOutputStream(blob.getBinaryOutputStream());
			  BufferedInputStream in=new BufferedInputStream(new FileInputStream(filein));
			  int c;
			  while((c=in.read())!=-1){
				  out.write(c);
			  }
			  in.close();
			  out.close();
		  }
		  _connection.commit();
	  }catch(Exception ex){
		  this._connection.rollback();
		  throw ex;
	  }
  }
  
  private String GetTableNameFromInsertSQL(String insertSQL) throws Exception {
    int j = insertSQL.indexOf("into");
    if (j == -1) {
      throw new Exception("不是合法的insert语句!");
    }
    else {
      int k = insertSQL.indexOf("values");
      if (k == -1) {
        throw new Exception("不是合法的insert语句!");
      }
      else {
        //下面肯定是一句合法的insert语句了，加以处理之
        String tmp = insertSQL.substring(j + 4, k);
        int l = tmp.indexOf("(");
        if (l == -1) {
          return tmp;
        }
        else {
          return tmp.substring(0, l);
        }
      }
    }
  }

  /**
   * 返回model,只适合单行查询结果的应用
   * @param sql String
   * @return LogicModel
   * @throws SQLException
   * @throws Exception
   */
  public LogicModel exeQuery(String sql) throws SQLException,
			DataNotFoundException {
		ResultSetMetaData resultmetadata = null;
		_statement = _connection.createStatement();
		try {
			_resultset = _statement.executeQuery(sql);
			try {
				if (_resultset.next()) {
					LogicModel logicModel = new LogicModel();
					String colName = "";
					String colVal = "";
					resultmetadata = _resultset.getMetaData();
					// 得到返回数据集的列数
					int numCols = resultmetadata.getColumnCount();

					// 把返回的列名放入columns矢量中
					// Vector columns = new Vector();
					for (int i = 1; i <= numCols; i++) {
						colName = resultmetadata.getColumnName(i);
						colVal = _resultset.getString(i);
						logicModel.register(colName, colVal);
					}
					// _resultset.close();
					// closeStatement();
					return logicModel;
				} else {
					// 返回的记录集为空
					// _resultset.close();
					// closeStatement();
					throw new DataNotFoundException();
				}
			}finally {
				_resultset.close();
			}
		} finally {
			closeStatement();
		}
	}

  public void exeTrans(int OperateType, String TableName, String KeyNames,
                       ViewerModel ViewModel, String WhereClause) throws
      SQLException, SucemException {
    String sql = "";
    DBMatching dbmatch = new DBMatching(KeyNames, ViewModel);
    if (OperateType == 0) { // update
      sql = "update " + TableName + " set " + dbmatch.ReturnKeyNamesAndValues() +
          " where " + WhereClause;
    }
    if (OperateType == 1) { // insert
      sql = "insert into " + TableName + "(" + dbmatch.ReturnKeyNames() +
          ") values(" + dbmatch.ReturnKeyValues() + ") ";
    }
    if (OperateType == 2) { // new insert written for zsz
      sql = "insert into " + TableName + "(" + dbmatch.ReturnFilterKeyNames() +
          ") values(" + dbmatch.ReturnKeyValues() + ") ";
    }
    executeTransaction(sql);
  }

  public void exeTransWithoutCommit(int Operateype, String TableName,
                                    String KeyNames, ViewerModel ViewModel,
                                    String WhereClause) throws SQLException,
      SucemException {
    String sql = "";
    DBMatching dbmatch = new DBMatching(KeyNames, ViewModel);
    if (Operateype == 0) { // update
      sql = "update " + TableName + " set " + dbmatch.ReturnKeyNamesAndValues() +
          " where " + WhereClause;
    }
    if (Operateype == 1) { // insert
      sql = "insert into " + TableName + "(" + dbmatch.ReturnKeyNames() +
          ") values(" + dbmatch.ReturnKeyValues() + ") ";
    }
    if (Operateype == 2) { // new insert written for zsz
      sql = "insert into " + TableName + "(" + dbmatch.ReturnFilterKeyNames() +
          ") values(" + dbmatch.ReturnFilterKeyValue() + ") ";
    }
    executeTransactionWithoutCommit(sql);
  }
  
  
  /*
  public String getsql(int Operateype, String TableName, String KeyNames,
			ViewerModel ViewModel, String WhereClause) throws SQLException,
			SucemException {
		String sql = "";
		DBMatching dbmatch = new DBMatching(KeyNames, ViewModel);
		if (Operateype == 0) { // update
			sql = "update " + TableName + " set "
					+ dbmatch.ReturnKeyNamesAndValues() + " where "
					+ WhereClause;
		}
		if (Operateype == 1) { // insert
			sql = "insert into " + TableName + "(" + dbmatch.ReturnKeyNames()
					+ ") values(" + dbmatch.ReturnKeyValues() + ") ";
		}
		if (Operateype == 2) { // new insert written for zsz
			sql = "insert into " + TableName + "("
					+ dbmatch.ReturnFilterKeyNames() + ") values("
					+ dbmatch.ReturnFilterKeyValue() + ") ";
		}
		// executeTransactionWithoutCommit(sql);
		return sql;
	}
  */
  public void Commit() throws SQLException {
    _connection.commit();
  }

  public void RollBack() {
    try {
      _connection.rollback();
    }
    catch (SQLException ex) {

    }
  }

  /**
   * 析构函数
   *
   * @throws Throwable
   */
  protected void finalize() throws Throwable {
    closeConnection();
    super.finalize();
  }

  public Connection getConnection() {
    return _connection;
  }

  /*catch (SQLException ex)  {
              Log.log("\nERROR:----- SQLException -----\n");
              Log.log("Message:   " + ex.getMessage());
              Log.log("SQLState:  " + ex.getSQLState());
              Log.log("ErrorCode: " + ex.getErrorCode());
          }*/
  /*
     //检验是否取得数据库连接
        private boolean hasConnection() {
            return _connection != null;
        }
     //检验是否从连接中得到Statement对象
        private boolean hasStatement() {
            return _statement !=null;
        }
        private boolean hasCallableStatement() {
            return _callablestatement != null;
        }
        private void createStatement() throws SQLException {
            _statement = _connection.createStatement();
        }
        private void closeStatement() throws SQLException {
            if(_statement != null)
                _statement.close();
        }
        private void createStatement(int resultSetType,
   int resultSetConcurrency) throws SQLException {
   _statement = _connection.createStatement(resultSetType,resultSetConcurrency);
     }*/
  public static void main(String[] args) {
	  /*
		  try {
			DataBase db=new DataBase("jdbc:oracle:thin:@10.0.0.203:1521:ora9","cy","cy2005");
			ImgFile f=new ImgFile("D:\\临时文件\\查验系统\\临时目录\\01.jpg");
			db.writeImageBlobWithoutCommit("insert into zp values('1103311234','02','CCS203',empty_blob(),'20110224120000')", "cyh", "1103311234", "ZP", f);
			db.Commit();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
  }
  /**
   * 执行一个SQL语句,将结果返回用于显示在jComboBox中
   * @param sql    要执行的SQL语句
   * @return        返回的结果集用于显示在jComboBox中
   */
  public ComboBoxModel retrieveComboBoxModel(String sql){
	 //  ComboBoxModel cbm=
	  DefaultComboBoxModel bm=new DefaultComboBoxModel();
	  try { 
		DataSet ds=retrieveDataSet(sql);
		bm.addElement("");
		for(int i=0;i<ds.getRowCount();i++){
			bm.addElement(ds.getValueAt(i, 0));
		}
	} catch (DataNotFoundException e) {
		
	} catch (SQLException e) {
		
	}
	return bm;
  }
}

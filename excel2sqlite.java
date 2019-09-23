package main;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class excel2sqlite {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		String folderName=args[0];
		String dbName=args[1];
		String tableName=args[2];
		String sheetName=args[3];
		String columns=args[4];
		try {
			System.out.println("Excel2003文件导入SQLite程序 孙汇洲 版权所有");
			System.out.println("使用注意事项:");
			System.out.println("1.目录中所有xlsx文件要提前统一改成xls文件后缀名");
			System.out.println("2.对输出后的表内的空值和非规范字段进行修改");
			SqliteDB db=new SqliteDB(dbName);
			boolean tag=db.canRetrieveValue("select count(*) from "+tableName);
			if(!tag) System.out.println("数据库测试失败");
			//遍历目录依次读取excel文件
			File eachDir=new File(folderName);
			if(!eachDir.exists()){
				System.out.println(eachDir+" 不存在！");
				return;
			}
			if(!eachDir.isDirectory()){
				System.out.println(eachDir+"不是一个目录！");
				return;
			}
			File[] files=eachDir.listFiles();
			for(File excelFile:files){
				if(excelFile.isDirectory()) continue;
				String excelName=excelFile.getAbsolutePath();
				System.out.print("导入"+excelName+"......");
				PoiExcel excel=null;
				try{
					excel=new PoiExcel(excelName);
				}catch(OfficeXmlFileException e){
					e.printStackTrace();
					continue;
				}
				String[] columnArr=columns.split(",");
				int[] columnIdx=new int[columnArr.length];
				for(int i=0;i<columnIdx.length;i++) columnIdx[i]=-1;
				Sheet sheet=excel.getSheet(sheetName);
				if(sheet==null){
					System.out.println("没有表'"+sheetName+"'");
					break;
				}
				Row row=sheet.getRow(0);
				if(row==null) System.out.println("导入"+excelName+"的时候,表'"+sheetName+"'中没有数据");
				int columnNum=row.getPhysicalNumberOfCells();
				for(int i=0;i<columnArr.length;i++){
					for(int j=0;j<columnNum;j++){
						Cell cell=row.getCell((short)(row.getFirstCellNum()+j));
						if(cell==null) continue;
						if(cell.getStringCellValue().trim().indexOf(columnArr[i])!=-1){
							columnIdx[i]=j;
							break;
						}
					}
					if(columnIdx[i]==-1){
						System.out.println("没有找到'"+columnArr[i]+"'");
						continue;
					}
				}
				
				int rowNum=sheet.getPhysicalNumberOfRows();//excel的第一行是标题栏，ommit
				Vector<String> sqls=new Vector<String>();
				String sql="";
				int empty=0;
				for(int k=0;k<rowNum;k++){
					for(int l=0;l<columnIdx.length;l++){
						Row eachRow=sheet.getRow(k+1);
						if(eachRow==null) continue;
						Cell cell=eachRow.getCell((short)columnIdx[l]);
						if(cell==null){
							sql+="'',";
							empty++;
						}else if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
							sql+="'"+cell.getStringCellValue()+"',";
						}else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
							sql+=cell.getNumericCellValue()+",";
						}else{
							sql+="'',";
							empty++;
						}
					}
					if("".equals(sql)) break;
					if(empty>=columnIdx.length){
						sql="";
						break;
					}
					sql="insert into "+tableName+"("+columns+") values("+sql;
					sql=sql.substring(0, sql.length()-1)+")";
					//System.out.println(sql);
					sqls.add(sql);
					sql="";
				}
				db.executeTransaction(sqls);
				System.out.println("成功!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

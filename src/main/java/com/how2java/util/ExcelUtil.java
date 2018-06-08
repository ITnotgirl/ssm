package com.how2java.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.alibaba.fastjson.JSON;
/**
 * @author zhou
 */
public class ExcelUtil {
    /**
     * 获取表名
     * @param excelPath
     * @return
     */
    public static String getTableName(String excelPath) {
        File file = new File(excelPath);
        String fileName = file.getName();
        /**获取表字段索引lastIndexOf[起始索引,结束索引)*/
        String wbName=fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println(wbName);
        return wbName;
    }

    /**
     * 取表头
     * @param excelPath
     * @return
     */
    public static List<String> getHeaderList(String excelPath) {
        try {
            /**
             * 读取表路径,获取文件数据输入流信息
             * 建立数据通道
             */
            FileInputStream fis = new FileInputStream(excelPath);
            /**
             * 打开excel文件工作薄
             * 获取sheet子表索引
             * 获取行头索引
             */
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow headRow = sheet.getRow(0);
            // 获取表头
            List<String> headerList = new ArrayList<String>();
            int index = 0;
            while (true) {
                HSSFCell cell = headRow.getCell(index);
                if (cell == null) {
                    break;
                }
                String head = cell.getStringCellValue();
                if (StringUtils.isNotBlank(head)) {
                    headerList.add(head);
                    index++;
                } else {
                    break;
                }
            }

            return headerList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 取字符串格式的表头
     * @param excelPath
     * @return
     */
    public static String getHeaderString(String excelPath) {
        List<String> headerList = getHeaderList(excelPath);
        StringBuffer headerString = new StringBuffer();

        for(String header : headerList) {
            if(headerString.length() > 0) {
                headerString.append(",");
            }
            headerString.append(header);
        }
        return headerString.toString();
    }

    /**
     * 取拼音格式表对
     * @param excelPath
     * @return
     */
    public static List<String> getEnglishHeaderList(String excelPath) {
        List<String> headerList = getHeaderList(excelPath);
        List<String> englishHeaderList = new ArrayList<String>();
        for(int i = 0; i < headerList.size(); i++) {
            String englistHeader = PingYingUtil.getPinYinHeadChar(headerList.get(i), true).toUpperCase();
            englishHeaderList.add(englistHeader);
        }

        // 校验是否存在重复表头
        List<String> checkList = new ArrayList<String>();
        StringBuilder stringBuilder=new StringBuilder();
        for(String head : englishHeaderList) {
            if(checkList.contains(head)) {
                System.out.println(String.format("发现重复字段：%s", head));
                // 在重复字段后面加_，避免建表时重复
                while(true) {
                    stringBuilder=stringBuilder.append(head+"_");
                    head = stringBuilder.toString();
                    if(!checkList.contains(head)) {
                        checkList.add(head);
                        System.out.println(String.format("将重复字段修正为：%s", head));
                        break;
                    }
                }
            } else {
                checkList.add(head);
            }
        }


        return englishHeaderList;
    }

    /**
     * 取表格内容
     * @param excelPath
     * @return
     */
    public static List<String> getContent(String excelPath) {
        try {
            FileInputStream fis = new FileInputStream(excelPath);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow headRow = sheet.getRow(0);
            // 获取表头
            int headerLength = 0;
            while (true) {
                HSSFCell cell = headRow.getCell(headerLength);
                if (cell == null) {
                    break;
                }
                String head = cell.getStringCellValue();
                if (StringUtils.isNotBlank(head)) {
                    headerLength++;
                } else {
                    break;
                }
            }

            // 取表格内容
            List<String> contentList = new ArrayList<String>();
            int j = 1;
            while (true) {
                HSSFRow row = sheet.getRow(j);
                if (row == null) {
                    break;
                }
                StringBuffer sb = new StringBuffer();
                for (int m = 0; m < headerLength; m++) {
                    HSSFCell cell = row.getCell(m);
                    String cellValue = "";
                    if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        String valueTmp = cell.getNumericCellValue() + "";
                        cellValue = valueTmp.substring(0, valueTmp.indexOf("."));
                    } else {
                        cellValue = cell.getStringCellValue();
                    }
                    cellValue = cellValue.replace(",", "，");
                    if(cellValue.indexOf("E+") != -1) {
                        System.out.println(String.format("请注意数据格式，内容：%s，位置（%s，%s）", cellValue, cell.getColumnIndex(), cell.getRowIndex()));
                    }
                    cellValue = "'" + cellValue + "'";

                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(cellValue);
                }
                contentList.add(sb.toString());
                j++;
            }

            return contentList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据excel表头生成建表语句
     * @param tableName
     * @param excelPath
     * @return
     */
    public static String getCreateTableSql(String dbName, String excelPath, boolean isChineseHeader) {
        String tableName = getTableName(excelPath);
        List<String> headerList = null;
        if(isChineseHeader) {
            headerList = getEnglishHeaderList(excelPath);
        } else {
            headerList = getHeaderList(excelPath);
        }

        StringBuffer sql = new StringBuffer();
        sql.append("create table ").append(tableName).append(" (");
        int i = 0;
        for(String head : headerList) {
            if(i > 0) {
                sql.append(",");
            }
            sql.append(head).append(" VARCHAR2(255)");
            i++;
        }
        sql.append(" ) tablespace RUNJCKDATA pctfree 10 initrans 1 maxtrans 255 storage ( initial 80K minextents 1 maxextents unlimited );\n");

        // 如果表头是中文，增加注释
        if(isChineseHeader) {
            List<String> chineseHeaderList = getHeaderList(excelPath);

            if(headerList.size() == chineseHeaderList.size()) {
                StringBuffer commentSql = new StringBuffer();
                for(int j = 0; j < headerList.size(); j++) {
                    commentSql.append("COMMENT ON COLUMN \"")
                            .append(dbName)
                            .append("\".\"")
                            .append(tableName)
                            .append("\".\"")
                            .append(headerList.get(j))
                            .append("\" IS '")
                            .append(chineseHeaderList.get(j))
                            .append("';\n");
                }
                sql.append(commentSql);
            } else {
                System.out.println("非法格式，无法生成注释...");
                System.out.println(JSON.toJSONString(headerList));
                System.out.println(JSON.toJSONString(chineseHeaderList));
            }
        }

        return sql.toString();
    }



    public static void main(String[] args) {
        String path = "E:\\data\\KD.xls";
        String dbName = "RUNJCK";
        System.out.println(getCreateTableSql(dbName, path, true));
        //getContent(path);
        getTableName(path);
    }
}

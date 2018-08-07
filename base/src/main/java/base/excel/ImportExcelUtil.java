package base.excel;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import base.utils.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ImportExcelUtil.class);
    private static DecimalFormat phoneFmt = new DecimalFormat("#");
    private static ImportExcelUtil self = new ImportExcelUtil();

    private ImportExcelUtil() {
    }

    protected Workbook getWorkBook(String filePath) throws ExcelException {
        String ext = FileUtils.getExt(filePath);
        Workbook book = null;
        FileInputStream in = null;

        try {
            in = new FileInputStream(filePath);
            if (".xls".equals(ext)) {
                book = new HSSFWorkbook(in);
            } else {
                if (!".xlsx".equals(ext)) {
                    logger.error("导入文件格式不正确,只能是xls或者xlsx");
                    throw new ExcelException("导入文件格式不正确,只能是xls或者xlsx");
                }

                book = new XSSFWorkbook(in);
            }
        } catch (Exception var13) {
            var13.printStackTrace();
            if (var13 instanceof ExcelException) {
                throw (ExcelException)var13;
            }

            throw new ExcelException("导入Excel失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var12) {
                    logger.warn("导入流关闭失败...");
                }
            }

        }

        return (Workbook)book;
    }

    public static <T> List<T> importExcel(Class<T> clazz, String filePath, Map<String, String> matchData, Map<String, String> data, String[] rules, List<ExcelErrorVo> excelErrorVos) throws ExcelException {
        return importExcel(clazz, 1, (String)null, filePath, matchData, data, rules, excelErrorVos);
    }

    public static <T> List<T> importExcel(Class<T> clazz, int startRow, String sheetName, String filePath, Map<String, String> dataFilter, Map<String, String> header, String[] rules, List<ExcelErrorVo> excelErrorVos) throws ExcelException {
        if (header == null) {
            throw new ExcelException("Excel中的中文列头和类的英文属性的对应关系Map为空");
        } else {
            Pattern[] patterns = compileRules(rules);
            Workbook book = self.getWorkBook(filePath);
            CellStyle defaultStyle = self.createDefaultCellStyle(book.createCellStyle());
            List<T> list = new ArrayList();
            Sheet sheet = self.getSheet(book, sheetName);
            int lastCellNum ;
            if (sheet == null) {
                throw new ExcelException("Excel中不存在名为：[" + sheetName + "]的sheet，导入excel文件失败");
            } else {
                int rows = sheet.getLastRowNum();
                if (rows <= 0) {
                    throw new ExcelException("Excel无数据，请将数据放到第一个sheet，并删掉sheet名字。");
                } else {
                    Set<String> headerKeys = header.keySet();
                    Map<Integer, String> indexDatas = new HashMap();
                    Map<Integer, String> ruleDatas = new HashMap();
                    if (startRow < 1) {
                        startRow = 1;
                    }

                    if (sheet.getRow(startRow - 1) == null) {
                        throw new ExcelException(String.format("Excel文件，第[%d]行数据为空", startRow));
                    } else {
                        lastCellNum = sheet.getRow(startRow - 1).getLastCellNum();
                        Iterator var18 = headerKeys.iterator();

                        while(var18.hasNext()) {
                            String key = (String)var18.next();
                            Integer index = self.getColumnIndex(key);
                            if (index.intValue() > lastCellNum) {
                                logger.error("取值列的位置坐标，超出了excel文件的cell坐标上限");
                                throw new ExcelException(String.format("Excel列映射关系配置异常!excel 与 映射文件中的[%s]列头坐标[%d]，超过了excel文件的row中lastCell的坐标[%d]", key, index, Integer.valueOf(lastCellNum)));
                            }

                            String collateralData = (String)header.get(key);
                            if (collateralData.indexOf("-&&-") > 1) {
                                String[] collateralDatas = collateralData.split("-&&-");
                                indexDatas.put(index, collateralDatas[0]);
                                ruleDatas.put(index, collateralDatas[1]);
                            } else {
                                indexDatas.put(index, collateralData);
                            }
                        }

                        Iterator var59;
                        for(int i = startRow - 1; i <= rows; ++i) {
                            Row row = sheet.getRow(i);
                            if (!self.isNullRow(row, Integer.valueOf(lastCellNum))) {
                                boolean filterMatched = self.filterMatch(dataFilter, row, lastCellNum);
                                if (filterMatched) {
                                    try {
                                        T entity = clazz.newInstance();
                                        var59 = indexDatas.entrySet().iterator();

                                        while(var59.hasNext()) {
                                            Entry<Integer, String> entry = (Entry)var59.next();
                                            Integer index = (Integer)entry.getKey();
                                            String voField = (String)entry.getValue();
                                            Cell cell = row.getCell(index.intValue());
                                            String cellVal;
                                            if (cell != null) {
                                                cell.setCellStyle(defaultStyle);
                                                cellVal = self.getCellVal(cell);
                                            } else {
                                                cellVal = "";
                                            }

                                            boolean matched = true;
                                            String fieldRule = (String)ruleDatas.get(index);
                                            if (fieldRule != null) {
                                                matched = cellVal.matches(fieldRule);
                                            } else if (patterns != null) {
                                                Pattern[] var30 = patterns;
                                                int var31 = patterns.length;

                                                for(int var32 = 0; var32 < var31; ++var32) {
                                                    Pattern pattern = var30[var32];
                                                    if (pattern.matcher(cellVal).find()) {
                                                        matched = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (matched) {
                                                self.setFieldValueByName(voField, cellVal, entity);
                                            } else {
                                                if (excelErrorVos == null) {
                                                    excelErrorVos = new ArrayList();
                                                }

                                                ((List)excelErrorVos).add(new ExcelErrorVo(i + 1, self.excelColIndexToStr(index.intValue()), cellVal));
                                            }
                                        }

                                        list.add(entity);
                                    } catch (InstantiationException var47) {
                                        throw new ExcelException("初始化映射实体异常");
                                    } catch (IllegalAccessException var48) {
                                        throw new ExcelException("setFieldValueByName 设置实体类属性时出现异常");
                                    } catch (ParseException var49) {
                                        throw new ExcelException("解析日期出现异常");
                                    }
                                }
                            }
                        }

                        if (excelErrorVos != null && !((List)excelErrorVos).isEmpty()) {
                            CellStyle errorCellStyle = self.createErrorCellStyle(book.createCellStyle());
                            CellStyle warnCellStyle = self.createWarnCellStyle(book.createCellStyle());
                            Map<Integer, Boolean> rowStatus = new HashMap();
                            var59 = ((List)excelErrorVos).iterator();

                            while(true) {
                                while(var59.hasNext()) {
                                    ExcelErrorVo vo = (ExcelErrorVo)var59.next();
                                    Cell cell;
                                    Row row;
                                    if (rowStatus.get(vo.getRowX() - 1) != null) {
                                        row = sheet.getRow(vo.getRowX() - 1);
                                        cell = row.getCell(self.getColumnIndex(vo.getCellY()));
                                        cell.setCellStyle(errorCellStyle);
                                    } else {
                                        row = sheet.getRow(vo.getRowX() - 1);

                                        for(int i = 0; i < lastCellNum; ++i) {
                                            if (i == self.getColumnIndex(vo.getCellY())) {
                                                cell = row.getCell(i);
                                                if (cell == null) {
                                                    cell = row.createCell(i);
                                                }

                                                cell.setCellStyle(errorCellStyle);
                                            } else {
                                                cell = row.getCell(i);
                                                if (cell == null) {
                                                    cell = row.createCell(i);
                                                }

                                                cell.setCellStyle(warnCellStyle);
                                            }
                                        }

                                        rowStatus.put(vo.getRowX() - 1, true);
                                    }
                                }

                                FileOutputStream out = null;

                                try {
                                    try {
                                        out = new FileOutputStream(filePath);
                                        book.write(out);
                                    } catch (IOException var45) {
                                        var45.printStackTrace();
                                    }
                                    break;
                                } finally {
                                    try {
                                        if (out != null) {
                                            out.close();
                                        }
                                    } catch (IOException var44) {
                                        var44.printStackTrace();
                                    }

                                }
                            }
                        }

                        return list;
                    }
                }
            }
        }
    }

    private CellStyle createDefaultCellStyle(CellStyle defaultStyle) {
        defaultStyle.setFillForegroundColor((short)0);
        defaultStyle.setBorderBottom((short)1);
        defaultStyle.setBorderLeft((short)1);
        defaultStyle.setBorderTop((short)1);
        defaultStyle.setBorderRight((short)1);
        return defaultStyle;
    }

    private CellStyle createErrorCellStyle(CellStyle defaultStyle) {
        defaultStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        defaultStyle.setFillPattern((short)1);
        defaultStyle.setBorderBottom((short)1);
        defaultStyle.setBorderLeft((short)1);
        defaultStyle.setBorderTop((short)1);
        defaultStyle.setBorderRight((short)1);
        return defaultStyle;
    }

    private CellStyle createWarnCellStyle(CellStyle defaultStyle) {
        defaultStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        defaultStyle.setFillPattern((short)1);
        defaultStyle.setBorderBottom((short)1);
        defaultStyle.setBorderLeft((short)1);
        defaultStyle.setBorderTop((short)1);
        defaultStyle.setBorderRight((short)1);
        return defaultStyle;
    }

    private boolean filterMatch(Map<String, String> dataFilter, Row row, int colLimit) {
        boolean matched = true;
        if (dataFilter != null) {
            Set<String> filterKeys = dataFilter.keySet();
            Iterator var6 = filterKeys.iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                String matchVal = (String)dataFilter.get(key);
                int excelCol = self.getColumnIndex(key);
                if (excelCol <= colLimit) {
                    String cellVal = self.getCellVal(row.getCell(excelCol));
                    if (!matchVal.equals(cellVal)) {
                        matched = false;
                        break;
                    }
                }
            }
        }

        return matched;
    }

    private boolean isNullRow(Row row, Integer lastCellNum) {
        boolean isNull = true;

        for(int i = 0; i < lastCellNum.intValue(); ++i) {
            if (row.getCell(i) != null && !"".equals(this.getCellVal(row.getCell(i)))) {
                isNull = false;
                break;
            }
        }

        return isNull;
    }

    private Sheet getSheet(Workbook book, Object sheetName) throws ExcelException {
        Sheet sheet = null;
        if (sheetName != null && sheetName instanceof String && !((String)sheetName).trim().equals("")) {
            sheet = book.getSheet(String.valueOf(sheetName));
        } else if (sheetName != null && sheetName instanceof Integer) {
            sheet = book.getSheetAt(((Integer)sheetName).intValue());
        } else {
            sheet = book.getSheetAt(0);
        }

        return sheet;
    }

    public static Pattern[] compileRules(String[] rules) {
        Pattern[] patterns = null;
        if (rules != null && rules.length > 0) {
            patterns = new Pattern[rules.length];
            int i = 0;
            String[] var3 = rules;
            int var4 = rules.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String rule = var3[var5];
                patterns[i++] = Pattern.compile(rule);
            }
        }

        return patterns;
    }

    private void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws IllegalAccessException, ParseException {
        Field field = this.getFieldByName(fieldName, o.getClass());
        if (field == null) {
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        } else {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if (Integer.TYPE != fieldType && Integer.class != fieldType) {
                if (Long.TYPE != fieldType && Long.class != fieldType) {
                    if (Float.TYPE != fieldType && Float.class != fieldType) {
                        if (Short.TYPE != fieldType && Short.class != fieldType) {
                            if (Double.TYPE != fieldType && Double.class != fieldType) {
                                if (Character.TYPE == fieldType) {
                                    if (fieldValue != null && fieldValue.toString().length() > 0) {
                                        field.set(o, fieldValue.toString().charAt(0));
                                    }
                                } else if (Date.class == fieldType) {
                                    field.set(o, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(fieldValue.toString()));
                                } else {
                                    field.set(o, fieldValue);
                                }
                            } else {
                                field.set(o, Double.valueOf(fieldValue.toString()));
                            }
                        } else {
                            field.set(o, Short.valueOf(fieldValue.toString()));
                        }
                    } else {
                        field.set(o, Float.valueOf(fieldValue.toString()));
                    }
                } else {
                    field.set(o, Long.valueOf(fieldValue.toString()));
                }
            } else {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            }

        }
    }

    private Field getFieldByName(String fieldName, Class<?> clazz) {
        Field[] selfFields = clazz.getDeclaredFields();
        Field[] var4 = selfFields;
        int var5 = selfFields.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            String s = field.getName();
            if (s.equals(fieldName)) {
                return field;
            }
        }

        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return this.getFieldByName(fieldName, superClazz);
        } else {
            return null;
        }
    }

    public int getColumnIndex(String col) {
        return CellReference.convertColStringToIndex(col);
    }

    public String excelColIndexToStr(int columnIndex) {
        return CellReference.convertNumToColString(columnIndex);
    }

    protected String getCellVal(Cell cell) {
        if (cell != null && cell.getCellType() != 3) {
            String cellValue;
            switch(cell.getCellType()) {
                case 0:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        cellValue = sdf.format(cell.getNumericCellValue());
                    } else {
                        cellValue = phoneFmt.format(cell.getNumericCellValue());
                    }
                    break;
                case 1:
                    cellValue = cell.getRichStringCellValue().getString().trim();
                    break;
                case 2:
                    cellValue = cell.getCellFormula();
                    break;
                case 3:
                default:
                    cellValue = "";
                    break;
                case 4:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
            }

            return cellValue;
        } else {
            return "";
        }
    }
}
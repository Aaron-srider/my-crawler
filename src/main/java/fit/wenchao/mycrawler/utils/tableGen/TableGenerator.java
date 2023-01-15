package fit.wenchao.mycrawler.utils.tableGen;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import fit.wenchao.mycrawler.model.po.gun.Gun;
import fit.wenchao.mycrawler.model.po.gun.GunAttrTranslation;
import fit.wenchao.mycrawler.model.po.gun.GunSortTranslation;
import fit.wenchao.mycrawler.utils.VarCaseConvertUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.servlet.support.JstlUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
class TableFieldInfo {
    String name;
    String typeName;
    int len;
    boolean primary;
}

public class TableGenerator {

    public static String quoteMysqlKeyword(String key) {
        return "`" + key + "`";
    }

    public static String quote(String key) {
        return "(" + key + ")";
    }


    public static String quote(List<String> keys) {
        if (keys.isEmpty()) {
            return "()";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String key : keys) {
            sb.append(key);
            sb.append(" ,");
        }

        String substring = sb.substring(0, sb.length() - 1);

        return substring + ")";
    }

    public static List<TableFieldInfo> getTableInfoList(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();

        List<TableFieldInfo> tableFieldInfos = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            TableFieldInfo tableFieldInfo = new TableFieldInfo();
            declaredField.setAccessible(true);
            String fieldName = declaredField.getName();
            TableGenFieldName fieldNameAnno = declaredField
                    .getAnnotation(TableGenFieldName.class);
            if (fieldNameAnno == null) {
                fieldName =
                        VarCaseConvertUtils.lowerCamel2LowerUnderScore(fieldName);
            }
            else {
                fieldName = fieldNameAnno.value();
            }
            tableFieldInfo.setName(fieldName);
            String fieldTypeName = "varchar";
            if (declaredField.getType().getName().equals("java.lang.String")) {
                fieldTypeName = "varchar";
            }
            else if (declaredField.getType().getName().equals("java.lang" +
                    ".Long")) {
                fieldTypeName = "int";
            }
            else if (declaredField.getType().getName().equals("java.lang" +
                    ".Integer")) {
                fieldTypeName = "int";
            }
            else if (declaredField.getType().getName().equals("int")) {
                fieldTypeName = "int";
            }
            else if (declaredField.getType().getName().equals("long")) {
                fieldTypeName = "int";
            }

            tableFieldInfo.setTypeName(fieldTypeName);

            int len = 0;
            TableGenFieldLength fieldLengthAnno = declaredField
                    .getAnnotation(TableGenFieldLength.class);
            if (fieldLengthAnno == null) {
                if (fieldTypeName.equals("varchar")) {
                    len = 255;
                }
                else if (fieldTypeName.equals("int")) {
                    len = 11;
                }
            }
            else {
                len = fieldLengthAnno.value();
            }
            tableFieldInfo.setLen(len);


            boolean primary = false;
            TableGenPrimary fieldPrimaryAnno = declaredField
                    .getAnnotation(TableGenPrimary.class);
            if (fieldPrimaryAnno == null) {
                primary = false;
            }
            else {
                primary = true;
            }
            tableFieldInfo.setPrimary(primary);
            tableFieldInfos.add(tableFieldInfo);
        }
        return tableFieldInfos;
    }

    public static String process(Class<?> clazz) {
        String tableName = null;
        tableName = getTableName(clazz);

        List<TableFieldInfo> tableFieldInfos = getTableInfoList(clazz);
        StringBuilder createTableStatementBuilder = new StringBuilder();
        createTableStatementBuilder.append("create table if not exists ");
        createTableStatementBuilder.append("`").append(tableName).append("`");
        createTableStatementBuilder.append("(\n");

        List<String> priKeyList = new ArrayList<>();
        for (TableFieldInfo tableFieldInfo : tableFieldInfos) {
            boolean primary = tableFieldInfo.isPrimary();
            String name = tableFieldInfo.getName();
            String typeName = tableFieldInfo.getTypeName();
            int len = tableFieldInfo.getLen();
            createTableStatementBuilder.append("  ");
            createTableStatementBuilder.append(quoteMysqlKeyword(name))
                                       .append(" ")
                                       .append(typeName);


            if (len != 0) {
                createTableStatementBuilder.append(quote(String.valueOf(len)));
            }

            if (primary) {
                priKeyList.add(name);
            }

            createTableStatementBuilder.append(",");
            createTableStatementBuilder.append("\n");
        }

        if (!priKeyList.isEmpty()) {
            createTableStatementBuilder.append("  ");
            createTableStatementBuilder.append("PRIMARY KEY").append(" ");
            priKeyList = priKeyList.stream().map((name) -> {
                                       return quoteMysqlKeyword(name);
                                   })
                                   .collect(Collectors.toList());

            createTableStatementBuilder.append(quote(priKeyList));
        }
        else {
            // remove new line and ,
            createTableStatementBuilder.delete(createTableStatementBuilder.length() - 2, createTableStatementBuilder.length());
        }

        createTableStatementBuilder.append("\n)").append("ENGINE = InnoDB;");
        String creatTableStatement = createTableStatementBuilder.toString();
        return creatTableStatement;
    }

    public static void main(String[] args) {


        String creatTableStatement;
        creatTableStatement = process(Gun.class);
        System.out.println(creatTableStatement);

        creatTableStatement = process(GunAttrTranslation.class);
        System.out.println(creatTableStatement);

        creatTableStatement = process(GunSortTranslation.class);
        System.out.println(creatTableStatement);

    }

    private static String getTableName(Class<?> clazz) {
        String tableName = null;
        TableGenFieldName tableNameAnno =
                Gun.class.getAnnotation(TableGenFieldName.class);
        if (tableNameAnno != null) {
            tableName = tableNameAnno.value();
        }
        else {
            tableName =
                    VarCaseConvertUtils.lowerCamel2LowerUnderScore(clazz.getSimpleName());
        }
        return tableName;
    }
}

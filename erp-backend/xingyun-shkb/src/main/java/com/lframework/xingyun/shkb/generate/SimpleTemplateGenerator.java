package com.lframework.xingyun.shkb.generate;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleTemplateGenerator {
    public static void main(String[] args) throws IOException {
        // 目标路径：resources/excel/contract/contract_task_replacement_part_simple_template.xlsx
        String targetPath = "src/main/resources/excel/contract/contract_task_replacement_part_simple_template.xlsx";

        // 1. 创建一个临时 Excel，写入标题和基础信息
        try (FileOutputStream fos = new FileOutputStream(targetPath)) {
            // 写入标题行（第1行）
            Map<String, Object> titleRow = new HashMap<>();
            titleRow.put("title", "必换件清单");
            // 写入基础信息行（第2行）
            Map<String, Object> baseInfoRow = new HashMap<>();
            baseInfoRow.put("contractCode", "合同号");
            baseInfoRow.put("replacementPartCode", "必换件单号");
            baseInfoRow.put("machineTypeName", "机型");
            baseInfoRow.put("serialNumber", "产品序号");

            // 写入表头行（第3行）
            String[] headers = {"序号", "名称", "件号", "数量", "单位", "换件原因", "价格", "备注"};
            // 写入占位符行（第4行）
            String[] placeholders = {"${index}", "${productName}", "${partNumberCode}", "${quantity}", "${unit}", "${replacementReason}", "${price}", "${remark}"};

            // 使用 EasyExcel 写入
            com.alibaba.excel.ExcelWriter writer = EasyExcel.write(fos).build();
            WriteSheet sheet = EasyExcel.writerSheet(0).build();

            // 写入标题（合并单元格，居中）
            writer.write(new java.util.ArrayList<java.util.Map<String, Object>>() {{
                add(titleRow);
            }}, sheet);

            // 写入基础信息占位符行（第2行）
            writer.write(new java.util.ArrayList<java.util.Map<String, Object>>() {{
                add(baseInfoRow);
            }}, sheet);

            // 写入表头行（第3行）
            writer.write(new java.util.ArrayList<java.util.List<String>>() {{
                add(java.util.Arrays.asList(headers));
            }}, sheet);

            // 写入占位符行（第4行）
            writer.write(new java.util.ArrayList<java.util.List<String>>() {{
                add(java.util.Arrays.asList(placeholders));
            }}, sheet);

            writer.finish();
        }

        System.out.println("极简模板生成完成: " + targetPath);
    }
}

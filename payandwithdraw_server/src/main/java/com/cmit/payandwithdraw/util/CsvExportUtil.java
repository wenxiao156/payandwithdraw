package com.cmit.payandwithdraw.util;

import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CsvExportUtil {
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件行分隔符
     */
    private static final String CSV_ROW_SEPARATOR = System.lineSeparator();

    /**
     * @param dataList 集合数据
     * @param titles   表头部数据
     * @param keys     表内容的键值
     */
    public static StringBuffer getStringBuffer(List<Map<String, Object>> dataList, String titles, String keys) {
        // 保证线程安全
        StringBuffer buf = new StringBuffer();

        String[] titleArr = titles.split(",");
        String[] keyArr = keys.split(",");

        // 组装表头
        for (String title : titleArr) {
            buf.append(title).append(CSV_COLUMN_SEPARATOR);
        }
        buf.append(CSV_ROW_SEPARATOR);

        // 组装数据
        if (!CollectionUtils.isEmpty(dataList)) {
            for (Map<String, Object> data : dataList) {
                for (String key : keyArr) {
                    buf.append(data.get(key)).append(CSV_COLUMN_SEPARATOR);
                }
                buf.append(CSV_ROW_SEPARATOR);
            }
        }
        return buf;
    }

}

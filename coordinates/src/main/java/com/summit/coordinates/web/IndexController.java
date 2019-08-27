package com.summit.coordinates.web;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.base.Strings;
import com.summit.coordinates.common.Coordinate;
import com.summit.coordinates.common.ExcelListener;
import com.summit.coordinates.util.CoordinateConvertUtils;
import com.summit.coordinates.util.CoordinateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by liusj on 2019/5/24
 */
@Controller
@Slf4j
public class IndexController {

    private static final String REGEX = "^\\d+" + "°" + "\\d+" + "′" + "\\d*\\.?\\d*" + "″";
    private static final String REGEX2 = "^\\d+" + "°" + "\\d*\\.?\\d*$";
    private static final String REGEX3 = "^\\d+" + "(\\." + "\\d+)?$";

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 模板数据下载.
     */
    @GetMapping("/export2")
    public void downLoad2(HttpServletResponse response) throws Exception {

        List<Coordinate> result = new ArrayList<>();
        // 模拟数据
        basicData(result);

        //  写入excel
        response.setCharacterEncoding("UTF-8");
        String name = URLEncoder.encode("模板示例.xlsx", "UTF-8");
        response.setContentType("application/x-msdownload");
        response.addHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + name);
        try (OutputStream out = response.getOutputStream()) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, Coordinate.class);
            sheet1.setSheetName("火星坐标");
            writer.write(result, sheet1);
            writer.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void basicData(List<Coordinate> result) {
        Coordinate coordinate = new Coordinate();
        coordinate.setLongitude("107.86377");
        coordinate.setLatitude("33.358062");
        Coordinate coordinate2 = new Coordinate();
        coordinate2.setLongitude("107°56′49.58″");
        coordinate2.setLatitude("33°13′7.93″");
        Coordinate coordinate3 = new Coordinate();
        coordinate3.setLongitude("107°56");
        coordinate3.setLatitude("33°13");
        Coordinate coordinate4 = new Coordinate();
        coordinate4.setLongitude("107°56″49.58″");
        coordinate4.setLatitude("33°13″7.93″");
        result.add(coordinate);
        result.add(coordinate2);
        result.add(coordinate3);
        result.add(coordinate4);
    }

    /**
     * 导出excel.
     */
    @GetMapping("/export")
    public void downLoad(HttpServletResponse response) throws Exception {
        Cache cache = cacheManager.getCache("file");
        Cache.ValueWrapper cacheValue = cache.get("result");
        if (cacheValue != null) {
            List<Coordinate> result = (List<Coordinate>) cacheValue.get();
            //  写入excel
            response.setCharacterEncoding("UTF-8");
            String name = URLEncoder.encode("火星坐标.xlsx", "UTF-8");
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + name);
            try (OutputStream out = response.getOutputStream()) {
                ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
                Sheet sheet1 = new Sheet(1, 0, Coordinate.class);

                log.info("=== sheet" + sheet1.toString());

                sheet1.setSheetName("火星坐标");
                log.info("=== writer" + writer);
                log.info("=== result " + result);
                writer.write(result, sheet1);
                log.info("=== 1");
                writer.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("=== cache.clear");
        cache.clear();
    }

    /**
     * excel 导入.
     */
    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
        // 清缓存
        Cache cache = cacheManager.getCache("file");
        cache.clear();

        // 解析每行结果在listener中处理
        ExcelListener listener = new ExcelListener();
        ExcelReader excelReader = new ExcelReader(file.getInputStream(), ExcelTypeEnum.XLSX, null, listener);
        excelReader.read(new Sheet(1, 1, Coordinate.class));

        List<Coordinate> datas = listener.getDatas();

        // 数据转化
        Pattern pattern = Pattern.compile(REGEX);
        Pattern pattern2 = Pattern.compile(REGEX2);
        Pattern pattern3 = Pattern.compile(REGEX3);

        List<Coordinate> result = new ArrayList<>();
        datas.forEach(coordinate -> {
            if (!Strings.isNullOrEmpty(coordinate.getLatitude())) {
                if (pattern.matcher(coordinate.getLatitude().trim()).matches() && pattern.matcher(coordinate.getLongitude().trim()).matches()) {
                    coordinate.setLongitude(CoordinateUtil.DmsTurnDD(coordinate.getLongitude()));
                    coordinate.setLatitude(CoordinateUtil.DmsTurnDD(coordinate.getLatitude()));
                    result.add(CoordinateConvertUtils.wgs84ToGcj02Copy(coordinate));
                } else if (pattern2.matcher(coordinate.getLatitude().trim()).matches() && pattern2.matcher(coordinate.getLongitude().trim()).matches()) {
                    coordinate.setLongitude(CoordinateUtil.DmTurnDD(coordinate.getLongitude()));
                    coordinate.setLatitude(CoordinateUtil.DmTurnDD(coordinate.getLatitude()));
                    result.add(CoordinateConvertUtils.wgs84ToGcj02Copy(coordinate));
                } else if (pattern3.matcher(coordinate.getLatitude().trim()).matches() && pattern3.matcher(coordinate.getLongitude().trim()).matches()) {
                    result.add(CoordinateConvertUtils.wgs84ToGcj02Copy(coordinate));
                } else {
                    System.out.println(coordinate);
                    Coordinate newObj = new Coordinate();
                    newObj.setLatitude("数据格式有误");
                    newObj.setLongitude("数据格式有误");
                    result.add(newObj);
                }
            } else {
                result.add(coordinate);
            }
        });
        //  写入緩存
        cache.put("result", result);
//        return "redirect:/";
    }
}

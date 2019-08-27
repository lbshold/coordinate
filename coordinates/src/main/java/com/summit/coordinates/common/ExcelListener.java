package com.summit.coordinates.common;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liusj on 2019/5/27
 * 阿里easyExcel.
 */
public class ExcelListener extends AnalysisEventListener {

    private List<Coordinate> datas = new ArrayList<>();

    @Override
    public void invoke(Object obj, AnalysisContext analysisContext) {
        datas.add((Coordinate) obj);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<Coordinate> getDatas() {
        return datas;
    }

    public void setDatas(List<Coordinate> datas) {
        this.datas = datas;
    }
}

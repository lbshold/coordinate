package com.summit.coordinates.web;

import com.summit.coordinates.util.CoordinateConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liusj on 2019/6/17
 */
@Api(description = "坐标转换")
@RestController
@RequestMapping("/transform")
public class TransformController {


    @ApiOperation(value = "火星坐标系(GCJ-02)转百度坐标系(BD-09)")
    @GetMapping("/gcg02ToBd09/{lng}/{lat}")
    public Object transform(@ApiParam(value = "经度") @PathVariable double lng,
                            @ApiParam(value = "纬度") @PathVariable double lat) {
        return CoordinateConvertUtils.gcg02ToBd09(lng, lat);
    }


    @ApiOperation(value = "百度坐标系(BD-09)转火星坐标系(GCJ-02)")
    @GetMapping("/bd09ToGcj02/{lng}/{lat}")
    public Object transform2(@ApiParam(value = "经度") @PathVariable double lng,
                            @ApiParam(value = "纬度") @PathVariable double lat) {
        return CoordinateConvertUtils.bd09ToGcj02(lng, lat);
    }

    @ApiOperation(value = "国际坐标(WGS84)转火星坐标系(GCJ02)")
    @GetMapping("/wgs84ToGcj02/{lng}/{lat}")
    public Object transform3(@ApiParam(value = "经度") @PathVariable double lng,
                             @ApiParam(value = "纬度") @PathVariable double lat) {
        return CoordinateConvertUtils.wgs84ToGcj02(lng, lat);
    }

    @ApiOperation(value = "火星坐标系(GCJ02)转国际坐标(WGS84).)")
    @GetMapping("/gcj02ToWgs84/{lng}/{lat}")
    public Object transform4(@ApiParam(value = "经度") @PathVariable double lng,
                             @ApiParam(value = "纬度") @PathVariable double lat) {
        return CoordinateConvertUtils.gcj02ToWgs84(lng, lat);
    }
}

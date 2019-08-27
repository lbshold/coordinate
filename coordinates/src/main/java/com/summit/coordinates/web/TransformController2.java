package com.summit.coordinates.web;

import com.summit.coordinates.util.CoordinateUtil;
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
@Api(description = "经纬度格式转换")
@RestController
@RequestMapping("/transform2")
public class TransformController2 {


    @ApiOperation(value = "经纬度转化，度分秒转度", notes = "如：108°13′21″= 108.2225")
    @GetMapping("/DmsTurnDD/{jwd}")
    public Object transform(@ApiParam(value = "度分秒格式的经纬度") @PathVariable String jwd) {
        return CoordinateUtil.DmsTurnDD(jwd);
    }

    @ApiOperation(value = "经纬度转换，度分转度度", notes = "如：112°30.4128 = 112.50688")
    @GetMapping("/DmTurnDD/{jwd}")
    public Object transform2(@ApiParam(value = "度分格式的经纬度") @PathVariable String jwd) {
        return CoordinateUtil.DmTurnDD(jwd);
    }
}

package com.summit.coordinates.common;

import lombok.Data;

/**
 * Created by liusj on 2019/6/17
 */
@Data
public class Point {

    private double lng;
    private double lat;

    public Point(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }
}

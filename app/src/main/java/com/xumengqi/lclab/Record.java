package com.xumengqi.lclab;

import java.util.Date;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
class Record {
    private Date date;
    private int calorie;

    Record(Date date, int calorie) {
        this.date = date;
        this.calorie = calorie;
    }

    Date getDate() {
        return date;
    }

    int getCalorie() {
        return calorie;
    }
}

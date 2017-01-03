package com.capitalone.efit.utility;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ndb338 on 1/3/17.
 */
public class Line {
    String data;
    Date timestamp;

    public Line(String line) {
        data=new String(line);
        timestamp= Calendar.getInstance().getTime();
    }
}

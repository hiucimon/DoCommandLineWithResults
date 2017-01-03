package com.capitalone.efit.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndb338 on 12/29/16.
 */
public class OutputCatcher extends Thread {
    public List<Line> getLines() {
        return Lines;
    }

    private final List<Line> Lines =new ArrayList<>();
    InputStream inputStream;

    OutputCatcher(InputStream is)
    {
        this.inputStream = is;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                Lines.add(new Line(line));
            }
                //System.out.println(label + ">" + line);
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}

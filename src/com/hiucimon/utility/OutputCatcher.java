package com.hiucimon.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ndb338 on 12/29/16.
 */
public class OutputCatcher extends Thread {
    public List<Line> getLines() {
        return Lines;
    }

    private final List<Line> Lines =new ArrayList<>();
    InputStream inputStream;
    Consumer<String> callback=null;

    OutputCatcher(InputStream is)
    {
        this.inputStream = is;
    }

    OutputCatcher(InputStream is, Consumer<String> callback) {this.inputStream = is; this.callback=callback;}

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                if (callback!=null) callback.accept(line.toString());
                Lines.add(new Line(line));
            }
                //System.out.println(label + ">" + line);
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}

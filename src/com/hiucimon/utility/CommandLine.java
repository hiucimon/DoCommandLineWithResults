package com.hiucimon.utility;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by ndb338 on 12/29/16.
 */
public class CommandLine {
    public int pid;
    public OutputCatcher stderr;
    public OutputCatcher stdout;
    private Process p;
    private final Runtime myRuntime;
    private final int COMMAND_FAILED=-9999;
    public CommandLine() {
        myRuntime=Runtime.getRuntime();
    }
    public void Kill() {
//        try {
//            System.out.println("Kill "+pid);
//            myRuntime.exec("kill -9 "+pid);
//            System.out.println("Killed "+pid);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (p!=null) {
//            System.out.println("Destroy "+p);
            p.destroy();
        }
    }
    public void PID() {
        if(p.getClass().getName().equals("java.lang.UNIXProcess")) {
  /* get the PID on unix/linux systems */
            try {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getInt(p);
            } catch (Throwable e) {
            }
        }
//        System.out.println("The PID is:"+pid);
    }
    public Integer RunCommandWithResults(String cmd,Consumer<String> callback) {
        try {
            p=myRuntime.exec(cmd);
            PID();
            stderr=new OutputCatcher(p.getErrorStream(),callback);
            stdout=new OutputCatcher(p.getInputStream(),callback);
            stderr.start();
            stdout.start();
            int result = p.waitFor();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        }
        return COMMAND_FAILED;
    }
    public Integer RunCommandWithResults(String cmd,Consumer<String> callbacko,Consumer<String> callbacke) {
        try {
            p=myRuntime.exec(cmd);
            PID();
            stderr=new OutputCatcher(p.getErrorStream(),callbacke);
            stdout=new OutputCatcher(p.getInputStream(),callbacko);
            stderr.start();
            stdout.start();
            int result = p.waitFor();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        }
        return COMMAND_FAILED;
    }
    public Integer RunCommandWithResults(String cmd) {
        try {
            p=myRuntime.exec(cmd);
            PID();
            stderr=new OutputCatcher(p.getErrorStream());
            stdout=new OutputCatcher(p.getInputStream());
            stderr.start();
            stdout.start();
            int result = p.waitFor();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        }
        return COMMAND_FAILED;
    }
    public Integer RunCommandWithResults(String cmd, Integer timeout) {
        int result=-9999;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = (Future<Integer>) executor.submit(() -> {
                        return this.RunCommandWithResults(cmd);
                }
        );
        try {
            System.out.println("Started..");
            result=future.get(timeout, TimeUnit.SECONDS);
            if (!future.isDone()) {
                Kill();

            }
            Kill();
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            Kill();
//            System.out.println("Terminated!");
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
        return result;
    }
    public Integer RunCommandWithResults(String cmd, Integer timeout,Consumer<String> callbacko,Consumer<String> callbacke) {
        int result=-9999;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = (Future<Integer>) executor.submit(() -> {
                    return this.RunCommandWithResults(cmd,callbacko,callbacke);
                }
        );
        try {
            System.out.println("Started..");
            result=future.get(timeout, TimeUnit.SECONDS);
            if (result==-9999) {
                Kill();
            }
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            Kill();
//            System.out.println("Terminated!");
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
        return result;
    }
    public Integer RunCommandWithResults(String cmd, Integer timeout,Consumer<String> callback) {
        int result=-9999;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = (Future<Integer>) executor.submit(() -> {
                    return this.RunCommandWithResults(cmd,callback);
                }
        );
        try {
            System.out.println("Started..");
            result=future.get(timeout, TimeUnit.SECONDS);
            if (result==-9999) {
                Kill();
            }
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            Kill();
//            System.out.println("Terminated!");
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
        return result;
    }
    public static void main(String[] argv) {
        System.out.println("Starting");
        CommandLine c=new CommandLine();
        int r=c.RunCommandWithResults("clamscan -v DoCommandLineWithResults.iml");
        System.out.println("Results="+r);

        c.stdout.getLines().forEach(
                l->{
                    String[] parts = l.data.split("\\s*:\\s*");
                    if (parts.length<2) {
                        //skip it
                        //System.out.println("vvvvvv---Do nothing to this line");
                    } else if (parts[1].equals("OK")) {
                        //System.out.println("------OK");
                    } else {
                        parts[0]=parts[0].replace(' ','_');
                        System.out.println("----- Massage the data to be usable:"+parts[0]+":"+parts[1]+":");
                        String[] part2 = parts[1].split(" ");
                        if (part2.length>1) {
                            Double w1;
                            float w2;
                            switch (part2[1].toLowerCase()) {
                                case "kb":  w1=Double.parseDouble(part2[0]);
                                            System.out.println(parts[0]+":"+w1*1000.0);
                                    break;
                                case "mb":w1=Double.parseDouble(part2[0]);
                                    System.out.println(parts[0]+":"+w1*1000000.0);
                                    break;
                                case "gb":w1=Double.parseDouble(part2[0]);
                                    System.out.println(parts[0]+":"+w1*1000000000.0);
                                    break;
                                case "sec":w2=Float.parseFloat(part2[0]);
                                    System.out.println(parts[0]+":"+w2);
                                    break;
                                default:
                            }
                        }
                    }
                    //System.out.println("My command returned:  "+l);
                }
        );


        int r2=c.RunCommandWithResults("clamscan -v DoCommandLineWithResults.iml",1);
        System.out.println("Results="+r2);
        c.stdout.getLines().forEach(l->System.out.println("My command returned:  "+l));
        int r3=c.RunCommandWithResults("clamscan -v DoCommandLineWithResults.iml",ca->System.out.println("Got:"+ca));
        int r4=c.RunCommandWithResults("clamscan -v DoCommandLineWithResults.iml",cao->System.out.println("Got out:"+cao),cae->System.out.println("Got err:"+cae));
        int r5=c.RunCommandWithResults("clamscan -v DoCommandLineWithResults.iml",1,cao->System.out.println("Got #out:"+cao),cae->System.out.println("Got #err:"+cae));
        int r6=c.RunCommandWithResults("ping localhost",1,cao->System.out.println("Got #out:"+cao),cae->System.out.println("Got #err:"+cae));
    }
}

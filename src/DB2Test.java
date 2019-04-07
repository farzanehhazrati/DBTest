import org.apache.log4j.Logger;

import java.util.TreeMap;

public class DB2Test {
    private static Logger log2 = Logger.getLogger(DB2Test.class);
    class Control {
        public volatile boolean flag = false;
        public volatile long cnt = 0;
        public volatile long conTime = 0;
        public volatile TreeMap<String,long[]> queryTimesMap=new TreeMap<String,long[]>();

    }
    final Control control = new Control();

    class ThreadDemo extends Thread {
        private Thread t;
        private String threadName;
        private String threadConfigFileName;
        ThreadDemo( String name,String configFileName) {
            threadName = name;
            threadConfigFileName=configFileName;
            //System.out.println("Creating " +  threadName );
            log2.info("Creating " + threadName);
        }
        DataTableView dataTableView;
        public void run() {
            //System.out.println("Running " +  threadName );
            log2.info("Running " +  threadName);
             dataTableView=new DataTableView(threadConfigFileName);
            control.conTime+=dataTableView.conTime;
            control.cnt++;
            long[] times =null;
            for (String queryName: dataTableView.queryTimesMap.keySet()){
                times=dataTableView.queryTimesMap.get(queryName);
                if (!control.queryTimesMap.containsKey(queryName))
                    control.queryTimesMap.put(queryName, times);
                else {
                    long[] oldTimes = control.queryTimesMap.get(queryName);
                    times[0] += oldTimes[0];
                    times[1] += oldTimes[1];
                    control.queryTimesMap.put(queryName, times);
                }
            }
            //System.out.println(threadName +"   "+ control.cnt+"   "+control.conTime+"  "+(control.conTime/control.cnt));
            log2.info( control.cnt +"\t"+ threadName +"   connection Time: total: "+control.conTime+"\t avg: "+(control.conTime/control.cnt));
            for (String queryName: control.queryTimesMap.keySet()) {
                times=control.queryTimesMap.get(queryName);
                //System.out.println(queryName + "     " + times[0] + "     " + times[1] + "   " + times[0]/control.cnt+"     "+times[1]/control.cnt );
                log2.info( control.cnt +"\t"+queryName + "\texecuting Query: total: " + times[0] +"\tcreating objects: total: " + times[1] +"\texecuting Query: avg: " + times[0]/control.cnt+"\tcreating objects: avg: "+times[1]/control.cnt );
            }

            //System.out.println("Thread " +  threadName + " exiting.");
            log2.info(  threadName + " exiting.");

        }

        public void start () {
            //System.out.println("Starting " + threadName);
            log2.info("Starting " + threadName);
            if (t == null) {
                t = new Thread (this, threadName);
                t.start ();
            }
        }
    }

    private void test(int threadCount,String configFileName) {
        ThreadDemo T[]=new ThreadDemo[threadCount];
        for (int i = 0; i < threadCount ; i++) {
            T[i] = new ThreadDemo( "Thread"+i,configFileName);
            T[i].start();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        try {
            int threadCount=1;
            if (args.length>0)
                threadCount=Integer.parseInt(args[0]);
            String configFileName="Config.properties";
            if (args.length==2)
                configFileName=args[1];
            DB2Test test = new DB2Test();
            test.test(threadCount,configFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

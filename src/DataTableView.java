
import org.apache.log4j.Logger;

import javax.transaction.TransactionRequiredException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;



public class DataTableView {
    private List<DataTableColumn> dataTableColumnsInExpansion = new ArrayList<DataTableColumn>();
    TreeMap<String,long[]> queryTimesMap=new TreeMap<String,long[]>();
    String columnsProperties="";
    String configFileName="Config.properties";
    ArrayList<String> queryList=new ArrayList<String>();

    long conTime;

    private List<DataTableRow> dataTableRows = new ArrayList<DataTableRow>();

    TreeMap<Integer, DataTableColumn> dataTableColumnTreeMap = new TreeMap<Integer, DataTableColumn>();
    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    JDBCPreparedConnection jdbcPreparedConnection;

    private static Logger log = Logger.getLogger(DataTableView.class);


    public void DataTableView() {

        String query="";
        InputStream input = null;

        File infile = new File("TestDB2query.txt");
        int q=0;
        if (infile.exists()){
            String myBuffer;
            try {
                FileReader fd = new FileReader(infile);
                BufferedReader br = new BufferedReader(fd);
                myBuffer = br.readLine();


//                System.out.println("myBuffer = " + myBuffer);
//                System.out.println("query = " + query);
                while (myBuffer != null ) {

                    if (myBuffer.toUpperCase().contains("QUERY_")) {
                        if (!query.trim().equals("")) {
                            queryList.add(query);
                            //System.out.println("query*** = " + query);
                            query = "";
                        }
                    }

                        query += " "+myBuffer;
                       //System.out.println("myBuffer = " + myBuffer);


                    myBuffer = br.readLine();
                }
                queryList.add(query);
                //System.out.println("query*** = " + query);
                fd.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


     /* jdbcPreparedConnection = new JDBCPreparedConnection(configFileName);

        getMetaData();
       jdbcPreparedConnection.close();
        dataTableTotalColumns();
        dataTableColumnsInExpansion();*/
        long t1 = System.currentTimeMillis();
        jdbcPreparedConnection = new JDBCPreparedConnection(configFileName);
        long t2 = System.currentTimeMillis();
        conTime=t2-t1;
        for (String query1 : queryList) {
            String queryName=query1.substring(0,query1.toUpperCase().indexOf("="));
            long[] times = getData(queryName, query1.substring(query1.toUpperCase().indexOf("=")+1));
                     if (!queryTimesMap.containsKey(queryName))
                         queryTimesMap.put(queryName,times);
            else{
                         long[] oldTimes=queryTimesMap.get(queryName);
                         times[0]+=oldTimes[0];
                         times[1]+=oldTimes[1];
                         queryTimesMap.put(queryName,times);
                     }
        }
        jdbcPreparedConnection.close();
        long t3 = System.currentTimeMillis();



    }
    public DataTableView() {
        DataTableView();
    }
    public DataTableView(String configFileName) {
this.configFileName=configFileName;
        //System.out.println("configFileName = " + configFileName);
        log.info("configFileName = " + configFileName);
        DataTableView();
    }
    public void dataTableColumnsInExpansion() {
        List<DataTableColumn> dataTableExpansionColumnList = new ArrayList<DataTableColumn>();

        for (Integer key : this.dataTableColumnTreeMap.keySet()) {
            dataTableExpansionColumnList.add(this.dataTableColumnTreeMap.get(key));
        }
        dataTableColumnsInExpansion = dataTableExpansionColumnList;
    }
    public ResultSet getRS(String q) {
        ResultSet resultSet;
        resultSet = jdbcPreparedConnection.getResultSet(q);
        return resultSet;
    }
    public long[] getData(String queryName,String query) {
        long[] times = new long[2];
        long t1=new Long(System.currentTimeMillis());
        List<DataTableRow> dataTableRowList = new ArrayList<DataTableRow>();
        try {
            ResultSet rs1 = getRS(query);
            long t2 = System.currentTimeMillis();
            if (rs1 != null) {
                ResultSetMetaData resultSetMetaData = rs1.getMetaData();
                while (rs1.next()) {
                    DataTableRow dataTableRow = new DataTableRow();
                    HashMap hs = new HashMap();
                    //log.info("-------------------");
                    for (int cc = 1; cc < resultSetMetaData.getColumnCount() + 1; cc++) {
                        //System.out.println("resultSetMetaData.getColumnTypeName("+cc+")=" + resultSetMetaData.getColumnTypeName(cc));
                        //log.info(resultSetMetaData.getColumnLabel(cc)+"=" + rs1.getString(cc));
                        hs.put(resultSetMetaData.getColumnLabel(cc), rs1.getString(cc));
                    }
                    dataTableRow.setHs(hs);
                    dataTableRowList.add(dataTableRow);
                }
            }
            long t3 = System.currentTimeMillis();
            log.info(df.format(new Date())+"\t"+queryName+"\t\t"+dataTableRowList.size()+" Records.\t\t"+(t2-t1)+"ms\t\t"+(t3-t2)+"ms");
                 times[0]=t2-t1;
            times[1]=t3-t2;
             //log.info("receive from memory:");
            String record="";
            for (DataTableRow dataTableRow:dataTableRowList) {
                record="";
               for (Object col:dataTableRow.getHs().keySet()){
                   record+=dataTableRow.getHs().get(col)+"\t";

               }
                //log.info(record);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.dataTableRows = dataTableRowList;
        //System.out.println("getdata dateTime2 = " + df.format(new Date()));
       return times;

    }
  public void dataTableTotalColumns() {
        if (getColumnsProperties() != null && !getColumnsProperties().equals("")) {
            String[] columnsProperties = getColumnsProperties().split("\n");

            for (int row = 0; row < columnsProperties.length; row++) {
                //System.out.println("columnsProperties[row] = " + columnsProperties[row]);
                if (new DataTableColumn(columnsProperties[row]).isValid())

                    this.dataTableColumnTreeMap.put(row, new DataTableColumn(columnsProperties[row]));
            }

        }
    }

    public String dateFormat(String date) {
        if (date.trim().equals(""))
            return date;
        else
            return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }

    public String timeFormat(String time) {
        if (time.trim().equals(""))
            return time;
        else
            return time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
    }

    public static ArrayList<String> createArrayList(String... elements) {
        ArrayList<String> list = new ArrayList<String>();
        for (String element : elements) {
            list.add(element);
        }
        return list;
    }

    public static ArrayList<Boolean> createBooleanArrayList(Boolean... elements) {
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        for (Boolean element : elements) {
            list.add(element);
        }
        return list;
    }


    public void getMetaData(String query){
        columnsProperties="";
        try {
            jdbcPreparedConnection = new JDBCPreparedConnection(configFileName);
            ResultSet rs1 = getRS(query);
            if (rs1 != null){
                ResultSetMetaData resultSetMetaData=rs1.getMetaData();
                for(int cc=1;cc<resultSetMetaData.getColumnCount()+1;cc++){
                    //System.out.println("x.getColumnName(cc) = " + resultSetMetaData.getColumnName(cc));

                    columnsProperties+=resultSetMetaData.getColumnName(cc)+":"+resultSetMetaData.getColumnTypeName(cc)+":"+resultSetMetaData.getColumnName(cc)+":"+"true"+"\n";
                }
            }
            jdbcPreparedConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getColumnsProperties() {
        return columnsProperties;
    }

    public void setColumnsProperties(String columnsProperties) {
        this.columnsProperties = columnsProperties;
    }

    public List<DataTableColumn> getDataTableColumnsInExpansion() {
        return dataTableColumnsInExpansion;
    }

    public void setDataTableColumnsInExpansion(List<DataTableColumn> dataTableColumnsInExpansion) {
        this.dataTableColumnsInExpansion = dataTableColumnsInExpansion;
    }

    public List<DataTableRow> getDataTableRows() {
        return dataTableRows;
    }

    public void setDataTableRows(List<DataTableRow> dataTableRows) {
        this.dataTableRows = dataTableRows;
    }
}
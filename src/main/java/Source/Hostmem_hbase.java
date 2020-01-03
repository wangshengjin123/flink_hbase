package Source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hostmem_hbase {
    private static TableName tableName = TableName.valueOf("www_hostmem");
    private static final String columnFamily = "info";
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties poro = new Properties();
        env.enableCheckpointing(1000);
        //ceshi:172.17.0.56:9092
        // aliyun:172.16.2.37:9092
        poro.setProperty("bootstrap.servers", "172.16.2.37:9092");
        poro.setProperty("group.id", "test1");
        FlinkKafkaConsumer011 kafkaConsumer011 = new FlinkKafkaConsumer011("hostmem", new SimpleStringSchema(), poro);
        kafkaConsumer011.setStartFromEarliest();
        DataStreamSource<String> data = env.addSource(kafkaConsumer011);
        data.rebalance().map(new MapFunction<String, Object>() {
            public String map(String value)throws IOException {
                System.out.println(value);
                writeIntoHBase(value);
                return value;
            }
        }).print();
        //transction.writeAsText("/home/admin/log2");
        // transction.addSink(new HBaseOutputFormat();
        try {
            env.execute("hostmem data");
        } catch (Exception ex) {
            Logger.getLogger(Hostio_hbase.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public static void writeIntoHBase(String m)throws IOException
    {
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();

        config.set("hbase.zookeeper.quorum", "172.16.2.37:2181");
//        config.set("hbase.master", "172.16.2.37:60000");

//        config.set("hbase.zookeeper.property.clientPort", hbaseZookeeperClinentPort);
        config.setInt("hbase.rpc.timeout", 20000);
        config.setInt("hbase.client.operation.timeout", 30000);
        config.setInt("hbase.client.scanner.timeout.period", 200000);

        //config.set(TableOutputFormat.OUTPUT_TABLE, hbasetable);
        try {
            //2种方式
        Connection c = ConnectionFactory.createConnection(config);
//            Connection c = ConnectionFactory.createConnection(config, new ThreadPoolExecutor(8, 32, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2048), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy()));

            System.out.println("begin...");
            Admin admin = c.getAdmin();
            if(!admin.tableExists(tableName)){
                admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(columnFamily)));
            }
            Table t = c.getTable(tableName);
            Timestamp time1;
            String host_name;
            String metrictype;
            String pct;
/*        TimeStamp ts = new TimeStamp(new Date());
        Date date = ts.getDate();*/
            JSONObject Model1_1= JSON.parseObject(m);
            time1=Model1_1.getTimestamp("time");
            host_name   =Model1_1.getString("host_name");
            metrictype  =Model1_1.getString("metrictype");
            pct     =Model1_1.getString("pct");

            Put put = new Put(org.apache.hadoop.hbase.util.Bytes.toBytes(time1.toString()));

            put.addColumn(org.apache.hadoop.hbase.util.Bytes.toBytes(columnFamily), org.apache.hadoop.hbase.util.Bytes.toBytes("host_name"), org.apache.hadoop.hbase.util.Bytes.toBytes(host_name));
            put.addColumn(org.apache.hadoop.hbase.util.Bytes.toBytes(columnFamily), org.apache.hadoop.hbase.util.Bytes.toBytes("metrictype"), org.apache.hadoop.hbase.util.Bytes.toBytes(metrictype));
            put.addColumn(org.apache.hadoop.hbase.util.Bytes.toBytes(columnFamily), org.apache.hadoop.hbase.util.Bytes.toBytes("pct"), org.apache.hadoop.hbase.util.Bytes.toBytes(pct));

            t.put(put);

            t.close();

            c.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("error");
        }
    }
}

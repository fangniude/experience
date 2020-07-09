package influxdb;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class InfluxdbTest {
    @Test
    public void test() {
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        String dbName = "aTimeSeries";
        influxDB.query(new Query("CREATE DATABASE " + dbName));
        influxDB.setDatabase(dbName);
        String rpName = "aRetentionPolicy";
        influxDB.query(new Query("CREATE RETENTION POLICY " + rpName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
        influxDB.setRetentionPolicy(rpName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        influxDB.write(Point.measurement("cpu")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("idle", 90L)
                .addField("user", 9L)
                .addField("system", 1L)
                .build());

        influxDB.write(Point.measurement("disk")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("used", 80L)
                .addField("free", 1L)
                .build());

        Query query = new Query("SELECT idle FROM cpu", dbName);
        influxDB.query(query);
        influxDB.query(new Query("DROP RETENTION POLICY " + rpName + " ON " + dbName));
        influxDB.query(new Query("DROP DATABASE " + dbName));
        influxDB.close();
    }

    @Test
    public void test1() {
        InfluxDB db = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        QueryResult databases = db.query(new Query("show databases"));

        System.out.println(databases);
    }
}

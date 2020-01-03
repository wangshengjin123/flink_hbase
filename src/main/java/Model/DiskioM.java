package Model;

import com.alibaba.fastjson.annotation.JSONField;

import java.sql.Timestamp;


public class DiskioM {



    private String host_name;
    private String metrictype;
    private String io_time;
    private String iostat_await;
    private String iostat_busy;
    private String iostat_time;
    private String disk_name;
    @JSONField(format="yyyy-MM-ddTHH:mm:ssZ")
    public Timestamp time;
    public Timestamp getTime() {
        return time;
    }
    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getMetrictype() {
        return metrictype;
    }

    public void setMetrictype(String metrictype) {
        this.metrictype = metrictype;
    }

    public String getIo_time() {
        return io_time;
    }

    public void setIo_time(String io_time) {
        this.io_time = io_time;
    }

    public String getIostat_await() {
        return iostat_await;
    }

    public void setIostat_await(String iostat_await) {
        this.iostat_await = iostat_await;
    }

    public String getIostat_busy() {
        return iostat_busy;
    }

    public void setIostat_busy(String iostat_busy) {
        this.iostat_busy = iostat_busy;
    }

    public String getIostat_time() {
        return iostat_time;
    }

    public void setIostat_time(String iostat_time) {
        this.iostat_time = iostat_time;
    }

    public String getDisk_name() {
        return disk_name;
    }

    public void setDisk_name(String disk_name) {
        this.disk_name = disk_name;
    }
}

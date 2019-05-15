package model;

public class ComputerInfo {


    public long cpuUsage;
    public long memUsage;
    public long disUsage;
    public ComputerInfo(long cpuUsage, long memUsage, long disUsage) {
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.disUsage = disUsage;
    }

    public long getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(long cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(long memUsage) {
        this.memUsage = memUsage;
    }

    public long getDisUsage() {
        return disUsage;
    }

    public void setDisUsage(long disUsage) {
        this.disUsage = disUsage;
    }

    @Override
    public String toString() {
        return "ComputerInfo{" +
                "cpuUsage=" + cpuUsage +
                ", memUsage=" + memUsage +
                ", disUsage=" + disUsage +
                '}';
    }

}

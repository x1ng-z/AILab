package hs.ServiceBus;

import hs.Bean.ControlModle;
import hs.Dao.Service.ModleDBServe;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public abstract class ModleRebuildTask implements Delayed,Task {
    private final int delaymilli;//延时时间
    private final long trigger;//触发时间
    private final int modleid;

    private ControlModle controlModle;
    private ModleDBServe modleDBServe;
    public ModleRebuildTask(int delaymillisec,int modleid) {
        this.delaymilli = delaymillisec;
        this.modleid=modleid;
        this.trigger= System.nanoTime()+NANOSECONDS.convert(delaymillisec,MILLISECONDS);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(trigger- System.nanoTime(),NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed message) {
        ModleRebuildTask that=(ModleRebuildTask)message;
        return (this.trigger<that.trigger)?-1:((this.trigger>that.trigger)?1:0);
    }

    public ControlModle getControlModle() {
        return controlModle;
    }

    public void setControlModle(ControlModle controlModle) {
        this.controlModle = controlModle;
    }

    public int getModleid() {
        return modleid;
    }

    public ModleDBServe getModleDBServe() {
        return modleDBServe;
    }

    public void setModleDBServe(ModleDBServe modleDBServe) {
        this.modleDBServe = modleDBServe;
    }
}

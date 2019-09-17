package com.sankuai.inf.leaf.segment.model;

import java.util.concurrent.atomic.AtomicLong;

public class Segment {
    private AtomicLong value = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private SegmentBuffer buffer;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public long getValue() {
        return this.value.get();
    }

//    public AtomicLong getValue() {
//        return this.value;
//    }

    public long nextValue(long delta, boolean odd) {
        long v = this.value.addAndGet(delta);
        return (v % 2) * 2 + (odd ? 1 : 0);
    }

    public void updateValue(long newValue) {
        this.value.set(newValue);
    }

//    public void setValue(AtomicLong value) {
//        this.value = value;
//    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public SegmentBuffer getBuffer() {
        return buffer;
    }

    public long getIdle() {
        return this.getMax() - this.value.get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment(");
        sb.append("value:");
        sb.append(value);
        sb.append(",max:");
        sb.append(max);
        sb.append(",step:");
        sb.append(step);
        sb.append(")");
        return sb.toString();
    }
}

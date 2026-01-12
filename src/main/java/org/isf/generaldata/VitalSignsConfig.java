package org.isf.generaldata;

public class VitalSignsConfig {

    public final int min;
    public final int max;
    public final int init;
    public final double step;

    public VitalSignsConfig(int min, int max, int init, double step) {
        this.min = min;
        this.max = max;
        this.init = init;
        this.step = step;
    }
}

// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.utils;

public class TruyNaInput {
    private String target;
    private String priceType;
    private double priceValue;
    private int step = 1;

    public TruyNaInput(String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public String getPriceType() {
        return this.priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public double getPriceValue() {
        return this.priceValue;
    }

    public void setPriceValue(double priceValue) {
        this.priceValue = priceValue;
    }

    public int getStep() {
        return this.step;
    }

    public void nextStep() {
        ++this.step;
    }
}

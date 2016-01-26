package shapeabstraction;

import simplegui.SimpleGUI;

public class Pixel {

    double x;
    double y;
    private double sigVal;
    private boolean display = true;
    private Pixel prevDisp; // the previous and next pixels that are displayed
    private Pixel nextDisp;

    public Pixel(int x, int y, Pixel p) {
        this.x = x;
        this.y = y;
        prevDisp = p;
        if (p == null)//if we are constructing the first pixel in the Pixels array, then it sets the prevDisp Pixel to itself
        {
            prevDisp = this;//until it gets changed once all of the pixels are read
        } else {
            p.nextDisp = this;
        }
    }

    public void show() {
        display = true;
    }

    public void erase() {
        int x = 1;
        prevDisp.nextDisp = this.nextDisp;
        nextDisp.prevDisp = this.prevDisp;
        display = false;
    }

    public void setPrev(Pixel p) {
        prevDisp = p;
    }

    public void setNext(Pixel n) {
        nextDisp = n;
    }

    public Pixel getPrev() {
        return prevDisp;
    }

    public Pixel getNext() {
        return nextDisp;
    }

    public boolean getDisplay() {
        return display;
    }

    public double getSigVal() {
        return sigVal;
    }

    public void setSigVal(double x) {
        sigVal = x;
    }

    public void draw(SimpleGUI sg, Pixel next) {
        sg.drawLine(x, y, next.x, next.y);
    }

    public double computeSig(Pixel prev, Pixel next) {
        double a = Math.sqrt(Math.pow((x - prev.x), 2) + Math.pow((y - prev.y), 2));
        double b = Math.sqrt(Math.pow((x - next.x), 2) + Math.pow((y - next.y), 2));
        double c = Math.sqrt(Math.pow((prev.x - next.x), 2) + Math.pow((prev.y - next.y), 2));
        sigVal = (a + b) - c;
        return sigVal;
    }

    @Override
    public String toString() {
        return ("Visible: " + display + " x: " + x + ", y: " + y + ", Significance: " + sigVal);
    }
}

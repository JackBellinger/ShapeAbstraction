package shapeabstraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import simplegui.GUIListener;
import simplegui.SimpleGUI;

/**
 * Started: 9/2/2015 Last Edit: 10/1/2015
 *
 * @author Jackson
 */
public class ShapeAbstraction implements GUIListener {
    /*TODO:
     * 1. fix bug that sometimes causes pixels to reappear after having been deleted when moving the slider down (probably related to TODO 2.)
     * 2. get rid of resetPix and make it so that I don't have to reset every pixel when you change the slider
     */

    Pixel[] pixels = new Pixel[400]; //I'm using an array so that I can un-abstract aka put pixels back without reparsing the txt file
    SimpleGUI sg;
    boolean dots = false;

    public void start() throws IOException {
        sg = new SimpleGUI(500, 500);
        sg.registerToGUI(this);
        readPixels();
        checkSigVals();
        //slayLowSigs();
        drawPixels();
        System.out.println(pixels.length);
    }

    public void readPixels() throws IOException {//O(n)
        //creates the list of pixels by reading the two values from the txt
        try (BufferedReader br = new BufferedReader(new FileReader("deerPixels.txt"))) {
            String line = br.readLine();
            int index = 0;
            while (line != null) {
                int first = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                int second = 700 - Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                pixels[index] = new Pixel(first, second, (index == 0 ? null : pixels[index - 1]));
                //if(index > 1 && index < 399)
                //    pixels[index-1].computeSig(pixels[index - 2], pixels[index]);
                line = br.readLine();
                index++;
            }
            pixels[0].setPrev(pixels[pixels.length - 1]);//sets the first and last to link together, making a loop list
            pixels[pixels.length - 1].setNext(pixels[0]);
            pixels[0].setSigVal(Double.POSITIVE_INFINITY);//these two are set to infinity so that the first and last pixel are never deleted
            pixels[pixels.length - 1].setSigVal(Double.POSITIVE_INFINITY);
        }
    }

    public void checkSigVals() {
        for (int i = 1; i < pixels.length - 1; i++) {
            pixels[i].computeSig(pixels[i - 1], pixels[i + 1]);
        }
    }

    public void updateSigVals(int i) {//O(1)
        if (i != 0 && i != pixels.length) { //recomputes the sig values for the closest diplayed pixels on the left and right
            Pixel left = pixels[i].getPrev();
            Pixel right = pixels[i].getNext();
            left.computeSig(left.getPrev(), right);
            right.computeSig(left, right.getNext());
        }
        //these two are set to infinity so that the first and last pixel are never deleted
        pixels[0].setSigVal(Double.POSITIVE_INFINITY);
        pixels[pixels.length - 1].setSigVal(Double.POSITIVE_INFINITY);
    }

    public void slayLowSigs() {//O(n) + i * O(n)
        //figures out the lowest sigVal pixel and removes it until there are the right number of pixels
        //the number i goes to is the number of pixels that get removed, ie bigger i = fewer pixels
        //the number of pixels that arre ultimately diplayed ranges from 345 to 65
        resetPix();
        for (int i = 0; i < (sg.getSliderValue() * 2) + 165; i++) {
            int lowest = 1;
            for (int n = 1; n < pixels.length - 2; n++) {
                if (pixels[n].getSigVal() < pixels[lowest].getSigVal() && pixels[n].getDisplay()) {
                    lowest = n;
                }
            }
            //System.out.println(lowest);
            pixels[lowest].computeSig(pixels[lowest].getPrev(), pixels[lowest].getNext());
            pixels[lowest].erase();
            updateSigVals(lowest);
        }
    }

    public void drawPixels() {//O(n)
        sg.eraseAllDrawables();
        int prevIndex = -1;
        int numPix = 1;
        for (int i = 0; i < pixels.length - 1; i++) {
            if (pixels[i].getDisplay()) {
                if (dots) {
                    sg.drawDot(pixels[i].getNext().x, pixels[i].getNext().y, 3);
                    sg.drawDot(pixels[i].getPrev().x, pixels[i].getPrev().y, 3);
                }
                if (prevIndex == -1) {
                    prevIndex = i;
                } else {
                    pixels[prevIndex].draw(sg, pixels[i]);
                    prevIndex = i;
                    numPix++;
                }
            }
        }
    }

    private void resetPix() {//O(n)
        for (Pixel pixel : pixels) {
            pixel.show();
        }
    }

    private void printPix() {//O(n)
        for (Pixel pixel : pixels) {
            System.out.println(pixel);
        }
    }

    @Override
    public void reactToButton1() {

    }

    @Override
    public void reactToButton2() {

    }

    @Override
    public void reactToSwitch(boolean bln) {
        dots = bln;
    }

    @Override
    public void reactToSlider(int i) {
        slayLowSigs();
        drawPixels();
    }

}

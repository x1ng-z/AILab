package hs.ShockDetect;

import java.math.*;
/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/18 16:20
 */
public class Complex {
    private double realpart;
    private double imagepart;

    /**
     *复数求和
     * */
    public Complex add(Complex b) {
        Complex result = new Complex();
        result.realpart = realpart + b.realpart;
        result.imagepart = imagepart + b.imagepart;
        return result;
    }


    /**
     * 复数相减
     * */
    public Complex substruction(Complex b) {
        Complex result = new Complex();
        result.realpart = realpart - b.realpart;
        result.imagepart = imagepart - b.imagepart;
        return result;
    }


    /**
     * 求模
     * */
    public double modl(){
        return Math.sqrt(realpart*realpart+imagepart*imagepart);
    }


    /**
     * 欧拉公式转换为虚部
     * */
    //Ae^(xj)=j*A*sin(x)+A*cos(x)
    public static Complex Euler(double A,double x){
        Complex complex=new Complex();
        complex.realpart=A*Math.cos(x);
        complex.imagepart= A*Math.sin(x);
        return complex;
    }


    public double getRealpart() {
        return realpart;
    }

    public void setRealpart(double realpart) {
        this.realpart = realpart;
    }

    public double getImagepart() {
        return imagepart;
    }

    public void setImagepart(double imagepart) {
        this.imagepart = imagepart;
    }


}

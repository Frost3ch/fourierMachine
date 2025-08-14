package net.frosty.fouriermachine;

import org.joml.Vector2d;

import java.util.Arrays;

public class FourierComputation {

     public Double[][] DFT(Vector2d[] points, Float size){

         int N = points.length;
         Double[][] X = new Double[N][];
         int m = N/2;

         for (int k=-m;k<m;k++){
             double re = 0.0;
             double im = 0.0;

             for (int n=-m;n<m;n++){
                 try {
                     double theta = 2*Math.PI * k * n / (double)N;

                     double x_n = -size*points[n+m].x;
                     double y_n = -size*points[n+m].y;


                     re += x_n * Math.cos(theta) + y_n * Math.sin(theta);
                     im += -x_n * Math.sin(theta) + y_n * Math.cos(theta);

                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

             re = re / (double)N;
             im = im / (double)N;

             double freq = k;
             double amp = Math.hypot(re, im);
             double phase = Math.atan2(im, re);


             X[k+m] = new Double[] {re, im, freq, amp, phase};

         }
         Arrays.sort(X, (x, y)-> Double.compare(y[3],x[3]));
         return X;
     }
}

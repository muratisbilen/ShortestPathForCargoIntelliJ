
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;


public class TurkeyMap extends JPanel{
    BufferedImage img;
    String route;
    HashMap<String,String> coor;

    public TurkeyMap(){
        super();
    }

    public TurkeyMap(String imgfn, String route, HashMap<String,String> coor) throws Exception{
        this.img = ImageIO.read(new File(imgfn));
        this.route = route;
        this.coor = coor;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0,this);

        String[] r = route.split(" --> ");

        int sm = 5;
        int bg = 10;

        String cit0 = r[0];
        String cc0 = coor.get(cit0);
        String[] ccs0 = cc0.split(",");
        float y0 = Float.parseFloat(ccs0[0]);
        float x0 = Float.parseFloat(ccs0[1]);

        int y02 = (int)normy(y0);
        int x02 = (int)normx(x0);


        int xx = x02;
        int yy = y02;

        for(int i=1;i<r.length;i++){
            String cit = r[i];
            String cc = coor.get(cit);
            String[] ccs = cc.split(",");
            float y = Float.parseFloat(ccs[0]);
            float x = Float.parseFloat(ccs[1]);

            int y2 = (int)normy(y);
            int x2 = (int)normx(x);

            g.setColor(Color.BLACK);
            g.fillOval(x2-sm, y2-sm, 2*sm, 2*sm);
            g.drawString(cit, x2, y2-2*sm);

            g.setColor(Color.RED);
            g.drawLine(x2, y2, xx, yy);
            xx = x2;
            yy = y2;
        }

        g.setColor(Color.RED);
        g.drawLine(x02, y02, xx, yy);

        g.setColor(Color.BLUE);
        g.fillOval(x02-bg, y02-bg, 2*bg, 2*bg);
        g.setColor(Color.BLACK);

    }

    public static float normy(float e){
        float e2 = e;

        float b1 = 36;
        float b2 = 6;
        //float b3 = 427;
        float b3 = 410;
        //float b4 = 67;
        float b4 = 75;

        e2 = b4+b3*(e-b1)/b2;
        e2 = 560 - e2;

        return(e2);
    }

    public static float normx(float e){
        float e2 = e;

        float a1 = 26;
        float a2 = 19;
        float a3 = 985;
        float a4 = 60;

        e2 = a4+a3*(e-a1)/a2;
        return(e2);
    }
}

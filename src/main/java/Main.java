import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Main implements ItemListener, ActionListener{

    Country country = new Country();;
    static JTextArea ta = new JTextArea();
    static ArrayList<String> cityList = new ArrayList<>();
    static ArrayList<JCheckBox> ccb = new ArrayList<>();
    static HashMap<String,String> coor = null;
    JFrame fm;
    TurkeyMap tm;

    public Main() throws Exception {
        this.country = new Country();
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public static void main(String[] args) throws Exception {

        coor = getCoordinates("koordinatlar.txt");
        Set<String> cn0 = coor.keySet();
        ArrayList<String> cn = new ArrayList<>();
        cn.addAll(cn0);
        Collections.sort(cn);
        ta.setEditable(false);



        //Route r = getBestRoute("Istanbul","Kastamonu",allRoutes,c);
        //System.out.println(r.toString());


        JFrame fm0 = new JFrame();
        fm0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fm0.setSize(1000, 1000);
        fm0.setResizable(false);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JScrollPane sp2 = new JScrollPane(p2);
        sp2.setPreferredSize(new Dimension(500,500));

        fm0.add(p1);



        for(int i=0;i<cn.size();i++){
            JCheckBox cb = new JCheckBox(cn.get(i));
            cb.addItemListener(new Main());
            ccb.add(cb);
        }

        p2.setLayout(new GridLayout(ccb.size(),1));

        for(int i=0;i<ccb.size();i++){
            p2.add(ccb.get(i));
        }

        p1.setLayout(new BorderLayout());
        p1.add(new JLabel("Select cities to ship packages: "),BorderLayout.NORTH);
        p1.add(sp2,BorderLayout.WEST);
        p1.add(ta,BorderLayout.CENTER);

        JButton bt = new JButton("Find Shortest Route");
        bt.addActionListener(new Main());
        p1.add(bt,BorderLayout.SOUTH);

        fm0.setVisible(true);

    }

    public static Route getBestRoute(String cityName1, String cityName2, Map<String, Map<String, Route>> allRoutes, Country c) throws Exception {
        Route r = allRoutes.get(cityName1).get(cityName2);
        for (int i = 0; i < r.getCities().size(); i++) {
            boolean check = true;
            int whilecount = 0;
            while (check) {

                ArrayList<City> cities = r.getCities().get(i);
                int sum = 0;
                for (int j = 1; j < cities.size(); j++) {
                    Route checkroute = allRoutes.get(cities.get(j - 1).getName()).get(cities.get(j).getName());
                    int minindex = checkroute.getDist().indexOf(Collections.min(checkroute.getDist()));
                    int routedistance = checkroute.getDist().get(minindex);
                    sum += routedistance;
                }

                if (r.getDist().get(r.getDist().indexOf(Collections.min(r.getDist()))) == sum) {
                    check = false;
                } else {
                    ArrayList<City> finalcity = new ArrayList<>();
                    finalcity.add(c.getCities().get(cities.get(0).getName()));
                    int finaldist = 0;
                    for (int j = 1; j < cities.size(); j++) {
                        Route newroute = getBestRoute(cities.get(j - 1).getName(), cities.get(j).getName(), allRoutes, c);
                        int minindex = newroute.getDist().indexOf(Collections.min(newroute.getDist()));
                        ArrayList<City> newcity = newroute.getCities().get(minindex);
                        int newdistance = newroute.getDist().get(minindex);
                        finaldist += newdistance;

                        for (int h = 1; h < newcity.size(); h++) {
                            finalcity.add(newcity.get(h));
                        }
                    }

                    ArrayList<ArrayList<City>> r2cities = r.getCities();
                    r2cities.set(i, finalcity);
                    r.setCities(r2cities);
                    ArrayList<Integer> r2dist = r.getDist();
                    r2dist.set(i, finaldist);
                    r.setDist(r2dist);
                }
            }
        }

        return (r);
    }

    public static Route getBestRouteMini(String cityName1, String cityName2, Map<String, Map<String, Route>> allRoutes) {
        return (allRoutes.get(cityName1).get(cityName2));
    }

    public static HashMap<String,String> getCoordinates(String fn) throws Exception{
        HashMap<String,String> coor = new HashMap<>();
        BufferedReader bf = new BufferedReader(new FileReader(fn));
        String s = bf.readLine();
        while((s=bf.readLine())!=null){
            String[] line = s.split("\t");
            coor.put(line[0], line[1]+","+line[2]);
        }

        return(coor);
    }

    public void itemStateChanged(ItemEvent e){
        JCheckBox cb = (JCheckBox)e.getItem();
        String city = cb.getText();
        String tatext = ta.getText();

        if(cb.isSelected()){
            if(!tatext.contains(city)){
                ta.setText(tatext+city+"\n");
            }
        }else{
            String tatext2 = tatext;
            tatext2 = tatext2.replaceFirst(city+"\n","");
            ta.setText(tatext2);
        }
    }

    public void actionPerformed(ActionEvent e){
        cityList = new ArrayList<>();
        for(int i=0;i<ccb.size();i++){
            if(ccb.get(i).isSelected()){
                cityList.add(ccb.get(i).getText());
            }
        }

        FloydWarshall fw = new FloydWarshall();
        Map<String, Map<String, Route>> allRoutes = null;
        Best5Route bestlist = null;

        try{
            allRoutes = fw.floydWarshall(country.getMat());
            bestlist = BestRoute.bestRoute("Kocaeli", cityList, allRoutes, country);
        }catch(Exception ex){

        }


        ArrayList<String> bestlist2 = bestlist.getCities().get(0);
        String finalRoute = "Kocaeli";
        bestlist2.add(0,"Kocaeli");
        bestlist2.add("Kocaeli");

        for (int j = 1; j < bestlist2.size(); j++) {

            Route r2 = null;
            try{
                r2 = getBestRoute(bestlist2.get(j - 1), bestlist2.get(j), allRoutes, country);
            }catch(Exception ex2){

            }

            String parroute = r2.toString2();
            String[] parr = parroute.split(" --> ");


            for(int i=1;i<parr.length;i++){
                finalRoute += " --> " + parr[i];
            }
        }

        fm = new JFrame();
        tm = null;
        try{
            tm = new TurkeyMap("turkiye.png",finalRoute,coor);
        }catch(Exception ex3){

        }
        fm.add(tm);
        fm.setSize(1100, 623);
        fm.setVisible(true);
        fm.setResizable(false);
    }
}

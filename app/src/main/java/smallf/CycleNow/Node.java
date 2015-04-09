package smallf.CycleNow;

//Created by smallF on 2015/3/18.


public class Node {
    private String type;
    private double lon;
    private double lat;
    private float ele;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLon(double lon){
        this.lon = lon;
        return;
    }

    public double getLon(){
        return this.lon;
    }

    public void setLat(double lat){
        this.lat = lat;
        return;
    }

    public double getLat(){
        return this.lat;
    }

    public void setEle(float ele){
        this.ele = ele;
        return;
    }

    public float getEle(){
        return this.ele;
    }

    public Node(){

    }

    public Node(double _lon, double _lat, float _ele){
        this.lon = _lon;
        this.lat = _lat;
        this.ele = _ele;
    }

}

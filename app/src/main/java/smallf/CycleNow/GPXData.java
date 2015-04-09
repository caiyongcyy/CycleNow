package smallf.CycleNow;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.github.mikephil.charting.data.Entry;



public class GPXData {
    public GPXData() {

    }

    public static ArrayList<Node> Gpxparser(BufferedReader in) {

        ArrayList<Node> nodes = new ArrayList<Node>();

        Node geo = new Node();

        try {

            XmlPullParser xpp = Xml.newPullParser();
            //gpxReader xpp = new gpxReader();
            int eventType = xpp.getEventType();

            xpp.setInput(in);
            //xpp.setXML(in);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG :
                        tagName = xpp.getName();

                        /*if (tagName.equals("wpt")) {
                            geo = new Node();
                            geo.setType("wpt");
                            double lat = Double.parseDouble(xpp.getAttributeValue(null, "lat"));
                            geo.setLat(lat);
                            double lon = Double.parseDouble(xpp.getAttributeValue(null, "lon"));
                            geo.setLon(lon);

                            xpp.nextTag();

                            if (xpp.getName().equals("name")) {
                                xpp.next();
                                String name = xpp.getText();
                                geo.setName(name);
                            }

                            nodes.add(geo);
                            Log.i("log2", lat + "," + lon);
                        }*/


                        if (tagName != null && tagName.equals("trkpt")) {
                            geo = new Node();
                            double lat = Double.parseDouble(xpp.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(xpp.getAttributeValue(null, "lon"));
                            geo.setType("trkpt");
                            geo.setLat(lat);
                            geo.setLon(lon);
                        }
                        if (tagName != null && tagName.equals("ele")) {
                            String strEle = null;
                            float ele = 0.0f;
                            try {
                                strEle = xpp.nextText();
                                ele = Float.valueOf(strEle);
                                geo.setEle(ele);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                    case XmlPullParser.END_TAG: {
                        if (xpp.getName().equals("trkpt")) {
                            nodes.add(geo);
                        }
                    }
                    break;
                    case XmlPullParser.TEXT:
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;

    }
}

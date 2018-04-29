package no.teacherspet.tring.util;

import java.util.Comparator;

import connection.Event;

/**
 * Created by magnus on 27.04.2018.
 */

public class EventComparator implements Comparator<Event> {

    private static String PROPERTY_TYPE;
    private boolean reverse;

    public EventComparator(String property, boolean reverse) {
        PROPERTY_TYPE = property;
        this.reverse = reverse;
    }

    @Override
    public int compare(Event e1, Event e2) {
        switch (PROPERTY_TYPE) {
            case "event_name":
                if (!reverse)
                    return e1.getProperty(PROPERTY_TYPE).compareToIgnoreCase(e2.getProperty(PROPERTY_TYPE));
                else
                    return e2.getProperty(PROPERTY_TYPE).compareToIgnoreCase(e1.getProperty(PROPERTY_TYPE));
            case "popularity":
                if (!reverse)
                    return Integer.parseInt(e1.getProperty(PROPERTY_TYPE)) - Integer.parseInt(e2.getProperty(PROPERTY_TYPE));
                else
                    return Integer.parseInt(e2.getProperty(PROPERTY_TYPE)) - Integer.parseInt(e1.getProperty(PROPERTY_TYPE));
            case "avg_score":
                if (!reverse)
                    return Float.compare(Float.parseFloat(e1.getProperty(PROPERTY_TYPE)), Float.parseFloat(e2.getProperty(PROPERTY_TYPE)));
                else
                    return Float.compare(Float.parseFloat(e1.getProperty(PROPERTY_TYPE)), Float.parseFloat(e2.getProperty(PROPERTY_TYPE)));
            case "avg_time":
                String[] data1 = e1.getProperty(PROPERTY_TYPE).split(":");
                String[] data2 = e2.getProperty(PROPERTY_TYPE).split(":");
                int time1 = Integer.parseInt(data1[0]) * 60 * 60 + Integer.parseInt(data1[1]) * 60 + Integer.parseInt(data1[2]);
                int time2 = Integer.parseInt(data2[0]) * 60 * 60 + Integer.parseInt(data2[1]) * 60 + Integer.parseInt(data2[2]);
                if (!reverse) return time1 - time2;
                else return time2 - time1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}

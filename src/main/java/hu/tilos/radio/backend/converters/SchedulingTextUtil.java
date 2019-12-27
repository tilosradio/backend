package hu.tilos.radio.backend.converters;

import hu.tilos.radio.backend.scheduling.SchedulingSimple;

/**
 * Create textual representation of a scheduling.
 */
public class SchedulingTextUtil {

    public String create(SchedulingSimple scheduling) {
        StringBuilder response = new StringBuilder();
        if (scheduling.getWeekType() == 2) {
            response.append("minden második ");
        }
        String days[] = new String[]{"hétfő", "kedd", "szerda", "csütörtök", "péntek", "szombat", "vasárnap"};

        response.append(days[scheduling.getWeekDay()]);
        int to = scheduling.getHourFrom() * 60 + scheduling.getMinFrom() + scheduling.getDuration();
        int toMin = to % 60;
        int toHour = (to - toMin) / 60;
        if (toHour >= 24) {
            toHour -= 24;
        }
        response.append(String.format(" %d:%02d-%d:%02d", scheduling.getHourFrom(), scheduling.getMinFrom(), toHour, toMin));
        return response.toString();
    }
}

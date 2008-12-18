package com.shade.controls;

public class DayPhaseTimer {

    public static final float TRANSITION_TIME = 1f / 7;
    public static final float MAX_SHADOW = 0.4f;

    int secondsPerDay;
    int timeofday;
    int totaltime;
    DayLightStatus daylight;

    public enum DayLightStatus {
        DAWN, DAY, DUSK, NIGHT
    }

    public DayPhaseTimer(int seconds) {
        secondsPerDay = seconds;
        timeofday = 0;
        totaltime = 0;
    }

    public DayLightStatus getDaylightStatus() {
        return daylight;
    }
    
    public float timeLeft(){
    	int timeofday = totaltime % (secondsPerDay/2);
        if(daylight == DayLightStatus.DUSK || daylight == DayLightStatus.DAWN){
        	return (timeofday-secondsPerDay * (1f / 2 - TRANSITION_TIME))/(0.5f*secondsPerDay*TRANSITION_TIME);
        }
        else return timeofday/(0.5f*secondsPerDay*(1-TRANSITION_TIME));
    }

    public void update(int delta) {
        totaltime += delta;
        int timeofday = totaltime % secondsPerDay;
        // is it day or night?
        if (timeofday > 1.0 * secondsPerDay * (1f / 2 - TRANSITION_TIME)) {
            daylight = DayLightStatus.NIGHT;
            if (timeofday < 1.0 * secondsPerDay / 2) {
                daylight = DayLightStatus.DUSK;
            }
            if (timeofday > 1.0 * secondsPerDay * (1 - TRANSITION_TIME)) {
                daylight = DayLightStatus.DAWN;
            }
        }
        else
        daylight = DayLightStatus.DAY;
    }

}

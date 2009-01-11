package com.shade.controls;

public class DayPhaseTimer {

    public static final float TRANSITION_TIME = 1f / 4;
    public static final float MAX_SHADOW = 0.4f;

    private int secondsPerDay;
    private int timeOfDay;
    private int startTime, runningTime;
    DayLightStatus daylight;

    public enum DayLightStatus {
        DAWN, DAY, DUSK, NIGHT
    }

    public DayPhaseTimer(int seconds) {
    	startTime = 0;
    	runningTime = 0;
        secondsPerDay = seconds;
        timeOfDay = 0;
    }

    public DayLightStatus getDaylightStatus() {
        return daylight;
    }
    
    public float timeLeft(){
    	timeOfDay = runningTime % (secondsPerDay/2);
        if(daylight == DayLightStatus.DUSK || daylight == DayLightStatus.DAWN){
        	return (timeOfDay-secondsPerDay * (1f / 2 - TRANSITION_TIME))/(secondsPerDay*TRANSITION_TIME);
        }
        else return timeOfDay/(0.5f*secondsPerDay*(1-TRANSITION_TIME));
    }

    public void update(int delta) {
        runningTime += delta;
        int timeofday = (runningTime-startTime) % secondsPerDay;
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
    
    public void reset(){

        runningTime = 0;
    	startTime = 0;
    }

}

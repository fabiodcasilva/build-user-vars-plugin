package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.TimerTrigger.TimerTriggerCause;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class TimerTriggerCauseDeterminant implements IUsernameSettable<TimerTriggerCause> {

    private static final String TIMER_TRIGGER_DUMMY_USER_NAME = "Timer Trigger";
    private static final String TIMER_TRIGGER_DUMMY_USER_ID = "timer";

    @Override
    public boolean setJenkinsUserBuildVars(TimerTriggerCause cause, Map<String, String> variables) {
        if (cause != null) {
            UsernameUtils.setUsernameVars(TIMER_TRIGGER_DUMMY_USER_NAME, variables);
            variables.put(BUILD_USER_ID, TIMER_TRIGGER_DUMMY_USER_ID);

            return true;
        }
        return false;
    }

    @Override
    public Class<TimerTriggerCause> getUsedCauseClass() {
        return TimerTriggerCause.class;
    }
}

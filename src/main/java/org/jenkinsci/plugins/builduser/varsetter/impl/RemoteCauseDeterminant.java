package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class RemoteCauseDeterminant implements IUsernameSettable<Cause.RemoteCause> {

    private static final String REMOTE_CAUSE_DUMMY_BUILD_USER_ID = "remoteRequest";

    @Override
    public boolean setJenkinsUserBuildVars(Cause.RemoteCause cause, Map<String, String> variables) {
        //As of Jenkins 2.51 remote cause is set the build was triggered using token and real user is not set
        if (cause != null) {
            UsernameUtils.setUsernameVars(String.format("%s %s", cause.getAddr(), cause.getNote()), variables);
            variables.put(BUILD_USER_ID, REMOTE_CAUSE_DUMMY_BUILD_USER_ID);

            return true;
        }
        return false;
    }

    @Override
    public Class<Cause.RemoteCause> getUsedCauseClass() {
        return Cause.RemoteCause.class;
    }
}

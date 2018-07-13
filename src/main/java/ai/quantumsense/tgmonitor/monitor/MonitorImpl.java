package ai.quantumsense.tgmonitor.monitor;

import ai.quantumsense.tgmonitor.monitor.entities.PeersUpdater;

import java.util.HashSet;
import java.util.Set;

public class MonitorImpl implements Monitor, PeersUpdater {

    private Telegram tg;
    private String phoneNumber = null;
    private boolean loggedIn = false;

    private static Monitor instance = null;

    public MonitorImpl(Telegram tg) {
        this.tg = tg;
        instance = this;
    }

    static Monitor get() {
        return instance;
    }

    @Override
    public void login(String phoneNumber) {
        if (loggedIn)
            throw new RuntimeException("Attempting to log in, but already logged in");
        tg.login(phoneNumber);
        this.phoneNumber = phoneNumber;
        loggedIn = true;
    }

    @Override
    public void logout() {
        if (!loggedIn)
            throw new RuntimeException("Attempting to log out, but already logged out");
        tg.logout();
        phoneNumber = null;
        loggedIn = false;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void peersChanged(Set<String> newSet) {
        Set<String> currentSet = tg.getMonitors();
        for (String p : getPeersToStop(currentSet, newSet))
            tg.stop(p);
        for (String p : getPeersToStart(currentSet, newSet))
            tg.start(p);
    }

    Set<String> getPeersToStop(Set<String> oldSet, Set<String> newSet) {
        Set<String> s = new HashSet<>(oldSet);
        s.removeAll(newSet);
        return s;
    }

    Set<String> getPeersToStart(Set<String> oldSet, Set<String> newSet) {
        Set<String> s = new HashSet<>(newSet);
        s.removeAll(oldSet);
        return s;
    }
}
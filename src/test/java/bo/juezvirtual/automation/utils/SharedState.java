package bo.juezvirtual.automation.utils;

public final class SharedState {
    private static String latestRegisteredNickname;
    private static String contestParticipantAlias;
    private static String latestPrivateContestTitle;
    private static String latestPublicContestTitle;

    private SharedState() {
    }

    public static synchronized String getLatestRegisteredNickname() {
        return latestRegisteredNickname;
    }

    public static synchronized void setLatestRegisteredNickname(String nickname) {
        latestRegisteredNickname = nickname;
    }

    public static synchronized String getContestParticipantAlias() {
        return contestParticipantAlias;
    }

    public static synchronized void setContestParticipantAlias(String alias) {
        contestParticipantAlias = alias;
    }

    public static synchronized void clearContestParticipantAlias() {
        contestParticipantAlias = null;
    }

    public static synchronized String getLatestPrivateContestTitle() {
        return latestPrivateContestTitle;
    }

    public static synchronized void setLatestPrivateContestTitle(String title) {
        latestPrivateContestTitle = title;
    }

    public static synchronized String getLatestPublicContestTitle() {
        return latestPublicContestTitle;
    }

    public static synchronized void setLatestPublicContestTitle(String title) {
        latestPublicContestTitle = title;
    }

    public static synchronized void clearContestTitles() {
        latestPrivateContestTitle = null;
        latestPublicContestTitle = null;
    }
}

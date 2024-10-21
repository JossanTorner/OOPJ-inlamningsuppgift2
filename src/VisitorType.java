public enum VisitorType {

    ACTIVE_MEMBER ("Active member"),
    NON_ACTIVE_MEMBER ("Non active member"),
    NON_MEMBER ("Not a member");

    public final String type;

    VisitorType(String type) {
        this.type = type;
    }
}

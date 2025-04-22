class ScheduleEntry {
    public int vertex;
    public boolean requiresBackpack;

    public ScheduleEntry(int vertex, boolean requiresBackpack) {
        this.vertex = vertex;
        this.requiresBackpack = requiresBackpack;
    }
}
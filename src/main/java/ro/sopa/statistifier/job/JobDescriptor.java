package ro.sopa.statistifier.job;

import java.util.Objects;

public class JobDescriptor {

    private JobType type;
    private String reference;

    public JobDescriptor(JobType type, String reference) {
        this.type = type;
        this.reference = reference;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDescriptor that = (JobDescriptor) o;
        return type == that.type && Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, reference);
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", reference='" + reference + '\'' +
                '}';
    }
}

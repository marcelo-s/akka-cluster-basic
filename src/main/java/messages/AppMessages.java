package messages;

import java.io.Serializable;

public interface AppMessages {

    public static final String BACKEND_REGISTRATION = "BackendRegistration";

    public static class JobMessage implements Serializable {
        private String payload;

        public JobMessage(String payload) {
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

    public static class ResultMessage implements Serializable {
        private String text;

        public ResultMessage(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "TransformationResult(" + text + ")";
        }
    }

    public static class FailedMessage implements Serializable {
        private String reason;
        private JobMessage job;

        public FailedMessage(String reason, JobMessage job) {
            this.reason = reason;
            this.job = job;
        }

        @Override
        public String toString() {
            return String.format("FAILED JOB: %s, REASON : %s ", this.job, this.reason);
        }
    }
}

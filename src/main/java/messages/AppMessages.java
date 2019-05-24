package messages;

import java.io.Serializable;

public interface AppMessages {

    String BACKEND_REGISTRATION = "BackendRegistration";

    class JobMessage implements Serializable {
        private String payload;

        public JobMessage(String payload) {
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }

    }

    class ResultMessage implements Serializable {
        private String text;

        public ResultMessage(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "TransformationResult(" + text + ")";
        }
    }

    class FailedMessage implements Serializable {
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

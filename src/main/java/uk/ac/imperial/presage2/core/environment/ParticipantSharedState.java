package uk.ac.imperial.presage2.core.environment;

import java.util.UUID;

/**
 *
 */
public class ParticipantSharedState<T> extends SharedState<T> {

    protected UUID participantID;

    public ParticipantSharedState(String type, T value, UUID participantID) {
        super(type, value);
        this.participantID = participantID;
    }

}

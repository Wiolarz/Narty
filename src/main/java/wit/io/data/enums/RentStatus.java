package wit.io.data.enums;

public enum RentStatus {
    /**
     * represents the rented state: not returned yet or customer has reserved the skis in advance
     */
    ACTIVE,

    /**
     * represents the returned state: customer has returned the skis
     */
    RETURNED,
    /**
     * represents a special case, when a rental cannot start at scheduled start date
     */
    FAILED,

    /**
     * represents the state when Skis were not returned on scheduled time
     */
    OVERDUE;
}

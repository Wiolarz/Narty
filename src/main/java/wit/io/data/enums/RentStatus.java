package wit.io.data.enums;

public enum RentStatus {
    ACTIVE, // rented, not returned yet or customer has reserved the skis in advance
    RETURNED, // customer has returned the skis
    FAILED; // special case, when one user has NOT returned the skis in time and another user has reserved them
}

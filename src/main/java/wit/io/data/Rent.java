package wit.io.data;

import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Class representing one rental record, contains information about ski equipment rentals.
 * implements Writeable
 */
public class Rent implements Writeable {
    private final UUID rentID; // PK

    private final String skiModel;
    private final String docID;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate updatedEndDate;
    private final String comment;
    private final RentStatus status;


    /**
     * constructor for Rent
     * @param uuid Rentals unique identifier, not required, will be generated if equal null, has to be unique
     * @param startDate Rental's start date, required argument
     * @param endDate Rental's planned end date, required argument
     * @param updatedEndDate Rental's real end date, is automatically updated daily, until status is changed to RETURNED, optional
     * @param skiModel Rental's model of ski equipment, required argument
     * @param clientID The ID of the person renting, required argument
     * @param comment Optional notes about the rental, optional argument
     * @param status Current rental status, optional argument
     * @throws IllegalArgumentException if required parameters are null
     */
    public Rent(UUID uuid, LocalDate startDate, LocalDate endDate, LocalDate updatedEndDate, String skiModel, String clientID, String comment, RentStatus status) {
        if (Util.isAnyArgumentNull(startDate, endDate, skiModel, clientID)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.rentID = (uuid == null) ? UUID.randomUUID() : uuid;
        this.startDate = startDate;
        this.comment = (comment == null) ? "" : comment;
        this.endDate = endDate;
        this.skiModel = skiModel;
        this.docID = clientID;
        // status can be null, when initializing new Rent and will be set while adding new object
        // but status can be set when loading previously saved object
        this.status = status;
        this.updatedEndDate = updatedEndDate == null ? endDate : updatedEndDate;
    }


    /**
     * Generates a hash code for the Rent object based on unique rentID.
     * @return The hash code of the rentID string.
     */
    @Override
    public int hashCode() {
        return rentID.hashCode();
    }


    /**
     * Returns a string representation of the Rent object.
     * @return A string containing all the Rentals details.
     */
    @Override
    public String toString() {
        return "Rent{" +
                "rentID=" + rentID +
                "skiModel=" + skiModel +
                ", clientID=" + docID +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", updatedEndDate=" + updatedEndDate +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Compares two Rentals.
     * Two Rents are considered equal if they have the same rentID.
     * @param o The object to compare with this Rent.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!o.getClass().equals(getClass())){
            return false;
        }

        return ((Rent) o).hashCode() == (this.hashCode());
    }

    /**
     * Writes the rent's data to a binary stream.
     * The data is written in the order: rentID, skiModel, docID, startDate, endDate, updatedEndDate, comment, status
     * dates are saved in the format specified in Const.java
     * @see wit.io.utils.Const
     * @param output The DataOutputStream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(rentID.toString());
        output.writeUTF(skiModel);
        output.writeUTF(docID);
        output.writeUTF(Util.dateToString(startDate));
        output.writeUTF(Util.dateToString(endDate));
        output.writeUTF(Util.dateToString(updatedEndDate));
        output.writeUTF(comment);
        output.writeUTF(status.name());
    }

    /**
     * Reads rental data from a binary stream and creates a new Rent object.
     * The data is read in the order: rentID, skiModel, docID, startDate, endDate, updatedEndDate, comment, status
     * @param input The DataInputStream to read the data from.
     * @return A new Rent object populated with the data from the stream.
     * @throws IOException if an I/O error occurs or if the end of the stream is reached before the object is populated.
     */
    public static Rent readData(DataInputStream input) throws IOException {
        UUID rentID = UUID.fromString(input.readUTF());
        String skiModel = input.readUTF();
        String docId = input.readUTF();
        LocalDate startDate, endDate, updatedEndDate;
        try {
            startDate = Util.stringToDate(input.readUTF());
            endDate = Util.stringToDate(input.readUTF());
            updatedEndDate = Util.stringToDate(input.readUTF());
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
        String comment = input.readUTF();
        RentStatus rentStatus = RentStatus.valueOf(input.readUTF());

        return new Rent(rentID, startDate, endDate, updatedEndDate, skiModel, docId, comment, rentStatus);
    }

    /**
     * Creates a new Rent object with the specified status update.
     * @param newStatus New status to apply to the rental
     * @return New Rent instance with updated status
     */
    public Rent setStatus(RentStatus newStatus) {
        return new Rent(rentID, startDate, endDate, updatedEndDate, skiModel, docID, comment, newStatus);
    }

    /**
     * Creates a new Rent object with the specified updated end date.
     * @param newUpdatedEndDate New end date for the rental
     * @return New Rent instance with updated end date
     */
    public Rent setUpdatedEndDate(LocalDate newUpdatedEndDate) {
        return new Rent(rentID, startDate, endDate, newUpdatedEndDate, skiModel, docID, comment, status);
    }

    /**
     * Gets the unique identifier for this rental record
     * @return UUID representing this Rent
     */
    public UUID getRentID() {
        return rentID;
    }

    /**
     * Gets the current status of the rental
     * @return RentStatus Current rent status
     */
    public RentStatus getStatus() {
        return status;
    }

    /**
     * Gets the start date of the rental period.
     * @return LocalDate representing the rental start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the original planned end date of the rental period
     * @return LocalDate representing the scheduled end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the updated end date if the rental period was OVERDUE.
     * @return LocalDate updatedEndDate representing the updated end date
     */
    public LocalDate getUpdatedEndDate() {
        return updatedEndDate;
    }

    /**
     * Gets the model of the rented ski.
     * @return String skiModel
     */
    public String getSkiModel() {
        return skiModel;
    }

    /**
     * Gets the id of the renting client.
     * @return Client identification string
     */
    public String getClientID() {
        return docID;
    }

    /**
     * Gets the optional notes about the rental.
     * @return Comment text or empty string if none exists
     */
    public String getComment() {
        return comment;
    }
}

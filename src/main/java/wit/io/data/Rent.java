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

public class Rent implements Writeable {
    private final UUID rentID; // PK

    private final String skiModel;
    private final String docID;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate updatedEndDate;
    private final String comment;
    private final RentStatus status;

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

    @Override
    public int hashCode() {
        return rentID.hashCode();
    }

    @Override
    public String toString() {
        return "Rent{" +
                "rentID=" + rentID +
                "skiModel=" + skiModel +
                ", clientID=" + docID +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                '}';
    }

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

    public Rent setStatus(RentStatus newStatus) {
        return new Rent(rentID, startDate, endDate, updatedEndDate, skiModel, docID, comment, newStatus);
    }

    public Rent setUpdatedEndDate(LocalDate newUpdatedEndDate) {
        return new Rent(rentID, startDate, endDate, newUpdatedEndDate, skiModel, docID, comment, status);
    }

    public UUID getRentID() {
        return rentID;
    }

    public RentStatus getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getUpdatedEndDate() {
        return updatedEndDate;
    }

    public String getSkiModel() {
        return skiModel;
    }

    public String getClientID() {
        return docID;
    }

    public String getComment() {
        return comment;
    }
}

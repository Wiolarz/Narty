package wit.io.data;

import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Rent implements Writeable {
    private final Integer rentID;
    private final Integer skiID;
    private final Integer docID;
    private final Date startDate;
    private final Date endDate;
    private final Date updatedEndDate;
    private final String comment;
    private final RentStatus status;

    public Rent(Date startDate, Date endDate, Date updatedEndDate, Integer skiID, Integer clientID, String comment, RentStatus status) {
        if (Util.isAnyArgumentNull(startDate, endDate, skiID, clientID)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.rentID = hashCode();
        this.startDate = startDate;
        this.comment = (comment == null) ? "" : comment;
        this.endDate = endDate;
        this.skiID = skiID;
        this.docID = clientID;
        // status can be null, when initializing new Rent and will be set while adding new object
        // but status can be set when loading previously saved object
        this.status = status;
        this.updatedEndDate = updatedEndDate == null ? endDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, skiID, docID, comment, status);
    }

    @Override
    public String toString() {
        return "Rent{" +
                "rentID=" + rentID +
                "skiID=" + skiID +
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

        return ((Rent) o).hashCode() == (o.hashCode());
    }

    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(String.valueOf(skiID));
        output.writeUTF(String.valueOf(docID));
        output.writeUTF(Util.dateToString(startDate));
        output.writeUTF(Util.dateToString(endDate));
        output.writeUTF(comment);
        output.writeUTF(status.name());
    }

    public static Rent readData(DataInputStream input) throws IOException {
        Integer skiID = input.readInt();
        Integer clientID = input.readInt();
        Date startDate, endDate;
        try {
            startDate = Util.stringToDate(input.readUTF());
            endDate = Util.stringToDate(input.readUTF());
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
        String comment = input.readUTF();
        RentStatus rentStatus = RentStatus.valueOf(input.readUTF());

        return new Rent(startDate, endDate, skiID, clientID, comment, rentStatus);
    }

    public Rent setStatus(RentStatus newStatus) {
        return new Rent(startDate, endDate, updatedEndDate, skiID, docID, comment, newStatus);
    }

    public Rent setUpdatedEndDate(RentStatus newUpdatedEndDate) {
        return new Rent(startDate, endDate, newUpdatedEndDate, skiID, docID, comment, status);
    }

    public Integer getRentID() {
        return rentID;
    }

    public RentStatus getStatus() {
        return status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getUpdatedEndDate() {
        return updatedEndDate;
    }

    public Integer getSkiID() {
        return skiID;
    }

    public Integer getClientID() {
        return docID;
    }

    public String getComment() {
        return comment;
    }
}

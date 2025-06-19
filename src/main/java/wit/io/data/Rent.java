package wit.io.data;

import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Rent implements Writeable {
    private final Integer rentID;
    private final Integer skiID;
    private final Integer clientID;
    private final Date startDate;
    private final Date endDate;
    private final String comment;
    private final RentStatus status;

    public Rent(Date startDate, Date endDate, Integer skiID, Integer clientID, String comments, RentStatus status) throws IOException {
        if (Util.isAnyArgumentNull(endDate, skiID, clientID)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.rentID = hashCode();
        // TODO: być może w managerze lepiej to robić. Btw czemu toString()?
        this.startDate = (startDate == null) ? Util.stringToDate(LocalDate.now().toString()) : startDate;
        this.comment = (comments == null) ? "" : comments;
        this.endDate = endDate;
        this.skiID = skiID;
        this.clientID = clientID;
        // status can be null, when initializing new Rent and will be set while adding new object
        // but status can be set when loading previously saved object
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, skiID, clientID, comment, status);
    }

    @Override
    public String toString() {
        return "Rent{" +
                "rentID=" + rentID +
                "skiID=" + skiID +
                ", clientID=" + clientID +
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
        output.writeUTF(String.valueOf(clientID));
        output.writeUTF(Util.dateToString(startDate));
        output.writeUTF(Util.dateToString(endDate));
        output.writeUTF(comment);
        output.writeUTF(status.name());
    }

    public static Rent readData(DataInputStream input) throws IOException {
        Integer skiID = input.readInt();
        Integer clientID = input.readInt();
        Date startDate = Util.stringToDate(input.readUTF());
        Date endDate = Util.stringToDate(input.readUTF());
        String comment = input.readUTF();
        RentStatus rentStatus = RentStatus.valueOf(input.readUTF());

        return new Rent(startDate, endDate, skiID, clientID, comment, rentStatus);
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

    public Integer getSkiID() {
        return skiID;
    }

    public Integer getClientID() {
        return clientID;
    }

    public String getComment() {
        return comment;
    }
}

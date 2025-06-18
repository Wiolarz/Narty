package wit.io.data;

import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Rent {
    private final Integer rentID;
    private final Integer skiID;
    private final Integer clientID;
    private final Date startDate;
    private final Date endDate;
    private final String comment;
    private final RentStatus status;

    public Rent(Integer rentID, Date startDate, Date endDate, Integer skiID, Integer clientID, String comments, RentStatus status) throws Exception {
        if (Util.isAnyArgumentNull(rentID, endDate, skiID, clientID, status)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.rentID = rentID;
        this.startDate = (startDate == null) ? Util.stringToDate(LocalDate.now().toString()) : startDate;
        this.comment = (comments == null) ? "" : comments;
        this.endDate = endDate;
        this.skiID = skiID;
        this.clientID = clientID;
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rentID, startDate, endDate, skiID, clientID, comment, status);
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
        if (obj == null) {
            return false;
        }

        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((Rent) obj).hashCode() == (obj.hashCode());
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

package rentme.dim.com.rentme.Activity.Data;

public class Requests {
    private String requestId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userId;
    private String startingPoint;
    private String endingPoint;
    private String rentCost;
    private String carType;
    private String requestDate;

    public Requests(String requestId, String userName, String userEmail, String userPhone, String userId, String startingPoint, String endingPoint, String rentCost, String carType, String requestDate) {
        this.requestId = requestId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userId = userId;
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.rentCost = rentCost;
        this.carType = carType;
        this.requestDate = requestDate;
    }

    public Requests() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }

    public String getRentCost() {
        return rentCost;
    }

    public void setRentCost(String rentCost) {
        this.rentCost = rentCost;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
}


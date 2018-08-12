package rentme.dim.com.rentme.Activity.Data;

public class SignInInfo {
    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userImageAngle;

    public SignInInfo(String userId, String userPassword, String userName, String userEmail, String userImageAngle) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImageAngle = userImageAngle;
    }
    public SignInInfo(){}

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImageAngle() {
        return userImageAngle;
    }

    public void setUserImageAngle(String userImageAngle) {
        this.userImageAngle = userImageAngle;
    }
}

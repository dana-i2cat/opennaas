package net.i2cat.mantychore.engine.model;

public class UserAccount {

    protected String userName;
    protected String password;
    protected String privilegedUser;
    protected String privilegedPassword;
    protected String routerConfigured;
    protected String smtpServer;
    protected String smtpServerPort;
    @Email
    protected String emailUser;
    protected String emailPassword;


    public String getUserName() {
        return userName;
    }


    public void setUserName(String value) {
        this.userName = value;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String value) {
        this.password = value;
    }


    public String getPrivilegedUser() {
        return privilegedUser;
    }


    public void setPrivilegedUser(String value) {
        this.privilegedUser = value;
    }


    public String getPrivilegedPassword() {
        return privilegedPassword;
    }


    public void setPrivilegedPassword(String value) {
        this.privilegedPassword = value;
    }


    public String getRouterConfigured() {
        return routerConfigured;
    }


    public void setRouterConfigured(String value) {
        this.routerConfigured = value;
    }


    public String getSmtpServer() {
        return smtpServer;
    }


    public void setSmtpServer(String value) {
        this.smtpServer = value;
    }


    public String getSmtpServerPort() {
        return smtpServerPort;
    }


    public void setSmtpServerPort(String value) {
        this.smtpServerPort = value;
    }


    public String getEmailUser() {
        return emailUser;
    }


    public void setEmailUser(String value) {
        this.emailUser = value;
    }


    public String getEmailPassword() {
        return emailPassword;
    }


    public void setEmailPassword(String value) {
        this.emailPassword = value;
    }

}

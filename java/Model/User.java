package Model;

public class User {
    private int id;
    private String username;
    private String password;
    private String realname;
    private String idNumber;
    private int vipId;
    private String phone;
    private String address;

    public User(){}

    public void setId(int id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setRealname(String realname) {this.realname = realname;}
    public void setIdNumber(String idNumber) {this.idNumber = idNumber;}
    public void setVipId(int vipiId) {this.vipId = vipiId;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setAddress(String address) {this.address = address;}

    public int getId() {return id;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getRealname() {return realname;}
    public String getIdNumber() {return idNumber;}
    public int getVipId() {return vipId;}
    public String getPhone() {return phone;}
    public String getAddress() {return address;}

}

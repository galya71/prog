package UserData;

import java.io.Serial;
import java.io.Serializable;

public class UserData implements Serializable {
	@Serial
	private static final long serialVersionUID = 6529685098267757690L;
	private String UserName;
	private double a,b;
	private String operation;
	private String result;

	public UserData(String UserName, double a, double b, String operation){
		setUserName(UserName);
		setValue(a,b);
		setOperation(operation);
	}

	public UserData(){
		this.UserName = "";
		this.a = 0;
		this.b = 0;
		this.operation = "";
	}

	public void setUserName(String UserName){
		this.UserName = UserName;
	}

	public void setValue(double a, double b){
		this.a = a;
		this.b = b;
	}

	public String getUserName(){
		return UserName;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public void setOperation(String operation){
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}
}
package distribucion;

public class Property {

	
	private String name;
	private String unit;
	private int value;
	private int bound;
	private boolean visible;
	
	public Property(String name,String unit,int value,int bound) {
		this.name = name;
		this.unit = unit;
		this.value = value;
		this.visible = true;
		this.bound = bound;
	}

	
	public boolean isVisilbe() {
		return this.visible;
	}
	
	public void toggleVisible() {
		visible = !visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public int getBound() {
		return this.bound;
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public int getValue() {
		return value;
	}


	public void setValue(int value) {
		this.value = value;
	}
	
	
	
}

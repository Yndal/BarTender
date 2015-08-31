package model.ADDb;

import java.io.Serializable;

public class DiscreteDatetime implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2589489936421090878L;
	
	private int hour;
	private int day;
	private int weekDay;
	private int month;
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

}

package model.ADDb;

import java.io.Serializable;

public class Video implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3988496496278634653L;

	public enum VideoType{
		Asset("asset"),
		Youtube("youtube"),
		Unknown("unknown");
		
		private final String strValue;
		
		private VideoType(String value){
			this.strValue = value;
		}
		
		public String getValue(){
			return strValue;
		}
		
		public static VideoType getType(String type){
			for(VideoType vt : VideoType.values())
				if(vt.strValue.equals(type))
					return vt;
			
			return Unknown;
		}
		
	}

	private final VideoType type;
	private final String link;
	
	public Video(String link, VideoType type){
		this.link = link;
		this.type = type;
	}
	
	
	
	public String getLink(){
		return link;
	}
	
	public VideoType getType(){
		return type;
	}
}

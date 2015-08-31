package control.dispenser.robotArm;

import java.io.IOException;

import control.dispenser.drink.Drink;
import control.dispenser.drink.IngredientAmount;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class RobotArmCommunicator {
	private enum Response {
		UNKNOWN(-1),
		OK(0),
		ERROR(1);

		private final int code;

		private Response(int code){
			this.code = code;
		}

		public int getCode(){
			return code;
		}
	}

	private static RobotArmCommunicator instance;

	static final String MSG_START_CHAR = "[";
	static final String MSG_END_CHAR = "]";
	private static final int CONNECTION_WAIT_TIME = 5000; //ms
	private static final String SCREEN_START_REQUEST = "START_ROBOT";
	private static final String SCREEN_START_RESPONSE = "ROBOT_STARTED";
	private final SerialPort serialPort;

	/**
	 * Default constructor for a baud rate of 9600, 8 data bits, one stop bit and no parity.
	 *
	 * @throws SerialPortException If the corresponding port cannot be opened.
	 * @throws IOException If no external screen is connected.
	 */
	@SuppressWarnings("unused")
	private RobotArmCommunicator() throws SerialPortException, IOException {
		this(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	}

	/**
	 * Specialized constructor.
	 *
	 * @param baudRate Baud rate.
	 * @param dataBits Number of data bits.
	 * @param stopBits Number of stop bits.
	 * @param parity Parity.
	 * @throws SerialPortException If the corresponding port cannot be opened.
	 * @throws IOException If no external robot arm is connected.
	 */
	private RobotArmCommunicator(int baudRate, int dataBits, int stopBits, int parity) throws SerialPortException, IOException {
		this(baudRate, dataBits, stopBits, parity, null);
	}

	/**
	 * Specialized constructor.
	 *
	 * @param baudRate Baud rate.
	 * @param dataBits Number of data bits.
	 * @param stopBits Number of stop bits.
	 * @param parity Parity.
	 * @param robotArm Instance of RobotArm (May be null)
	 * @throws SerialPortException If the corresponding port cannot be opened.
	 * @throws IOException If no external robot arm is connected.
	 */
	private RobotArmCommunicator(RobotArm robotArm) throws SerialPortException, IOException {
		this(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, robotArm);
	}

	/**
	 * Specialized constructor.
	 *
	 * @param baudRate Baud rate.
	 * @param dataBits Number of data bits.
	 * @param stopBits Number of stop bits.
	 * @param parity Parity.
	 * @param eventListener A SerialPortEventListener (May be null)
	 * @throws SerialPortException If the corresponding port cannot be opened.
	 * @throws IOException If no external robot arm is connected.
	 */
	public RobotArmCommunicator(int baudRate, int dataBits, int stopBits, int parity, RobotArm robotArm) throws SerialPortException, IOException {
		String[] ports = SerialPortList.getPortNames();
		if (ports.length == 0)
			throw new IllegalStateException("No ports found.");

		serialPort = searchForRobot(ports, baudRate, dataBits, stopBits, parity);
		if (serialPort == null)
			throw new IOException("Robot is not connected.");

		if (!serialPort.isOpened())
			startSerialPort(serialPort, baudRate, dataBits, stopBits, parity);

		if(robotArm != null){
			serialPort.addEventListener(new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {
					if(event.isRXCHAR()){
						String msg = "";
						boolean done = false;
						while(!done){
							String s = "";
							try {
								s = serialPort.readString();
								System.out.println("SerialPortEventListener incoming RXCHAR: " + s);
								msg += s;
							} catch (SerialPortException e) {
								e.printStackTrace();
							}
							if(s.isEmpty())
								break;
						}

						if(msg.contains(MSG_START_CHAR) && msg.contains(MSG_END_CHAR)){
							Object somethingNew = translateIncomingMsg(msg);
							robotArm.onIncomingSerialEvent(somethingNew);
						}
					}
				}
			});
		}
	}

	private Object translateIncomingMsg(String msg){
		return null;
	}

	private SerialPort searchForRobot(String[] names, int baudRate, int dataBits, int stopBits, int parity) {
		for (String s : names) {
			try {
				SerialPort sp = new SerialPort(s);
				startSerialPort(sp, baudRate, dataBits, stopBits, parity);

				String data = MSG_START_CHAR + SCREEN_START_REQUEST + MSG_END_CHAR;
				sp.writeBytes(data.getBytes());

				long start = System.currentTimeMillis();
				long end = CONNECTION_WAIT_TIME;
				StringBuilder sb = new StringBuilder();
				while (System.currentTimeMillis() - start < end) {
					byte[] bytes = sp.readBytes();
					if (bytes == null)
						continue;
					sb.append(convertBytesToString(bytes));
					if (sb.toString().contains(SCREEN_START_RESPONSE))
						return sp;
				}
				sp.closePort();
			} catch (SerialPortException spe) {
				spe.printStackTrace();
			}
		}
		return null;
	}

	private void startSerialPort(SerialPort port, int baudRate, int dataBits, int stopBits, int parity) throws SerialPortException {
		if (port == null)
			throw new IllegalArgumentException("Port cannot be null");
		port.openPort();
		port.setParams(baudRate, dataBits, stopBits, parity);
	}

	public boolean writeAndWaitForVerification(Request request, long timeoutMs){
		long endTime = System.currentTimeMillis() + timeoutMs;

		//Send msg
		try {
			write(request.getAsRequestString());
		} catch (SerialPortException e) {
			e.printStackTrace();
			return false;
		}

		//Look for verification
		String response = "";
		boolean done = false;
		while(!done && System.currentTimeMillis() < endTime){
			try {
				response += serialPort.readString();
				if(response.contains(MSG_START_CHAR))
					done = true;
			} catch (SerialPortException e) { }
		}

		//Cut away garbage in front of response
		int startIndex = response.indexOf(MSG_START_CHAR);
		response = response.substring(startIndex);

		//Read until MSG_END_CHAR
		done = false;
		String tempStr;
		while(!done && System.currentTimeMillis() < endTime){
			try{
				tempStr = serialPort.readString();
				if(tempStr.contains(MSG_END_CHAR)){
					int endIndex = tempStr.indexOf(MSG_END_CHAR);
					response += tempStr.substring(0, endIndex+1);
					if(strResponse2ResponseCode(response) == Response.OK)
						return true;
				}
				response += tempStr;
			} catch (SerialPortException exp){
			}	
		}

		return false;
	}

	private boolean write(String s) throws SerialPortException {
		String data = MSG_START_CHAR + s + MSG_END_CHAR;
		return serialPort.writeBytes(data.getBytes());
	}

	private String convertBytesToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
			sb.append((char) bytes[i]);
		return sb.toString();
	}

	public static RobotArmCommunicator getInstance(RobotArm robotArm) throws SerialPortException, IOException{
		if(instance == null)
			instance = new RobotArmCommunicator(robotArm);
		return instance;
	}

	public void close() {
		try {
			serialPort.closePort();
		} catch (SerialPortException spe) {
			spe.printStackTrace();
		}
	}
	
	private Response strResponse2ResponseCode(String str){
		if(str.indexOf(RobotArmCommunicator.MSG_START_CHAR) != 0 ||
				str.indexOf(RobotArmCommunicator.MSG_END_CHAR) != str.length()-1)
			return Response.UNKNOWN;
		
		int code =Integer.valueOf(str.substring(1, str.length()-1));
		
		for(Response response : Response.values())
			if(response.getCode() == code)
				return response;
		
		return Response.UNKNOWN;
	}
}

class Request {
	private final Drink drink;
	
	Request(Drink drink){
		this.drink = drink;
	}
	
	Drink getDrink(){
		return drink;
	}
	
	String getAsRequestString(){
		return "sdfds";
	}
	
	@Override
	public String toString(){
		String str = "Request(";
		str += "Glass:" + drink.getGlass().getName() + " - Ingredients:[";
		for(IngredientAmount ia : drink.getIngredientAmounts())
			str +="Pos: " + ia.getIngredientPosition() + ", Amount: " + ia.getAmount() + ", ";
		return str.substring(0, str.length()-1) + "])";
	}
}
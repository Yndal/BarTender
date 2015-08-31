package control.dispenser.robotArm;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import control.dispenser.Dispenser;
import control.dispenser.DispenserListener.State;
import control.dispenser.drink.Drink;
import control.dispenser.drink.IngredientAmount;
import jssc.SerialPortException;
import model.ADDb.Glass;

public class RobotArm extends Dispenser {
	private static final long WRITE_REQUEST_TIMEOUT_MS = 1000; //ms
	private static RobotArm instance;
	private State state;
	private Queue<Drink> drinkQueue = new ConcurrentLinkedQueue<>(); 
	private RobotArmCommunicator comm;

	private RobotArm(){
		changeState(State.Initializing);
		//Private constructor

		//Establish connection
		if(tryEstablishConnection())
			changeState(State.InitializationSucceeded);
		else 
			changeState(State.InitializationFailed);

	}

	private boolean tryEstablishConnection(){
		try {
			comm = RobotArmCommunicator.getInstance(this);
			return true;
		} catch (SerialPortException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void makeDrink(Glass glass, List<IngredientAmount> ingredientAmounts) {
		drinkQueue.add(new Drink(glass, ingredientAmounts));

		//Return if robot is not free
		if(state != State.Idle)
			return;

		executeDrinks();
	}

	private void executeDrinks(){
		while(!drinkQueue.isEmpty()){
			changeState(State.Busy);
			Drink drink = drinkQueue.poll();
			Request request = new Request(drink);
			boolean succeeded = comm.writeAndWaitForVerification(request, WRITE_REQUEST_TIMEOUT_MS);
			if(!succeeded){
				System.err.println("RobotArm.executeDrinks was unable to call writeAndWait...() for request: " + request);
			}
			//comm.waitForReady()

			/*
			 * Send all the drink information to the robot
			 */








			changeState(State.DrinkMade);
		}
		changeState(State.Idle);
	}




	/*
	 * For the listeners
	 */
	private void changeState(State state){
		this.state = state;
		stateChanged(state);
	}

	void onIncomingSerialEvent(Object object){
		System.out.println("RobotArm got an object from the Communication class");
	}

	/*
	 * This may take a while if the connection has to be established...
	 */
	public static RobotArm getInstance(){
		if(instance == null)
			instance = new RobotArm();
		return instance;
	}






}



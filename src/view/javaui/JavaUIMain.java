package view.javaui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import control.Controller;
import control.Controller.AvailableDrinkListener;
import control.Controller.ControllerStatusListener;
import control.Controller.DrinkMakerListener;
import model.Amount;
import control.storage.LocalStorage;
import model.Amount.QuantityType;
import model.ADDb.Drink;
import model.ADDb.Drink.Alcoholic;
import model.ADDb.Ingredient;
import model.ADDb.IngredientProperties;

public class JavaUIMain implements ControllerStatusListener, DrinkMakerListener{
	private static final String PROGRAM_TITLE = "BarTender";

	private static JavaUIMain instance;
	private JFrame frame;
	private Container contentPane;
	private JPanel northPanel;
	private JPanel centerPanel;
	private JPanel southPanel;
	private JLabel statusLabel;
	private Dimension screenDimension;
	private Controller controller;
	private final HashMap<String, JButton> addedDrinks = new HashMap<>();

	private static boolean windowAlreadyLoaded = false;

	private JavaUIMain(){
		//Private constructor to prevent several instances
	}

	public void createMainWindow(){
		if(windowAlreadyLoaded){
			frame.toFront();
			return;
		}

		windowAlreadyLoaded = true;
		controller = Controller.getInstance();	
		controller.addControllerStatusListener(this);
		controller.addDrinkMakerListener(this);
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		frame = new JFrame(PROGRAM_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//		frame.setSize(screenDimension);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		//frame.setMinimumSize(screenDimension);
		//frame.setMaximumSize(screenDimension);
		//frame.setPreferredSize(screenDimension);
		contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
	

		addNorthPanel();
		addCenterPanel();
		addSouthPanel();

		//updateContent();
		frame.pack();
		frame.setVisible(true);

		//TODO
		/*List<Drink> drinks = controller.getTestDrinksFromADDb();//LocalStorage();
		for(int i=0; i<drinks.size() && i<50; i++)
			addDrink(drinks.get(i));
		 */
	}

	private void addNorthPanel(){
		northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(1,0));
		northPanel.setBorder(BorderFactory.createTitledBorder("North titled border"));

		//Add/remove ingredient
		JButton ingredientButton = new JButton("Add/remove ingredient...");
		northPanel.add(ingredientButton);
		ingredientButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddIngredient.createView(controller);
			}
		});		

		//Search for drinks
		JButton searchButton = new JButton("Search for drinks...");
		northPanel.add(searchButton);
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				statusLabel.setText("Search button clicked");
			}
		});


		//Add drink
		JButton drinkButton = new JButton("Create new drink...");
		northPanel.add(drinkButton);
		drinkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				statusLabel.setText("Add drink button clicked");

			}
		});

	}

	private void addCenterPanel(){
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(0,3));
		JScrollPane scrollPane = new JScrollPane(centerPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		/*centerPanel.setSize(screenDimension.width/2, screenDimension.height/2);
		scrollPane.setSize(screenDimension.width/2, screenDimension.height/2);
		contentPane.setSize(screenDimension.width/2, screenDimension.height/2);
		 */
		//		frame.setMinimumSize(new Dimension(screenDimension.width/2 + 40, screenDimension.height/2));
		centerPanel.setBorder(BorderFactory.createTitledBorder("Select a drink"));
		centerPanel.addComponentListener(new ComponentListener() {	
			@Override
			public void componentShown(ComponentEvent e) {
				updateSize();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				updateSize();
			}

			@Override
			public void componentMoved(ComponentEvent e) { }

			@Override
			public void componentHidden(ComponentEvent e) { }

			private void updateSize(){
				int amount = centerPanel.getComponents().length;
				centerPanel.setPreferredSize(new Dimension(screenDimension.width, (screenDimension.height/2)*(amount/3)));
			}
		});

		//updateContent();
	}

	private void addSouthPanel(){
		southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
		southPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		contentPane.add(southPanel, BorderLayout.SOUTH);

		//Add drinks made counter
		@SuppressWarnings("serial")
		class DrinkCounterJLabel extends JLabel implements DrinkMakerListener {

			@Override
			public void onDrinkStarted(Drink drink) {}

			@Override
			public void onDrinkMade(Drink drink, int number) {
				setText("Drinks made: " + number);
			}

			@Override
			public void onDrinkProgress(Drink drink, int currentIngredientLevel) {}

			@Override
			public void onIngredientAlmostEmpty(Ingredient ingredient) {
				System.out.println("Almost empty: " + ingredient.getName());
			}

			@Override
			public void onIngredientEmpty(Ingredient ingredient) {
				System.out.println("Empty: " + ingredient.getName());
			}

		}
		southPanel.add(Box.createRigidArea(new Dimension(20,20)));
		DrinkCounterJLabel drinkCounter = new DrinkCounterJLabel();
		southPanel.add(drinkCounter);
		drinkCounter.setText("Drinks made: " + controller.getNumberOfDrinksMade());
		controller.addDrinkMakerListener(drinkCounter);

		southPanel.add(Box.createHorizontalGlue());

		//Add status area
		statusLabel = new JLabel("Status messages in this area");
		southPanel.add(statusLabel);

		southPanel.add(Box.createHorizontalGlue());

		//Add <available drinks>/<all drinks> label
		@SuppressWarnings("serial")
		class BarTenderJLabel extends JLabel implements AvailableDrinkListener {

			private int availableDrinks = 0;
			private int totalDrinks = 0;
			private int totalUpdated = 0;

			public BarTenderJLabel(String string) {
				super(string);
				updateTextField();
				controller.addAvailableDrinkAmountListener(this);
			}

			@Override
			public void onAvailableDrinksUpdated(List<String> drinkIds) {
				availableDrinks = drinkIds.size();
				
				//Remove all drinks that is not in the updated set
				for(String id : addedDrinks.keySet())
					if(!drinkIds.contains(id))
						removeDrink(id);
				
				//Add att new drinks
				for(String id : drinkIds){
					//No need to add it twice...
					if(addedDrinks.containsKey(id))
						continue;
					
					Drink drink = controller.getDrink(id);
					if(drink != null)
						addDrink(drink);
				}
				updateTextField();
			}

			/*@Override
			public void onTotalDrinksUpdated(int totalAmount) {
				totalDrinks = totalAmount;
				updateTextField();

			}*/

			@Override
			public void onDrinksUpdated(List<String> drinkIds, int totalUpdated, int totalAmount) {
				this.totalUpdated = totalUpdated;
				this.totalDrinks = totalAmount;
				
				updateTextField();
				
			}
			
			private void updateTextField(){
				String text = "Available drinks: " + availableDrinks + "/" + totalDrinks;
				if(totalDrinks != totalUpdated)
					text += " (" + totalUpdated + " updated)";
				setText(text);
			}

		}

		BarTenderJLabel availableDrinksLabel = new BarTenderJLabel("Available drinks label");
		southPanel.add(availableDrinksLabel);
		controller.addAvailableDrinkAmountListener(availableDrinksLabel);

		southPanel.add(Box.createRigidArea(new Dimension(20,20)));

		//updateContent();

	}

	private void addDrink(Drink drink){
		centerPanel.setVisible(false);
		//Main 
		JButton button = new JButton();
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.makeDrink(drink);
			}
		});
		centerPanel.add(button);
		addedDrinks.put(drink.getId(), button);
		button.setLayout(new BorderLayout());

		//North side
		JPanel northButton = new JPanel();
		northButton.setLayout(new GridBagLayout());
		button.add(northButton, BorderLayout.NORTH);

		//East side
		JPanel outterEast = new JPanel();
		outterEast.setLayout(new GridLayout(2, 1));
		button.add(outterEast, BorderLayout.EAST);

		//Actual
		JPanel eastButton = new JPanel();
		eastButton.setLayout(new GridLayout(0, 1));
		outterEast.add(eastButton);

		//South side
		JPanel southButton = new JPanel();
		southButton.setLayout(new BorderLayout());
		southButton.setBorder(BorderFactory.createTitledBorder("Description"));
		button.add(southButton, BorderLayout.SOUTH);

		//Center 
		JPanel centerButton = new JPanel();
		centerButton.setLayout(new BorderLayout());//new BoxLayout(centerButton, BoxLayout.PAGE_AXIS));
		button.add(centerButton, BorderLayout.CENTER);

		//Add rating
		JLabel rating = new JLabel("Rating: " + drink.getRating());
		eastButton.add(rating);

		//Add Skill
		String skillStr;
		if(drink.getSkill() != null)
			skillStr = drink.getSkill().getName();
		else 
			skillStr = "?";

		JLabel skill = new JLabel("Skill: " + skillStr);
		eastButton.add(skill);

		//Add isAlcoholic
		final double alcoholFactor = 20;
		if(drink.getIsAlchoholic() == null)
			drink.setIsAlchoholic(Alcoholic.Unknown);

		try {
			File file;
			switch (drink.getIsAlchoholic()) {
			case Alcoholic:
				file = new File("images/alcoholic/Alcoholic.jpg");
				break;
			case NotAlcoholic:
				file = new File("images/alcoholic/NonAlcoholic.jpg");
				break;
			default:
				file = new File("images/alcoholic/UnknownAlcoholic.jpg");
				break;
			}
			BufferedImage myPicture = ImageIO.read(file);
			JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance((int) (screenDimension.width/alcoholFactor), -1, Image.SCALE_AREA_AVERAGING)));
			eastButton.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}


		//Add servedIn
		int glassImageFactor = 25;
		ImageIcon glassImage = null;
		if(drink.getServedIn() == null || drink.getServedIn().getImage() == null){
			try {
				File file = new File("images/glasses/unknown-glass.jpg");
				BufferedImage myPicture = ImageIO.read(file);
				glassImage = new ImageIcon(myPicture);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			glassImage = drink.getServedIn().getImage();
		}
		Image smallGlassImage = glassImage.getImage().getScaledInstance(-1, screenDimension.height/glassImageFactor, Image.SCALE_AREA_AVERAGING);
		eastButton.add(new JLabel(new ImageIcon(smallGlassImage)));


		//Add ingredients
		JPanel ingPanel = new JPanel();
		outterEast.add(ingPanel);

		ingPanel.setBorder(BorderFactory.createTitledBorder("Ingredients"));
		ingPanel.setLayout(new GridLayout(0,1));
		if(drink.getIngredientProperties() != null){
			for(IngredientProperties ing : drink.getIngredientProperties()){
				ingPanel.add(new JLabel(ing.getTextPlain()));

			}
		} else {
			ingPanel.add(new JLabel("Unknown"));
		}


		//Add image
		final double imageFactor = 4;
		Image imgImage;
		JLabel picLabel = null;
		if(drink.getImage() != null){
			//Load image
			imgImage = drink.getImage().getScaledInstance(-1, (int) (screenDimension.height/imageFactor), Image.SCALE_AREA_AVERAGING);
			picLabel = new JLabel(new ImageIcon(imgImage));

		}

		//If there is no image or loading fails
		if(picLabel == null) {
			//TODO Call this when loading the actual image fails - needs to be tested
			//Load default image
			try {
				File file = new File("images/DefaultDrinkImage.jpg");
				imgImage = ImageIO.read(file);
				picLabel = new JLabel(new ImageIcon(imgImage.getScaledInstance(-1, (int) (screenDimension.height/imageFactor), Image.SCALE_AREA_AVERAGING)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(picLabel != null) //Just for precaution
			centerButton.add(picLabel, BorderLayout.CENTER);

		//Add name
		if(drink.getName() == null || drink.getName().equals(""))
			drink.setName("Unknown");
		JLabel title = new JLabel(drink.getName());
		title.setFont(new Font("Serif", Font.BOLD, 30));
		northButton.add(title);

		//Add description
		if(drink.getDescription() == null)
			drink.setDescription("");

		JLabel description = new JLabel("<html>" + drink.getDescriptionPlain() + "</html>");
		southButton.add(description);

//		southPanel.revalidate();
//		southPanel.repaint();
//		southButton.revalidate();
//		southButton.repaint();
//		description.revalidate();
//		description.repaint();
	//	button.setMinimumSize(new Dimension(screenDimension.width/3, screenDimension.height/2));
		centerPanel.setPreferredSize(new Dimension(centerPanel.getComponentCount() * screenDimension.width, screenDimension.height/2));
		//centerPanel.revalidate();
		//centerPanel.repaint();
//		contentPane.revalidate();
//		contentPane.repaint();
//		frame.revalidate();
//		frame.repaint();
		

		//updateContent();
		centerPanel.setVisible(true);
	}


	public void addDrinks(List<Drink> drinks){
		for(Drink drink : drinks)
			addDrink(drink);
	}
	
	public void removeDrink(String id){
		JButton drinkButton = addedDrinks.get(id);
		if(drinkButton != null)
			centerPanel.remove(drinkButton);
	}

	public void removeAllDrinks(){
		centerPanel.removeAll();
	}
		
	
	private void updateContent(){
		//TODO Make sure that everything is displayed correctly after new stuff is added! (I.e. after adding the first couple of drinks)
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		contentPane.revalidate();
		contentPane.repaint();
		frame.revalidate();
		frame.repaint();
		frame.pack();
		frame.setVisible(true);

	}

	public static JavaUIMain getInstance(){
		if(instance == null)
			instance = new JavaUIMain();
		return instance;
	}


	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JavaUIMain.getInstance().createMainWindow();
			}
		});

	}

	@Override
	public void onDrinkStarted(Drink drink) {
	}

	@Override
	public void onDrinkProgress(Drink drink, int currentIngredientLevel) {
	}

	@Override
	public void onDrinkMade(Drink drink, int number) {
	}

	@Override
	public void onStatusUpdated(Status status) {
		statusLabel.setText(status.getDescription());
		switch(status){
		case InitializationStarted:
			break;
		case InitializationEnded:
			break;
		case ShuttingDown:
			break;
		case SearchingForDrinksStarted:
			break;
		case SearchingForDrinksEnded:
			break;
		case MakingDrinkStarted:
			break;
		case MakingDrinkEnded:
			break;
		case UnderMaintananceStarted:
			break;
		case UnderMaintananceEnded:
			break;
		default:
			//Ignore for now
		}		
	}

	@Override
	public void onIngredientAlmostEmpty(Ingredient ingredient) {
	}

	@Override
	public void onIngredientEmpty(Ingredient ingredient) {
	}

	/*@Override
	public void onLoadedDrinksFromLocalStorage(List<Drink> drinks) {
	}

	@Override
	public void onCollectedDrinksFromInternet(List<Drink> drinks) {
	}

	@Override
	public void onAvailableDrinksUpdated(List<Drink> drinks) {
	}*/

}
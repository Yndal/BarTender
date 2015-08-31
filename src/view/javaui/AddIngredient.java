package view.javaui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import control.Controller;
import model.Amount;
import model.Amount.QuantityType;
import model.ADDb.Ingredient;

public class AddIngredient {
	private static final String FRAME_TITLE = "Add ingredient";
	private static final AtomicBoolean viewIsShowing = new AtomicBoolean(false);
	private static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	private static JFrame frame;
	private static Container contentPane;

	private static Controller controller;

	private static String ingredientId = "";
	private static int ingredientPosition = -1;
	private static Amount ingredientAmount = new Amount();

	private static JComboBox<String> nameComboBox;
	private static JComboBox<String> positionsBox;
	private static JTextField quantityTextField;
	private static JComboBox<String> qTypeComboBox;
	private static Color nameComboBoxDefaultColor;
	private static Color positionsBoxDefaultColor;
	private static Color quantityTextFieldDefaultColor;


	private AddIngredient(){
		//Private constructor to avoid direct instantiation
	}

	public static void createView(Controller theController) {
		if(viewIsShowing.get()){
			System.out.println("AddIngredient is already showing");
			frame.toFront();
			return;
		}
		viewIsShowing.set(true);
		controller = theController;

		List<Ingredient> ingredients = theController.getIngredients();
		Set<String> types = new HashSet<>();
		for(Ingredient i : ingredients)
			types.add(i.getType());

		frame = new JFrame(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				viewIsShowing.set(false);
			}

			@Override
			public void windowActivated(WindowEvent e) {}
		});
		//		frame.setSize(screenDimension);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		//frame.setMinimumSize(screenDimension);
		frame.setMaximumSize(screenDimension);
		contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(0, 1));

		List<Boolean> availablePositions = theController.getAvailableIngredientPositions();

		addNamePart(contentPane);
		addPositionPart(contentPane, availablePositions);
		addQuantityPart(contentPane);
		addButtons(contentPane);

		updateContent();
		frame.setVisible(true);
	}

	private static void addNamePart(Container contentPane) {
		//Add name part
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		contentPane.add(panel);

		JLabel nameLabel = new JLabel("Name");
		panel.add(nameLabel);


		nameComboBox = new JComboBox<>();
		Map<String, String> name2IdMap = controller.getIngredientName2IdMap();
//		for(String name : name2IdMap.keySet())
//			System.out.println(name + "=>" + name2IdMap.get(name));
		String[] elements = name2IdMap.keySet().toArray(new String[0]);
		AutoCompleteSupport<String> o = AutoCompleteSupport.install(nameComboBox, GlazedLists.eventListOf(elements));
		o.setHidesPopupOnFocusLost(true);
		nameComboBox.addItemListener(new ItemListener() {		
			@Override
			public void itemStateChanged(ItemEvent e) {
				int index = nameComboBox.getSelectedIndex();
				if(index != -1){
					String name = (String) nameComboBox.getSelectedItem();
					String id = name2IdMap.get(name);//controller.getIngredientIdFromName(name);
					System.out.println("name selected: " + name);
					System.out.println("id collected selected: ");
					if(id != null && !id.isEmpty())
						ingredientId = id;
				} else {
					ingredientId = null;
				}
			}
		});
		nameComboBoxDefaultColor = nameComboBox.getEditor().getEditorComponent().getBackground();
		panel.add(nameComboBox);
	}


	@SuppressWarnings("unchecked")
	private static void addPositionPart(Container contentPane, final List<Boolean> availablePositions) {
		//Add position part
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		contentPane.add(panel);

		JLabel positionLabel = new JLabel("Position");
		panel.add(positionLabel);

		DefaultListSelectionModel model = new DefaultListSelectionModel();

		String[] positionsArray = new String[availablePositions.size()];
		int selectable = -1;
		for(int i=0; i<availablePositions.size(); i++){
			positionsArray[i] = "" + i;
			if(availablePositions.get(i)){
				model.addSelectionInterval(i, i);
				if(selectable == -1) //Ensure that a selectable is actually selected
					selectable = i;				
			}
		}
		ingredientPosition = selectable;

		positionsBox = new JComboBox<>(positionsArray);
		positionsBox.setSelectedIndex(selectable);
		final int fSelectable = selectable;
		positionsBox.addItemListener(new ItemListener() {
			private int prevValue = fSelectable;
			@Override
			public void itemStateChanged(ItemEvent e) {
				int pos = positionsBox.getSelectedIndex();
				boolean canBeSelected = availablePositions.get(pos);
				if(!canBeSelected) {
					positionsBox.setSelectedIndex(prevValue);	
				} else {
					prevValue = pos;
					ingredientPosition = pos;
				}
			}
		});

		EnabledJComboBoxRenderer enableRenderer = new EnabledJComboBoxRenderer(model);
		positionsBox.setRenderer(enableRenderer);
		positionsBoxDefaultColor = positionsBox.getEditor().getEditorComponent().getBackground();
		panel.add(positionsBox);

	}


	private static void addQuantityPart(Container contentPane) {
		//Add quantity
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		contentPane.add(panel);
		JLabel qLabel = new JLabel("Quantity:");
		panel.add(qLabel);

		//This field must only accept numbers
		quantityTextField = new JTextField();
		quantityTextField.addKeyListener(new KeyListener() {
			Character[] numbers = new Character[]{'0','1','2','3','4','5','6','7','8','9'};
			private Set<Character> chars = new HashSet<Character>(Arrays.asList(numbers));

			private String text = "";

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(chars.contains(e.getKeyChar()))
					text += e.getKeyChar();
				quantityTextField.setText(text);

				updateIngredientQuantity();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		panel.add(quantityTextField);
		/*NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setParseIntegerOnly(true);
		decimalFormat.setGroupingUsed(false);
		quantityTextField = new JFormattedTextField(decimalFormat);
		quantityTextField.setColumns(15);
		panel.add(quantityTextField);*/

		String[] qTypes = new String[QuantityType.values().length];
		int qCounter = 0;
		for(QuantityType qt :QuantityType.values())
			qTypes[qCounter++] = qt.getName();
		qTypeComboBox = new JComboBox<>(qTypes);
		qTypeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateIngredientQuantity();
			}
		});
		quantityTextFieldDefaultColor = quantityTextField.getBackground();
		panel.add(qTypeComboBox);

	}

	private static void updateIngredientQuantity(){
		if(!quantityTextField.getText().isEmpty()){
			int amountInt = Integer.valueOf(quantityTextField.getText());
			QuantityType  type =  QuantityType.valueOf(((String) qTypeComboBox.getSelectedItem()));
			ingredientAmount = new Amount(amountInt, type);
		}
	}

	private static void addButtons(Container contentPane){
		//Add Cancel and Add button
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		contentPane.add(panel);

		//Cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();				
			}
		});
		panel.add(cancelButton);

		//Add button
		JButton addButton = new JButton("Add...");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isValid = validateInputs();
				if(isValid){					
					try{
						Ingredient ingredient = controller.getIngredientFromId(ingredientId);
						int position = ingredientPosition;
						Amount amount = ingredientAmount;

						controller.addIngredient(ingredient, position, amount); //Define amount
						frame.dispose();
						System.out.println("Ingredient added to controller");
					} catch (IllegalArgumentException exp){
						JOptionPane.showMessageDialog(frame,
								"Unable to add ingredient: " + exp.getMessage(),
								"Inane error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel.add(addButton);
	}

	private static boolean validateInputs(){
		boolean result = true;
		//Name
		if(ingredientId == null || ingredientId.isEmpty()){
			result = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					nameComboBox.getEditor().getEditorComponent().setBackground(Color.red);
				}
			});
		} else {
			nameComboBox.getEditor().getEditorComponent().setBackground(nameComboBoxDefaultColor);
		}

		//Position
		int position = positionsBox.getSelectedIndex();
		if(position == -1){
			result = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					positionsBox.getEditor().getEditorComponent().setBackground(Color.red);
				}
			});
		} else {
			positionsBox.getEditor().getEditorComponent().setBackground(positionsBoxDefaultColor);
		}

		//Quantity
		if(quantityTextField.getText().isEmpty() || quantityTextField.getText().equals("0")){
			result = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					System.out.println("Amount can't be 0.");	
					quantityTextField.setBackground(Color.red);
				}
			});
		} else {
			quantityTextField.setBackground(quantityTextFieldDefaultColor);
		}


		return result;
	}


	private static void updateContent(){
		frame.revalidate();
		frame.repaint();
		frame.pack();
	}

	/**
	 * Class that can be used as a renderer for a JComboBox which enables/disables
	 * items based upon a ListSelectionModel that specifies which items are enabled. 
	 * @author Greg Cope
	 */
	//Based on http://www.algosome.com/articles/enable-disable-jcombobox.html
	static class EnabledJComboBoxRenderer extends BasicComboBoxRenderer{

		static final long serialVersionUID = -984932432414L;

		private final ListSelectionModel enabledItems;

		private Color disabledColor = Color.lightGray;

		/**
		 * Constructs a new renderer for a JComboBox which enables/disables items
		 * based upon the parameter model.
		 * @param enabled
		 */
		public EnabledJComboBoxRenderer(ListSelectionModel enabled){
			super();
			this.enabledItems = enabled;
		}

		/**
		 * Sets the color to render disabled items. 
		 * @param disabledColor
		 */
		public void setDisabledColor(Color disabledColor){
			this.disabledColor = disabledColor;
		}

		/**
		 * Custom implementation to color items as enabled or disabled. 
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ( !enabledItems.isSelectedIndex(index) ){//not enabled
				if ( isSelected ){
					c.setBackground(UIManager.getColor("ComboBox.background"));
				}else{
					c.setBackground(super.getBackground());
				}
				c.setForeground(disabledColor);
			}else{
				c.setBackground(super.getBackground());
				c.setForeground(super.getForeground());
			}
			return c;
		}
	}
}

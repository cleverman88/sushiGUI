package comp1206.sushi.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import comp1206.sushi.common.*;
import comp1206.sushi.server.ServerInterface.UnableToDeleteException;

/**
 * Provides the Sushi Server user interface
 *
 */
public class ServerWindow extends JFrame implements UpdateListener {
	private JTabbedPane tabbedPane;
	private JPanel orders, dishes, ingredients, suppliers, staff, drones, users, postcodes, configuration, p;
	private JTable droneTable, staffTable, postcodeTable, supplierTable, ingredientTable, dishTable, orderTable,
			userTable;
	private static final long serialVersionUID = -4661566573959270000L;
	private ServerInterface server;
	private ArrayList suppliersBox, ingredientBox;

	/**
	 * Create a new server window
	 * 
	 * @param server instance of server to interact with
	 */
	public ServerWindow(ServerInterface server) {
		super("Sushi Server");
		this.server = server;
		this.setTitle(server.getRestaurantName() + " Server");
		server.addUpdateListener(this);

		// Display window
		setSize(1000, 1000);
		init();
		tabbedPane.setFont(new Font("Times New Roman", Font.BOLD, 22));
		tabbedPane.addTab("Postcodes", postcodes);
		tabbedPane.addTab("Drones", drones);
		tabbedPane.addTab("Staff", staff);
		tabbedPane.addTab("Suppliers", suppliers);
		tabbedPane.addTab("Ingredients", ingredients);
		tabbedPane.addTab("Dishes", dishes);
		tabbedPane.addTab("Orders", orders);
		tabbedPane.addTab("Users", users);
		tabbedPane.addTab("Configuration", configuration);
		tabbedPane.setPreferredSize(new Dimension(400, 50));
		this.add(tabbedPane);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		// Start timed updates
		startTimer();
	}

	/**
	 * Start the timer which updates the user interface based on the given interval
	 * to update all panels
	 */

	public void init() {
		tabbedPane = new JTabbedPane();
		orders = new JPanel();
		dishes = new JPanel();
		ingredients = new JPanel();
		suppliers = new JPanel();
		staff = new JPanel();
		drones = new JPanel();
		users = new JPanel();
		postcodes = new JPanel();
		configuration = new JPanel();
		populateDrones();
		populateStaff();
		populatePostcodes();
		populateSuppliers();
		populateIngredients();
		populateDishes();
		populateOrders();
		populateUsers();
	}

	public JTable addTable(JPanel panel, String data[][], String columnNames[]) {
		JTable j = new JTable(data, columnNames);
		j.getTableHeader().setReorderingAllowed(false);
		JScrollPane sp = new JScrollPane(j);
		sp.setPreferredSize(new Dimension(1000, 450));
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		j.setModel(tableModel);
		j.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 14));
		panel.setLayout(new BorderLayout());
		panel.add(sp, BorderLayout.NORTH);
		return j;
	}

	public ArrayList<JButton> addButton(JPanel panel, boolean istrue, boolean haveMap) {
		JPanel totalPanel = new JPanel();
		totalPanel.setPreferredSize(new Dimension(1000, 100));
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel bottomPanel = new JPanel();

		JButton add = new JButton("Add");
		JButton edit = new JButton("Edit");
		JButton del = new JButton("Delete");
		JButton map = new JButton("Open map");

		add.setBackground((new Color(180, 180, 180)));
		edit.setBackground(new Color(180, 180, 180));
		del.setBackground(new Color(180, 180, 180));

		ArrayList<JButton> buttons = new ArrayList<JButton>();

		bottomPanel.setLayout(new GridBagLayout());
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.ipadx = 300;
		gbc.ipady = 15;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		bottomPanel.add(add, gbc);
		
		if(haveMap) {
			gbc.anchor = GridBagConstraints.PAGE_END;
			bottomPanel.add(map, gbc);
		}
		if (istrue) {
			gbc.anchor = GridBagConstraints.PAGE_END;
			bottomPanel.add(edit, gbc);
		}
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		bottomPanel.add(del, gbc);
		bottomPanel.setBackground(new Color(210, 210, 210));
		panel.add(bottomPanel, BorderLayout.SOUTH);
		buttons.add(add);
		if (istrue) {
			buttons.add(edit);
		}
		if(haveMap) {
			buttons.add(map);
		}
		buttons.add(del);
		return buttons;
	}

	public ArrayList addTextField(JPanel panel, String[] labels) {
		p = new JPanel();
		ArrayList<Object> list = new ArrayList<Object>();
		SpringLayout layout = new SpringLayout();
		p.setLayout(layout);
		int counter = 20;
		for (int i = 0; i < labels.length; i++) {
			JLabel label = new JLabel(labels[i] + ":");
			Object field = null;
			if (label.getText().equals("Postcodes:") || label.getText().equals("Supplier:")) {
				field = new JComboBox();
			} else {
				field = new JTextField(30);
			}
			label.setFont(new Font("Calibri", Font.BOLD, 30));
			((Container) field).setFont(new Font("Calibri", Font.BOLD, 25));
			((Component) field).setForeground(Color.GRAY);
			layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, p);
			layout.putConstraint(SpringLayout.NORTH, label, counter, SpringLayout.NORTH, p);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, (Component) field, 100, SpringLayout.HORIZONTAL_CENTER,
					p);
			layout.putConstraint(SpringLayout.NORTH, (Component) field, counter, SpringLayout.NORTH, p);
			list.add(field);
			p.add(label);
			p.add((Component) field);
			counter = counter + 75;
		}
		panel.add(p, BorderLayout.CENTER);
		return list;
	}

	public boolean errorCheck(ArrayList<JTextField> fields) {
		boolean isCorrect = true;
		for (Object f : fields) {
			if (f instanceof JTextField) {
				if (((JTextField) f).getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Error one or more of the fields are empty", "Error", 0);
					isCorrect = false;
					break;
				}
			}
		}
		return isCorrect;
	}

	public void populateDrones() {
		String[] columnNamesDrone = { "Name", "Speed", "Status" };
		String[] labels = { "Name", "Speed" };
		String[][] data = new String[server.getDrones().size()][3];

		droneTable = addTable(drones, data, columnNamesDrone);
		ArrayList<JButton> buttons = addButton(drones, false,true);
		ArrayList<JTextField> fields = addTextField(drones, labels);
		DefaultTableModel model = (DefaultTableModel) droneTable.getModel();
		for (Drone d : server.getDrones()) {
			model.addRow(new String[] { d.getName().toString(), d.getSpeed().toString(), server.getDroneStatus(d) });
		}

		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getActionCommand().equals("Open map")) {
					int indexRow = droneTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Drone d : server.getDrones()) {
							if (droneTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								Mapper.buildURLDrone(d);
							}
						}
					}
				}
				if (ae.getActionCommand().equals("Add")) {
					if (errorCheck(fields)) {
						try {
							Drone drone = server.addDrone(Integer.parseInt((fields.get(1).getText())));
							drone.setName(fields.get(0).getText());
							Random randomizer = new Random();
							Postcode random = (server.getSuppliers().get(randomizer.nextInt(server.getSuppliers().size()))).getPostcode();
							drone.setSource(random);
							drone.setDestination(server.getRestaurantPostcode());
							fields.get(0).setText("");
							fields.get(1).setText("");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
						}
					}
				}
				if (ae.getActionCommand().equals("Delete")) {
					int indexRow = droneTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Drone d : server.getDrones()) {
							if (droneTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								try {
									server.removeDrone(d);
									break;
								} catch (UnableToDeleteException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}

	}

	public void populateStaff() {
		String[] columnNamesStaff = { "Name", "Status", "Fatigue" };
		String[] lables = { "Name" };
		String[][] data = new String[server.getStaff().size()][3];
		staffTable = addTable(staff, data, columnNamesStaff);
		ArrayList<JButton> buttons = addButton(staff, false,false);
		ArrayList<JTextField> fields = addTextField(staff, lables);
		DefaultTableModel model = (DefaultTableModel) staffTable.getModel();
		for (Staff d : server.getStaff()) {
			model.addRow(new String[] { d.getName().toString(), server.getStaffStatus(d), d.getFatigue().toString() });
		}

		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (ae.getActionCommand().equals("Add")) {
					if (errorCheck(fields)) {
						try {
							Staff staff = server.addStaff((fields.get(0).getText()));
							staff.setName(fields.get(0).getText());
							fields.get(0).setText("");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
						}
					}
				}
				if (ae.getActionCommand().equals("Delete")) {
					int indexRow = staffTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Staff d : server.getStaff()) {
							if (staffTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								try {
									server.removeStaff(d);
									break;
								} catch (UnableToDeleteException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}
	}

	public void populatePostcodes() {

		String[] columnNamesPostcodes = { "Postcodes", "Distance", "Lat", "Lon" };

		String[][] data = new String[server.getPostcodes().size()][4];
		postcodeTable = addTable(postcodes, data, columnNamesPostcodes);
		ArrayList<JButton> buttons = addButton(postcodes, false,true);
		String[] lables = { "Postcode" };
		ArrayList<JTextField> fields = addTextField(postcodes, lables);
		DefaultTableModel model = (DefaultTableModel) postcodeTable.getModel();
		for (Postcode d : server.getPostcodes()) {
			model.addRow(new String[] { d.getName().toString(), d.getDistance().toString(),
					d.getLatLong().get("lat").toString(), d.getLatLong().get("lon").toString() });
		}

		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getActionCommand().equals("Open map")) {
					Mapper.buildURL(server);
				}
				if (ae.getActionCommand().equals("Add")) {
					boolean add = true;
					if (errorCheck(fields)) {
						try {
							for(Postcode p : server.getPostcodes()) {
								if(p.getName().equals(fields.get(0).getText())) {
									add = false;
									break;
								}
							}
							
							if(add) {Postcode postcode = server.addPostcode(fields.get(0).getText());
							postcode.setName(fields.get(0).getText());
							}
							else {JOptionPane.showMessageDialog(null, "Error duplicate postcode", "Error", 0);}
							
							fields.get(0).setText("");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error not a valid UK postcode", "Error", 0);
						}
						((JComboBox) suppliersBox.get(1)).removeAllItems();
						for (Postcode p : server.getPostcodes()) {
							((JComboBox) suppliersBox.get(1)).addItem(p.getName());
						}
					}
				}
				if (ae.getActionCommand().equals("Delete")) {
					boolean carry = true;
					int indexCol = 0;
					int indexRow = postcodeTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						for (Supplier s : server.getSuppliers()) {
							if (s.getPostcode().toString().equals(postcodeTable.getValueAt(indexRow, indexCol))) {
								JOptionPane.showMessageDialog(null, "Supplier is using postcode", "Error", 0);
								carry = false;
								break;
							}
						}
					}
					if (carry) {
						for (Postcode d : server.getPostcodes()) {
							if (postcodeTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								try {
									server.removePostcode(d);
									break;
								} catch (UnableToDeleteException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}
	}

	public void populateSuppliers() {
		String[] columnNamesSuppliers = { "Name", "Distance", "Postcode" };
		String[][] data = new String[server.getSuppliers().size()][3];
		supplierTable = addTable(suppliers, data, columnNamesSuppliers);
		ArrayList<JButton> buttons = addButton(suppliers, false,true);
		String[] lables = { "Name", "Postcodes" };
		ArrayList fields = addTextField(suppliers, lables);
		suppliersBox = fields;
		DefaultTableModel model = (DefaultTableModel) supplierTable.getModel();
		for (Supplier d : server.getSuppliers()) {
			model.addRow(
					new String[] { d.getName().toString(), d.getDistance().toString(), d.getPostcode().toString() });
		}
		for (Postcode p : server.getPostcodes()) {
			((JComboBox) fields.get(1)).addItem(p.getName());
		}
		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getActionCommand().equals("Open map")) {
					Mapper.buildURL(server);
				}
				if (ae.getActionCommand().equals("Add")) {
					if (errorCheck(fields)) {
						try {
							Supplier supplier = server.addSupplier(((JTextField) fields.get(0)).getText(),
									new Postcode(((JComboBox) fields.get(1)).getSelectedItem().toString()));
							((JTextField) fields.get(0)).setText("");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
						}
					}
					((JComboBox) ingredientBox.get(2)).removeAllItems();
					for (Supplier s : server.getSuppliers()) {
						((JComboBox) ingredientBox.get(2)).addItem(s.getName());
					}
					;
				}
				if (ae.getActionCommand().equals("Delete")) {
					int indexCol = 0;
					int indexRow = supplierTable.getSelectedRow();
					boolean carry = true;
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						for (Ingredient i : server.getIngredients()) {
							if (i.getSupplier().getName().equals(supplierTable.getValueAt(indexRow, indexCol))) {
								JOptionPane.showMessageDialog(null, "Ingredient is using this supplier", "Error", 0);
								carry = false;
								break;
							}
						}
						if (carry) {
							for (Supplier d : server.getSuppliers()) {
								if (supplierTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
									try {
										server.removeSupplier(d);
										break;
									} catch (UnableToDeleteException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}

	}

	public void populateIngredients() {
		String[] columnNamesIngredients = { "Name", "Unit", "Supplier", "Restock amount", "Restock Threshold" };
		String[][] data = new String[server.getIngredients().size()][5];
		ingredientTable = addTable(ingredients, data, columnNamesIngredients);
		ArrayList<JButton> buttons = addButton(ingredients, true,false);
		ArrayList fields = addTextField(ingredients, columnNamesIngredients);
		ingredientBox = fields;
		for (Supplier s : server.getSuppliers()) {
			((JComboBox) fields.get(2)).addItem(s.getName());
		}
		DefaultTableModel model = (DefaultTableModel) ingredientTable.getModel();
		for (Ingredient d : server.getIngredients()) {
			model.addRow(new String[] { d.getName().toString(), d.getUnit(), d.getSupplier().toString(),
					d.getRestockAmount().toString(), d.getRestockThreshold().toString() });
		}
		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (ae.getActionCommand().equals("Add")) {
					Supplier store = null;
					for (Supplier s : server.getSuppliers()) {
						if (s.getName().equals(((JComboBox) fields.get(2)).getSelectedItem()))
							store = s;
					}
					if (errorCheck(fields)) {
						try {
							server.addIngredient(((JTextField) fields.get(0)).getText(),
									((JTextField) fields.get(1)).getText(), store,
									Integer.parseInt(((JTextField) fields.get(3)).getText()),
									Integer.parseInt(((JTextField) fields.get(4)).getText()));
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
						}

					}
					((JTextField) fields.get(0)).setText("");
					((JTextField) fields.get(1)).setText("");
					((JTextField) fields.get(3)).setText("");
					((JTextField) fields.get(4)).setText("");

				}
				if (ae.getActionCommand().equals("Delete")) {
					boolean carry = true;
					int indexRow = ingredientTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Ingredient d : server.getIngredients()) {
							if (ingredientTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								for (Dish i : server.getDishes()) {
									if (!(i.getRecipe().get(d) == null)) {
										JOptionPane.showMessageDialog(null, i.getName() + " is using this Ingredient",
												"Error", 0);
										carry = false;
										break;
									}
								}
								if (carry) {
									try {
										server.removeIngredient(d);
										break;
									} catch (UnableToDeleteException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				if (ae.getActionCommand().equals("Edit")) {
					int indexRow = ingredientTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Ingredient d : server.getIngredients()) {
							if (ingredientTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								updateValues(d, ingredientTable, indexRow);
								break;
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}
	}

	public void populateDishes() {
		String[] columnNamesDishes = { "Name", "Description", "Price", "Restock amount", "Restock Threshold" };
		String[][] data = new String[server.getDishes().size()][7];

		dishTable = addTable(dishes, data, columnNamesDishes);
		ArrayList<JButton> buttons = addButton(dishes, true,false);
		String[] lables = { "Name", "Description", "Price", "Restock amount", "Restock Threshold" };
		ArrayList<JTextField> fields = addTextField(dishes, lables);
		DefaultTableModel model = (DefaultTableModel) dishTable.getModel();
		for (Dish d : server.getDishes()) {
			model.addRow(new String[] { d.getName().toString(), d.getDescription(), d.getPrice().toString(),
					d.getRestockAmount().toString(), d.getRestockThreshold().toString() });
		}
		ActionListener printListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (ae.getActionCommand().equals("Add")) {
					if (errorCheck(fields)) {
						try {
							Dish dish = server.addDish(fields.get(0).getText(), fields.get(1).getText(),
									Integer.parseInt(fields.get(2).getText()),
									Integer.parseInt(fields.get(3).getText()),
									Integer.parseInt(fields.get(4).getText()));
							fields.get(0).setText("");
							fields.get(1).setText("");
							fields.get(2).setText("");
							fields.get(3).setText("");
							fields.get(4).setText("");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
						}
					}
				}
				if (ae.getActionCommand().equals("Delete")) {
					int indexRow = dishTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Dish d : server.getDishes()) {
							if (dishTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								try {
									server.removeDish(d);
									break;
								} catch (UnableToDeleteException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				if (ae.getActionCommand().equals("Edit")) {
					int indexRow = dishTable.getSelectedRow();
					if (indexRow == -1) {
						JOptionPane.showMessageDialog(null, "Please select a row", "Error", 0);
					} else {
						int indexCol = 0;
						for (Dish d : server.getDishes()) {
							if (dishTable.getValueAt(indexRow, indexCol).equals(d.getName())) {
								updateValues(d, dishTable, indexRow);
								break;
							}
						}
					}
				}
			}
		};

		for (JButton b : buttons) {
			b.addActionListener(printListener);
		}

	}

	public void populateOrders() {
		String[] columnNamesOrders = { "Date", "Status", "Distance" };
		String[][] data = new String[server.getOrders().size()][3];

		orderTable = addTable(orders, data, columnNamesOrders);
		DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
		for (Order d : server.getOrders()) {
			model.addRow(new String[] { d.getName().toString(),(server.getOrderStatus(d)),(server.getOrderDistance(d)).toString() });
		}
	}

	public void populateUsers() {
		String[] columnNamesUsers = { "Name", "Distance", "Postcode" };
		String[][] data = new String[server.getUsers().size()][3];
		userTable = addTable(users, data, columnNamesUsers);
		DefaultTableModel model = (DefaultTableModel) userTable.getModel();
		for (User d : server.getUsers()) {
			model.addRow(
					new String[] { d.getName().toString(), d.getDistance().toString(), d.getPostcode().toString() });
		}
	}

	public void updateValues(Object object, JTable table, int indexRow) {
		JSpinner xField = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		JSpinner yField = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		xField.setEditor(new JSpinner.DefaultEditor(xField));
		yField.setEditor(new JSpinner.DefaultEditor(yField));

		JPanel totalPanel = new JPanel();
		ArrayList<JLabel> listOfIng = new ArrayList<JLabel>();
		ArrayList<JSpinner> listOfFields = new ArrayList<JSpinner>();
		JPanel myPanel = new JPanel();
		DefaultTableModel model2 = (DefaultTableModel) dishTable.getModel();

		totalPanel.setLayout(new GridLayout(2, 1));
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(new JLabel("Restock Amount: "));
		myPanel.add(xField);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Restock Threshold: "));
		myPanel.add(yField);
		totalPanel.add(myPanel);

		listOfFields.add(xField);
		listOfFields.add(yField);

		if (object instanceof Dish) {
			JPanel bottomPanel = new JPanel();
			for (Ingredient i : server.getIngredients()) {
				JLabel box = new JLabel(i.getName());
				listOfIng.add(box);
				bottomPanel.add(Box.createHorizontalStrut(15)); // a spacer
				bottomPanel.add(box);
				bottomPanel.add(new JLabel("Amount: "));
				JSpinner field = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
				field.setEditor(new JSpinner.DefaultEditor(field));
				bottomPanel.add(field);
				listOfFields.add(field);
			}
			totalPanel.add(bottomPanel);
		}
		int result = JOptionPane.showConfirmDialog(null, totalPanel, "Please enter the correct values",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			if (object instanceof Ingredient) {
				{
					try {
						yField.commitEdit();
						xField.commitEdit();
						server.setRestockLevels(((Ingredient) object), (Integer) (yField.getValue()),
								(Integer) (xField.getValue()));
						table.setValueAt(xField.getValue(), indexRow, 3);
						table.setValueAt(yField.getValue(), indexRow, 4);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
					}
				}
			}

			if (object instanceof Dish) {
				{
					try {
						yField.commitEdit();
						xField.commitEdit();
						server.setRestockLevels(((Dish) object), (Integer) (yField.getValue()),
								(Integer) (xField.getValue()));
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
					}
					table.setValueAt(xField.getValue(), indexRow, 3);
					table.setValueAt(yField.getValue(), indexRow, 4);
					int count = 0;
					for (Ingredient i : server.getIngredients()) {
						server.removeIngredientFromDish((Dish) object, i);
						if (i.getName().equals(listOfIng.get(count).getText())) {
							try {
								listOfFields.get(count +2).commitEdit();
								dishTable.setValueAt(listOfFields.get(count + 2).getValue(), indexRow,
										dishTable.getColumn(listOfIng.get(count).getText()).getModelIndex());
								server.addIngredientToDish((Dish) object, i,
										(Integer) (listOfFields.get(count + 2).getValue()));
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Error one of the inputs is invalid", "Error", 0);
								break;
							}
						}
						count++;
					}
				}
			}
		}
	}

	public void startTimer() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		int timeInterval = 5;
		scheduler.scheduleAtFixedRate(() -> refreshAll(), 0, timeInterval, TimeUnit.SECONDS);
	}

	/**
	 * Refresh all parts of the server application based on receiving new data,
	 * calling the server afresh
	 */
	public void refreshAll() {
		for (Order d : server.getOrders()) {
			DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
			model.setRowCount(0);
			model.addRow(new String[] { d.getName().toString(),(server.getOrderStatus(d)),(server.getOrderDistance(d)).toString() });
		}
		
		DefaultTableModel model = (DefaultTableModel) droneTable.getModel();
		
		if (server.getDrones().size() != droneTable.getRowCount()) {
			model.setRowCount(0);
			for (Drone d : server.getDrones()) {
				model.addRow(new String[] { d.getName().toString(), d.getSpeed().toString(), server.getDroneStatus(d) });
			}
		}
		int droneCount = 0;
		for(Drone d: server.getDrones()) {
			model.setValueAt(server.getDroneStatus(d), droneCount, 2);
			droneCount++;
		}
		model = (DefaultTableModel) staffTable.getModel();
		if (server.getStaff().size() != staffTable.getRowCount()) {
			model.setRowCount(0);
			for (Staff d : server.getStaff()) {
				model.addRow(new String[] { d.getName().toString(),  server.getStaffStatus(d), d.getFatigue().toString() });
			}
		}
		int staffCount = 0;
		for(Staff d: server.getStaff()) {
			model.setValueAt(server.getStaffStatus(d), staffCount, 1);
			staffCount++;
		}
		if (server.getPostcodes().size() != postcodeTable.getRowCount()) {
			model = (DefaultTableModel) postcodeTable.getModel();
			model.setRowCount(0);
			for (Postcode d : server.getPostcodes()) {
				model.addRow(new String[] { d.getName().toString(), d.getDistance().toString(),
						d.getLatLong().get("lat").toString(), d.getLatLong().get("lon").toString() });
			}
		}
		if (server.getSuppliers().size() != supplierTable.getRowCount()) {
			model = (DefaultTableModel) supplierTable.getModel();
			model.setRowCount(0);
			for (Supplier d : server.getSuppliers()) {
				model.addRow(new String[] { d.getName().toString(), d.getDistance().toString(),
						d.getPostcode().toString() });
			}
		}
		DefaultTableModel model2 = (DefaultTableModel) dishTable.getModel();
		if (server.getDishes().size() != dishTable.getRowCount()) {
			model2.setRowCount(0);
			int rowcount = 0;
			for (Dish d : server.getDishes()) {
				model2.addRow(new String[] { d.getName().toString(), d.getDescription(), d.getPrice().toString(),
						d.getRestockAmount().toString(), d.getRestockThreshold().toString() });
				for (int counter = 5; counter < model2.getColumnCount(); counter++) {
					Iterator i = d.getRecipe().entrySet().iterator();
					while (i.hasNext()) {
						Map.Entry me = (Map.Entry) i.next();
						if (me.getKey().toString().equals(model2.getColumnName(counter))) {
							model2.setValueAt(me.getValue(), rowcount, counter);
						}
					}
				}
				rowcount++;
			}

		}
		if (server.getIngredients().size() != ingredientTable.getRowCount()) {
			model = (DefaultTableModel) ingredientTable.getModel();
			model.setRowCount(0);
			model2.setRowCount(0);
			model2.setColumnCount(0);
			String[] columnNamesDishes = { "Name", "Description", "Price", "Restock amount", "Restock Threshold" };
			for (String s : columnNamesDishes) {
				model2.addColumn(s);
			}
			for (Ingredient d : server.getIngredients()) {
				model2.addColumn(d.getName());
				model.addRow(new String[] { d.getName().toString(), d.getUnit(), d.getSupplier().toString(),
						d.getRestockAmount().toString(), d.getRestockThreshold().toString() });
			}
			int rowcount = 0;
			for (Dish d : server.getDishes()) {
				model2.addRow(new String[] { d.getName().toString(), d.getDescription(), d.getPrice().toString(),
						d.getRestockAmount().toString(), d.getRestockThreshold().toString() });
				for (int counter = 5; counter < model2.getColumnCount(); counter++) {
					Iterator i = d.getRecipe().entrySet().iterator();
					while (i.hasNext()) {
						Map.Entry me = (Map.Entry) i.next();
						if (me.getKey().toString().equals(model2.getColumnName(counter))) {
							model2.setValueAt(me.getValue(), rowcount, counter);
						}
					}
				}
				rowcount++;
			}
		}
	}

	@Override
	/**
	 * Respond to the model being updated by refreshing all data displays
	 */
	public void updated(UpdateEvent updateEvent) {

		refreshAll();
	}

}

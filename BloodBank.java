import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BloodBank extends Frame implements ActionListener {
    private Button donateButton, displayButton, removeButton, donorHistoryButton;
    private Label nameLabel, bloodGroupLabel, userIdLabel;
    private TextField nameTextField, userIdTextField;
    private Choice bloodGroupChoice; // Added Choice for selecting blood group

    public BloodBank() {
        // Initialize components
        donateButton = new Button("Donate Blood");
        displayButton = new Button("Display Donors");
        removeButton = new Button("Remove Donor");
        donorHistoryButton = new Button("Donor History");
        nameLabel = new Label("Name:");
        bloodGroupLabel = new Label("Blood Group:");
        userIdLabel = new Label("User ID:");
        nameTextField = new TextField();
        userIdTextField = new TextField();

        // Initialize blood group choice
        bloodGroupChoice = new Choice();
        bloodGroupChoice.add("A+");
        bloodGroupChoice.add("A-");
        bloodGroupChoice.add("B+");
        bloodGroupChoice.add("B-");
        bloodGroupChoice.add("AB+");
        bloodGroupChoice.add("AB-");
        bloodGroupChoice.add("O+");
        bloodGroupChoice.add("O-");

        // Set layout manager to GridLayout
        setLayout(new GridLayout(8, 2));  // 8 rows, 2 columns

        // Add components to the frame
        add(nameLabel);
        add(nameTextField);
        add(bloodGroupLabel);
        add(bloodGroupChoice); // Use Choice for selecting blood group
        add(userIdLabel);
        add(userIdTextField);

        // Add empty labels for spacing
        add(new Label(""));
        add(new Label(""));

        // Add buttons
        add(donateButton);
        add(displayButton);
        add(removeButton);
        add(donorHistoryButton);

        // Register event listeners for the buttons
        donateButton.addActionListener(this);
        displayButton.addActionListener(this);
        removeButton.addActionListener(this);
        donorHistoryButton.addActionListener(this);

        // Set frame properties (title, size, etc.)
        setTitle("Blood Bank Management System");
        setSize(500, 400);
        setLocationRelativeTo(null);  // Center the frame on the screen
        setResizable(false);  // Prevent resizing
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == donateButton) {
            String donorName = nameTextField.getText();
            String bloodGroup = bloodGroupChoice.getSelectedItem(); // Get selected blood group from Choice

            // Call the backend to insert donor information into the database and get the user ID
            int userId = BloodBankDatabase.insertDonor(donorName, bloodGroup);

            // Display the user ID
            userIdTextField.setText(String.valueOf(userId));

            // You might want to display a confirmation message or update the GUI accordingly
            System.out.println("Donation recorded for: User ID " + userId + ", Blood Group: " + bloodGroup);
        } else if (e.getSource() == displayButton) {
            // Call the backend to display all donors with their blood groups
            displayAllDonors();
        } else if (e.getSource() == removeButton) {
            // Call the backend to remove a donor
            removeDonor();
        } else if (e.getSource() == donorHistoryButton) {
            // Display donor history
            displayDonorHistory();
        }
    }

    private void displayAllDonors() {
        try {
            ResultSet resultSet = BloodBankDatabase.getAllDonors();
    
            System.out.println("All Donors:");
            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String bloodGroup = resultSet.getString("blood_group");
                System.out.println("User ID: " + userId + ", Blood Group: " + bloodGroup);
            }
    
            // Display the count of each blood group
            displayBloodGroupCount();
    
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void displayBloodGroupCount() {
        try {
            ResultSet countResultSet = BloodBankDatabase.getBloodGroupCount();
    
            System.out.println("\nBlood Group Counts:");
            while (countResultSet.next()) {
                String bloodGroup = countResultSet.getString("blood_group");
                int count = countResultSet.getInt("count");
                System.out.println("Blood Group: " + bloodGroup + ", Count: " + count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    

    private void removeDonor() {
        // Create a simple dialog box for user input
        Frame inputFrame = new Frame("Remove Donor");
        Label label = new Label("User ID:");
        TextField textField = new TextField();
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel"); // Added Cancel button

        inputFrame.setLayout(new GridLayout(2, 3));
        inputFrame.add(label);
        inputFrame.add(textField);
        inputFrame.add(okButton);
        inputFrame.add(new Label(""));
        inputFrame.add(new Label(""));
        inputFrame.add(cancelButton);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userIdToRemove = textField.getText();
                inputFrame.dispose(); // Close the input frame

                if (!userIdToRemove.isEmpty()) {
                    try {
                        int userId = Integer.parseInt(userIdToRemove);
                        boolean removed = BloodBankDatabase.removeDonor(userId);

                        if (removed) {
                            System.out.println("Donor with User ID " + userId + " removed successfully.");
                        } else {
                            System.out.println("Donor with User ID " + userId + " not found or removal failed.");
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input for User ID.");
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputFrame.dispose(); // Close the input frame
            }
        });

        inputFrame.setSize(300, 100);
        inputFrame.setLocationRelativeTo(this);
        inputFrame.setVisible(true);
    }

    private void displayDonorHistory() {
        try {
            ResultSet resultSet = BloodBankDatabase.getDonorHistory();

            System.out.println("Donor History:");
            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String donorName = resultSet.getString("name");
                String bloodGroup = resultSet.getString("blood_group");
                System.out.println("User ID: " + userId + ", Donor: " + donorName + ", Blood Group: " + bloodGroup);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BloodBank();
    }
}

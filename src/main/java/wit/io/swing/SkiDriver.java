package wit.io.swing;

// Java Program to create a SkiDriver class to run
// the toast class
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.exceptions.*;
import wit.io.managers.SkiManager;
import wit.io.managers.SkiTypeManager;
import wit.io.utils.Const;
import wit.io.utils.Writeable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class SkiDriver {

    //region Variables
    // create a frame
    static JFrame mainFrame;

    static SkiDriver driver;

    static JPanel tabSpace;


    // managers
    static SkiTypeManager skiTypeManager;
    static SkiManager skiManager;
    

    //endregion Variables


    //region Init

    private static boolean managersSetup() throws SkiAppException {
        try {
            skiTypeManager = new SkiTypeManager(Const.SkiTypeFilePath);
            skiManager = new SkiManager(Const.SkiFilePath);
        } catch (ReadingException e)
        {
            System.out.println("Failed to create Manager");
            return false;
        }

        skiManager.resetEntityData();
        skiTypeManager.resetEntityData();

        populateData();
        return true;
    }


    private static void populateData() throws SkiAppException {
        skiManager.resetEntityData();
        skiTypeManager.resetEntityData();

        SkiType skiType1 = new SkiType("hello", "world");
        SkiType skiType2 = new SkiType("kill", "mee");
        skiTypeManager.addEntity(skiType1);
        skiTypeManager.addEntity(skiType2);


        Ski ski1 = new Ski(skiType1, "marka_a", "super", "ekstra", 10f);
        Ski ski2 = new Ski(skiType2, "marka_b", "kiepski", "zwykle", 20f);
        Ski ski3 = new Ski(skiType1, "marka_c", "kiepski", "zwykle", 5f);
        Ski ski4 = new Ski(skiType2, "marka_d", "kiepski", "zwykle", 3f);
        Ski ski5 = new Ski(skiType1, "marka_e", "kiepski", "ekstra", 50f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);
        skiManager.addEntity(ski3);
        skiManager.addEntity(ski4);
        skiManager.addEntity(ski5);

    }


    private static void createMainFrame() {
        // create the frame
        mainFrame = new JFrame("SkiApp");


        // Window Closing
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });


        // setSize
        mainFrame.setSize(1000, 500);
    }

    //endregion Init

    //region Helpers

    public void refresh() {
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }


    /**
     * https://stackoverflow.com/a/70393691/17491940
     * @param x, y
     * @return
     */
    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int gap = 3;
        gbc.insets = new Insets(gap, gap + 2 * gap * x, gap, gap);
        return gbc;
    }
    private static GridBagConstraints createGbc(int x, int y, int width) {
        GridBagConstraints gbc = createGbc(x, y);
        gbc.gridwidth = width;
        return gbc;
    }

    //endregion Helpers


    //region Generic Tab elements

    private interface searchableTab<E extends Writeable> {
        void selectedItem(E selectedObject);
    }

    private interface EntityPanel {
        void createItem();
        void editItem();
        void deleteItem();
    }


    private static class SearchedPositionButton<E extends Writeable> extends Button implements ActionListener {
        E storedEntity;
        searchableTab<E> tab;

        SearchedPositionButton(String buttonText, E storedEntity_, searchableTab<E> tab_) {
            super(buttonText);
            this.storedEntity = storedEntity_;
            this.tab = tab_;
        }

        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            tab.selectedItem(storedEntity);
        }

    }


    private static class CreateNewEntityButton extends Button implements ActionListener {
        EntityPanel panel;
        CreateNewEntityButton(String buttonText, EntityPanel panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.createItem();
        }
    }
    private static class EditEntityButton extends Button implements ActionListener {
        EntityPanel panel;
        EditEntityButton(String buttonText, EntityPanel panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.editItem();
        }
    }
    private static class DeleteEntityButton extends Button implements ActionListener {
        EntityPanel panel;
        DeleteEntityButton(String buttonText, EntityPanel panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.deleteItem();
        }
    }

    //endregion Generic Tab elements


    //region SkiType

    private static class SkiAppTab extends JPanel implements searchableTab<SkiType>{
        SkiTypeSearchPanel skiTypeSearchPanel;
        SkiTypeEntityPanel skiTypeEntityPanel;
        SkiAppTab() {
            skiTypeSearchPanel = new SkiTypeSearchPanel(this);
            add(skiTypeSearchPanel);

            skiTypeEntityPanel = new SkiTypeEntityPanel(this);
            add(skiTypeEntityPanel);
        }
        

        public void repeatSearch() {
            skiTypeSearchPanel.repeatSearch();
        }


        @Override
        public void selectedItem(SkiType selectedSkiType) {
            //System.out.println(selectedSkiType.getName());
            skiTypeEntityPanel.entityLoaded(selectedSkiType);
        }
        
        
    }


    private static class SkiTypeSearchPanel extends JPanel implements ActionListener {
        JPanel searchResultsPanel;
        JTextField searchNameTextField;
        JTextField searchDescriptionTextField;
        SkiAppTab parent;
        SkiTypeSearchPanel(SkiAppTab parent_) {
            this.parent = parent_;
            setLayout(new GridBagLayout());

            add(new JLabel("Name:"), createGbc(0, 0));
            add(new JLabel("Description:"), createGbc(0, 1));


            this.searchNameTextField = new JTextField(16);
            this.searchDescriptionTextField = new JTextField(16);

            add(this.searchNameTextField, createGbc(1, 0));
            add(this.searchDescriptionTextField,  createGbc(1, 1));


            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);
            add(searchButton, createGbc(0, 2, 2));

            this.searchResultsPanel = new JPanel();
            add(searchResultsPanel, createGbc(0, 3));

            
            // Show all items
            ArrayList<SkiType> results = skiTypeManager.search(null, null);
            loadSearchResults(results);
        }
        public void actionPerformed(ActionEvent e) {
            ArrayList<SkiType> results = skiTypeManager.search(searchNameTextField.getText(), searchDescriptionTextField.getText());
            loadSearchResults(results);
        }

        /**
         * Once Entity is either added/removed search bar should refresh
         */
        public void repeatSearch() {
            ArrayList<SkiType> results = skiTypeManager.search(searchNameTextField.getText(), searchDescriptionTextField.getText());
            loadSearchResults(results);
        }

        public void loadSearchResults(ArrayList<SkiType> searchResults) {
            searchResultsPanel.removeAll();

            int number_of_results = searchResults.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);

            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (SkiType skiItem : searchResults) {
                System.out.println(skiItem.toString());
                SearchedPositionButton<SkiType> skiTypeResult = new SearchedPositionButton<>(skiItem.getName(), skiItem, parent);

                skiTypeResult.addActionListener(skiTypeResult);

                searchResultsPanel.add(skiTypeResult);
            }
            driver.refresh();
            System.out.println("End of Search");
        }
    }


    private static class SkiTypeEntityPanel extends JPanel implements ActionListener, EntityPanel {
        JTextField selectedItemName;
        JTextField selectedItemDescription;

        SkiType selectedEntity;

        SkiAppTab parent;

        SkiTypeEntityPanel(SkiAppTab parent_) {
            this.parent = parent_;
            // Elements
            selectedItemName = new JTextField("");
            selectedItemDescription = new JTextField("");
            selectedItemName.setPreferredSize( new Dimension(200, 24));
            selectedItemDescription.setPreferredSize( new Dimension(200, 24));

            JLabel nameLabel = new JLabel("Name:  ");
            JLabel descriptionLabel = new JLabel("Description:  ");


            CreateNewEntityButton createNewEntityButton = new CreateNewEntityButton("New", this);
            createNewEntityButton.addActionListener(createNewEntityButton);

            EditEntityButton editEntityButton = new EditEntityButton("Edit", this);
            editEntityButton.addActionListener(editEntityButton);

            DeleteEntityButton deleteEntityButton = new DeleteEntityButton("Delete", this);
            deleteEntityButton.addActionListener(deleteEntityButton);


            //Layout


            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();


            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            int gap = 3;
            gbc.insets = new Insets(gap, gap, gap, gap);
            gbc = createGbc(0, 0);
            gbc.weightx = 0.5;

            // Top left
            add(nameLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemName, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(descriptionLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemDescription, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;


            add(createNewEntityButton, gbc);
            gbc.gridx += 1;
            add(editEntityButton, gbc);
            gbc.gridx += 1;
            add(deleteEntityButton, gbc);
        }

        public void entityLoaded(SkiType selectedEntity) {
            selectedItemName.setText(selectedEntity.getName());
            selectedItemDescription.setText(selectedEntity.getDescription());
            this.selectedEntity = selectedEntity;
        }

        public void actionPerformed(ActionEvent e) {
           
        }

        @Override
        public void createItem() {
            System.out.println("Create");
            SkiType newSkiType = new SkiType(selectedItemName.getText(), selectedItemDescription.getText());


            try {
                skiTypeManager.addEntity(newSkiType);
            } catch (SkiAppException e) {
                System.out.println("Failed to create new SkiType: " + e);
            }
            parent.repeatSearch();
        }

        @Override
        public void editItem() {
            System.out.println("Edit");
            SkiType newSkiType = new SkiType(selectedItemName.getText(), selectedItemDescription.getText());

            try {
                skiTypeManager.editEntity(selectedEntity, newSkiType);
            } catch (SkiAppException e) {
                System.out.println("Failed to edit SkiType: " + e);
            }
            parent.repeatSearch();
        }

        @Override
        public void deleteItem() {
            System.out.println("Delete");
            try {
                skiTypeManager.removeEntity(selectedEntity);
            } catch (SkiAppException e) {
                System.out.println("Failed to delete SkiType: " + e);
            }
            parent.repeatSearch();
        }
    }

    //endregion SkiType


    //region Main Screen

    private static class TabButton extends Button implements ActionListener {
        private final JPanel panel;

        TabButton(String buttonText, JPanel panel_){
            super(buttonText);
            this.panel = panel_;
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("button is pressed  " + this.getLabel());
            driver.loadNewTab(panel);
        }
    }


    public void loadNewTab(JPanel newTab) {
        tabSpace.removeAll();
        tabSpace.add(newTab);
        refresh();
    }

    //endregion Main Screen


    public static void main(String[] args) throws SkiAppException {
        boolean success = managersSetup();
        if (!success){
            return;
        }

        createMainFrame();
        
        // reference to the main object
        driver = new SkiDriver();

        SkiAppTab skiAppTab = new SkiAppTab();

        //TODO TEMP
        JPanel emptyTestPanel = new JPanel();
        emptyTestPanel.add(new JLabel("Empty content"));


        // Main Space
        tabSpace = new JPanel();
        tabSpace.add(skiAppTab); // TODO remember last selected tab by user
        

        mainFrame.setLayout(new GridBagLayout());


        // Tab Selection Buttons
        TabButton skiTypesTabButton = new TabButton("skitype", skiAppTab);
        TabButton skiTabButton = new TabButton("ski", emptyTestPanel);
        skiTypesTabButton.addActionListener(skiTypesTabButton);
        skiTabButton.addActionListener(skiTabButton);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int gap = 3;
        gbc.insets = new Insets(0, 0, gap, 0);
        gbc = createGbc(0, 0);
        gbc.weightx = 0.5;
        mainFrame.add(skiTypesTabButton, gbc);


        gbc.gridx = 1;
        mainFrame.add(skiTabButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        gbc.weighty = 1.0;
        gbc.weightx = 1.0;

        mainFrame.add(tabSpace, gbc);


        driver.refresh();
    }
}

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
        mainFrame.setSize(500, 500);
    }

    //endregion Init

    private interface searchableTab<E extends Writeable> {
        void found(E selectedObject);
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

            tab.found(storedEntity);
        }

    }


    private static class SkiAppTab extends JPanel implements searchableTab<SkiType>{
        SkiTypeSearchPanel skiTypeSearchPanel;
        SkiTypeEntityPanel skiTypeEntityPanel;
        SkiAppTab() {
            skiTypeSearchPanel = new SkiTypeSearchPanel(this);
            add(skiTypeSearchPanel);

            skiTypeEntityPanel = new SkiTypeEntityPanel();
            add(skiTypeEntityPanel);
        }

        @Override
        public void found(SkiType selectedSkiType) {

            System.out.println(selectedSkiType.getName());
            skiTypeEntityPanel.entityLoaded(selectedSkiType);
        }
    }

    private static class SkiTypeSearchPanel extends JPanel implements ActionListener {
        JPanel searchResultsPanel;
        JTextField searchTextField;
        SkiAppTab parent;
        SkiTypeSearchPanel(SkiAppTab parent_) {
            this.parent = parent_;
            setLayout(new GridLayout(2, 1));
            // textfield
            this.searchTextField = new JTextField(16);
            add(searchTextField);
            
            // button
            Button searchButton = new Button("search");

            // add action listener
            searchButton.addActionListener(this);

            add(searchButton);


            this.searchResultsPanel = new JPanel();
            add(searchResultsPanel);

            // Show all items
            ArrayList<SkiType> results = skiTypeManager.search(null, null);
            loadSearchResults(results);
        }
        public void actionPerformed(ActionEvent e) {
            ArrayList<SkiType> results = skiTypeManager.search(searchTextField.getText(), null);
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

    private static class SkiTypeEntityPanel extends JPanel implements ActionListener {
        JLabel selectedItemName;
        JLabel selectedItemDescription;
        SkiTypeEntityPanel() {
            //Labels
            selectedItemName = new JLabel("test");
            selectedItemDescription = new JLabel("dwa");

            add(selectedItemName);
            add(selectedItemDescription);
        }

        public void entityLoaded(SkiType selectedEntity) {
            selectedItemName.setText(selectedEntity.getName());
            selectedItemDescription.setText(selectedEntity.getDescription());
        }

        public void actionPerformed(ActionEvent e) {
           
        }
    }

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
    public void refresh() {
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    public void loadNewTab(JPanel newTab) {
        tabSpace.removeAll();
        tabSpace.add(newTab);
        refresh();
    }


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
        

        mainFrame.setLayout(new GridLayout(2, 2));

        // Tab Selection Buttons
        TabButton skiTypesTabButton = new TabButton("skitype", skiAppTab);
        TabButton skiTabButton = new TabButton("ski", emptyTestPanel);
        skiTypesTabButton.addActionListener(skiTypesTabButton);
        skiTabButton.addActionListener(skiTabButton);

        
        mainFrame.add(skiTypesTabButton);
        mainFrame.add(skiTabButton);
        mainFrame.add(tabSpace);

        driver.refresh();
    }
}

package wit.io.swing;

// Java Program to create a SkiDriver class to run
// the toast class
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.exceptions.*;
import wit.io.managers.Manager;
import wit.io.managers.SkiManager;
import wit.io.managers.SkiTypeManager;
import wit.io.utils.Const;
import wit.io.utils.Writeable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

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
     * <a href="https://stackoverflow.com/a/70393691/17491940">...</a>
     * @param x, y
     * @return parameter to be used alongside add method to determine layout of new element
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


    /**
     * Indian homie helped us!
     * <a href="https://www.tutorialspoint.com/how-can-we-implement-auto-complete-jcombobox-in-java">...</a>
     */
    static class AutoCompleteComboBox extends JComboBox {
        public int caretPos = 0;
        public JTextField tfield = null;
        public AutoCompleteComboBox(final Object[] countries) {
            super(countries);
            setEditor(new BasicComboBoxEditor());
            setEditable(true);
        }
        public void setSelectedIndex(int index) {
            super.setSelectedIndex(index);
            tfield.setText(getItemAt(index).toString());
            tfield.setSelectionEnd(caretPos + tfield.getText().length());
            tfield.moveCaretPosition(caretPos);
        }
        public void setEditor(ComboBoxEditor editor) {
            super.setEditor(editor);
            if(editor.getEditorComponent() instanceof JTextField) {
                tfield = (JTextField) editor.getEditorComponent();
                tfield.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent ke) {
                        char key = ke.getKeyChar();
                        if (!(Character.isLetterOrDigit(key) || Character.isSpaceChar(key) )) return;
                        caretPos = tfield.getCaretPosition();
                        String text="";
                        try {
                            text = tfield.getText(0, caretPos);
                        } catch (javax.swing.text.BadLocationException e) {
                            e.printStackTrace();
                        }
                        for (int i=0; i < getItemCount(); i++) {
                            String element = (String) getItemAt(i);
                            if (element.startsWith(text)) {
                                setSelectedIndex(i);
                                return;
                            }
                        }
                    }
                });
            }
        }
    }




    //endregion Helpers


    //region Generic Tab elements

    private interface searchableTab<E extends Writeable> {
        void selectedItem(E selectedObject);
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

    // Entity Edit BUTTONS
    private static class CreateNewEntityButton<E extends Writeable, M extends Manager<E>> extends Button implements ActionListener {
        EntityPanel<E, M> panel;
        CreateNewEntityButton(String buttonText, EntityPanel<E, M> panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.createItem();
            panel.repeatSearch();
        }
    }
    private static class EditEntityButton<E extends Writeable, M extends Manager<E>> extends Button implements ActionListener {
        EntityPanel<E, M> panel;
        EditEntityButton(String buttonText, EntityPanel<E, M> panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.editItem();
            panel.repeatSearch();
        }
    }
    private static class DeleteEntityButton<E extends Writeable, M extends Manager<E>> extends Button implements ActionListener {
        EntityPanel<E, M> panel;
        DeleteEntityButton(String buttonText, EntityPanel<E, M> panel_) {
            super(buttonText);
            this.panel = panel_;
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.deleteItem();
            panel.repeatSearch();
        }
    }

    //endregion Generic Tab elements


    //region Generic Tab Core

    private static abstract class GenericAppTab<E extends Writeable, M extends Manager<E>> extends JPanel implements searchableTab<E> {
        SearchPanel<E, M> searchPanel;
        EntityPanel<E, M> entityPanel;


        public final void repeatSearch() {
            searchPanel.repeatSearch();
        }

        public final void selectedItem(E selectedEntity) {
            entityPanel.entityLoaded(selectedEntity);
        }
    }


    private static abstract class SearchPanel<E extends Writeable, M extends Manager<E>> extends JPanel implements ActionListener  {
        //TODO make use of generic manager to call search inside abstract class
        //TODO implement safer constructor
        GenericAppTab<E, M> parent;
        JPanel searchResultsPanel;
        
        protected abstract ArrayList<E> performSearch(); // TODO make it even more secure, by forcing overriding of arguments collection

        public final void actionPerformed(ActionEvent e) {
            loadSearchResults(performSearch());
        }

        /**
         * Once Entity is either added/removed search bar should refresh
         */
        public final void repeatSearch() {
            loadSearchResults(performSearch());
        }

        abstract void loadSearchResults(ArrayList<E> searchResults);
    }


    private static abstract class EntityPanel<E extends Writeable, M extends Manager<E>> extends JPanel {
        E selectedEntity;
        M manager;
        GenericAppTab<E, M> parent;

        final protected void repeatSearch() {
            parent.repeatSearch();
        }


        abstract E loadItemData();

        /**
         * Called only by entityLoaded()
         * @param selectedEntity_
         */
        protected abstract void onEntityLoaded(E selectedEntity_);


        /**
         * Is called by SearchPanel
         * @param selectedEntity_
         */
        public final void entityLoaded(E selectedEntity_) {
            selectedEntity = selectedEntity_;
            onEntityLoaded(selectedEntity_);
        }

        final void createItem() {
            try {
                E newEntity = loadItemData();
                manager.addEntity(newEntity);
            } catch (SkiAppException e) { // TODO consider Split error message so that it can cover case if loading item data was successful
                System.out.println("Failed to create new SkiType. Error: " + e);
            }

        }
        final void editItem() {
            try {
                manager.editEntity(selectedEntity, loadItemData());
            } catch (SkiAppException e) {
                System.out.println("Failed to edit: " + selectedEntity.toString() + "  Error: " + e);
            }
        }
        final void deleteItem() {
            try {
                manager.removeEntity(selectedEntity);
            } catch (SkiAppException e) {
                System.out.println("Failed to delete: " + selectedEntity.toString() + "  Error: " + e);
            }
        }
    }

    //endregion Generic Tab Core


    //region SkiType

    private static class SkiTypeAppTab extends GenericAppTab<SkiType, SkiTypeManager> {
        SkiTypeAppTab(SkiTypeManager manager_) {
            searchPanel = new SkiTypeSearchPanel(manager_, this);
            add(searchPanel);

            entityPanel = new SkiTypeEntityPanel(manager_, this);
            add(entityPanel);
        }
    }
    
    
    private static class SkiTypeSearchPanel extends SearchPanel<SkiType, SkiTypeManager> {
        // as search() can have any combination of types of arguments, it cannot be generalised //TODO verify if its true
        SkiTypeManager manager;
        
        JTextField searchNameTextField;
        JTextField searchDescriptionTextField;
        

        SkiTypeSearchPanel(SkiTypeManager manager_, SkiTypeAppTab parent_) {
            this.manager = manager_;
            this.parent = parent_;


            this.searchNameTextField = new JTextField(16);
            this.searchDescriptionTextField = new JTextField(16);

            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);

            this.searchResultsPanel = new JPanel();


            setLayout(new GridBagLayout());

            add(new JLabel("Name:"), createGbc(0, 0));
            add(new JLabel("Description:"), createGbc(0, 1));
            add(this.searchNameTextField, createGbc(1, 0));
            add(this.searchDescriptionTextField,  createGbc(1, 1));
            add(searchButton, createGbc(0, 2, 2));
            add(searchResultsPanel, createGbc(0, 3));

            
            // Show all items
            ArrayList<SkiType> results = skiTypeManager.search(null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<SkiType> performSearch() {
            return manager.search(searchNameTextField.getText(), searchDescriptionTextField.getText());
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


    private static class SkiTypeEntityPanel extends EntityPanel<SkiType, SkiTypeManager>  {
        JTextField selectedItemName;
        JTextField selectedItemDescription;

        SkiTypeEntityPanel(SkiTypeManager manager_, SkiTypeAppTab parent_){
            this.manager = manager_;
            this.parent = parent_;


            // Elements
            selectedItemName = new JTextField("");
            selectedItemDescription = new JTextField("");
            selectedItemName.setPreferredSize( new Dimension(200, 24));
            selectedItemDescription.setPreferredSize( new Dimension(200, 24));

            JLabel nameLabel = new JLabel("Name:  ");
            JLabel descriptionLabel = new JLabel("Description:  ");


            CreateNewEntityButton<SkiType, SkiTypeManager> createNewEntityButton = new CreateNewEntityButton<>("New", this);
            createNewEntityButton.addActionListener(createNewEntityButton);

            EditEntityButton<SkiType, SkiTypeManager> editEntityButton = new EditEntityButton<>("Edit", this);
            editEntityButton.addActionListener(editEntityButton);

            DeleteEntityButton<SkiType, SkiTypeManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);
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


        @Override
        public void onEntityLoaded(SkiType selectedEntity) {
            selectedItemName.setText(selectedEntity.getName());
            selectedItemDescription.setText(selectedEntity.getDescription());
        }

        @Override
        SkiType loadItemData() {
            return new SkiType(selectedItemName.getText(), selectedItemDescription.getText());
        }
    }

    //endregion SkiType



    //region Ski

    private static class SkiAppTab extends GenericAppTab<Ski, SkiManager> {
        SkiAppTab(SkiManager manager_, SkiTypeManager skiTypeManager) {
            searchPanel = new SkiSearchPanel(manager_, this);
            add(searchPanel);

            entityPanel = new SkiEntityPanel(manager_, this, skiTypeManager);
            add(entityPanel);
        }
    }

    private static class SkiSearchPanel extends SearchPanel<Ski, SkiManager> {
        // as search() can have any combination of types of arguments, it cannot be generalised //TODO verify if its true
        SkiManager manager;

        JTextField searchNameTextField;
        JTextField searchDescriptionTextField;


        SkiSearchPanel(SkiManager manager_, SkiAppTab parent_) {
            this.manager = manager_;
            this.parent = parent_;


            JLabel nameLabel = new JLabel("Name:");
            JLabel descriptionLabel = new JLabel("Description:");


            this.searchNameTextField = new JTextField(16);
            this.searchDescriptionTextField = new JTextField(16);

            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);

            this.searchResultsPanel = new JPanel();


            //LAYOUT
            setLayout(new GridBagLayout());

            add(nameLabel, createGbc(0, 0));
            add(descriptionLabel, createGbc(0, 1));

            add(this.searchNameTextField, createGbc(1, 0));
            add(this.searchDescriptionTextField,  createGbc(1, 1));


            add(searchButton, createGbc(0, 2, 2));


            add(searchResultsPanel, createGbc(0, 3));


            // Show all items
            ArrayList<Ski> results = skiManager.search(null, null, null, null, null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<Ski> performSearch() {
            //SkiType type, String brand, String model, String bonds, Float minLength, Float maxLength  //TODO fix
            return manager.search(null, null, null, null, null, null);
        }


        public void loadSearchResults(ArrayList<Ski> searchResults) {
            searchResultsPanel.removeAll();

            int number_of_results = searchResults.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);

            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (Ski skiItem : searchResults) {
                System.out.println(skiItem.toString());
                SearchedPositionButton<Ski> skiResult = new SearchedPositionButton<>(skiItem.toString(), skiItem, parent);

                skiResult.addActionListener(skiResult);

                searchResultsPanel.add(skiResult);
            }
            driver.refresh();
            System.out.println("End of Search");
        }
    }


    private static class SkiEntityPanel extends EntityPanel<Ski, SkiManager>  {
        SkiTypeManager skiTypeManager;
        JTextField selectedItemName;
        JTextField selectedItemDescription;

        AutoCompleteComboBox comboBox;

        SkiEntityPanel(SkiManager manager_, SkiAppTab parent_, SkiTypeManager skiTypeManager_){
            this.manager = manager_;
            this.parent = parent_;
            this.skiTypeManager = skiTypeManager_;

            // Elements
            selectedItemName = new JTextField("");
            selectedItemDescription = new JTextField("");
            selectedItemName.setPreferredSize( new Dimension(200, 24));
            selectedItemDescription.setPreferredSize( new Dimension(200, 24));

            JLabel nameLabel = new JLabel("Brand:  ");
            JLabel descriptionLabel = new JLabel("Model:  ");


            CreateNewEntityButton<Ski, SkiManager> createNewEntityButton = new CreateNewEntityButton<>("New", this);
            createNewEntityButton.addActionListener(createNewEntityButton);

            EditEntityButton<Ski, SkiManager> editEntityButton = new EditEntityButton<>("Edit", this);
            editEntityButton.addActionListener(editEntityButton);

            DeleteEntityButton<Ski, SkiManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);
            deleteEntityButton.addActionListener(deleteEntityButton);


            List<SkiType> skiTypes = this.skiTypeManager.getEntities();
            String[] skiTypeNames = new String[skiTypes.size()];
            for (int i = 0; i < skiTypes.size(); i++) {
                skiTypeNames[i] = skiTypes.get(i).getName();
            }

            comboBox = new AutoCompleteComboBox(skiTypeNames);



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


            //XD
            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(comboBox, gbc);
            gbc.gridwidth = 1;
            //XD


            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;
            add(createNewEntityButton, gbc);
            gbc.gridx += 1;
            add(editEntityButton, gbc);
            gbc.gridx += 1;
            add(deleteEntityButton, gbc);


            //XD HERE XD

        }


        @Override
        public void onEntityLoaded(Ski selectedEntity) {
            selectedItemName.setText(selectedEntity.toString());
            //selectedItemDescription.setText(selectedEntity.getDescription());
        }

        @Override
        Ski loadItemData() {
            //return new Ski(selectedItemName.getText(), selectedItemDescription.getText());
            return null;
        }
    }

    //endregion Ski


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

        SkiTypeAppTab skiTypeAppTab = new SkiTypeAppTab(skiTypeManager);

        SkiAppTab skiAppTab = new SkiAppTab(skiManager, skiTypeManager);


        // Main Space
        tabSpace = new JPanel();
        tabSpace.add(skiAppTab); // TODO remember last selected tab by user


        mainFrame.setLayout(new GridBagLayout());


        // Tab Selection Buttons
        TabButton skiTypesTabButton = new TabButton("skitype", skiTypeAppTab);
        TabButton skiTabButton = new TabButton("ski", skiAppTab);
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

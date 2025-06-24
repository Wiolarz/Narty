package wit.io.swing;

// Java Program to create a SkiDriver class to run
// the toast class
import wit.io.data.Client;
import wit.io.data.Rent;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.managers.*;
import wit.io.utils.Const;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.*;
import java.time.LocalDate;
import java.util.List;


/*
TODO:
      scrollable SearchPanel
      make 3 raport windows:
        - one that takes two dates
        - two that just display generated info
 */


public class SkiDriver {

    //region Variables
    // create a frame
    static JFrame mainFrame;

    static public SkiDriver driver;

    static JPanel tabSpace;
    static JPanel windowSpace;

    static SkiTypeManager skiTypeManager;
    static SkiManager skiManager;
    static ClientManager clientManager;
    static RentManager rentManger;

    static ReportManager reportManager;
    static ErrorPopup errorPopup;


    //endregion Variables


    //region Init

    private static boolean managersSetup() throws SkiAppException {
        try {
            skiTypeManager = new SkiTypeManager(Const.SkiTypeFilePath);
            skiManager = new SkiManager(Const.SkiFilePath);
            clientManager = new ClientManager(Const.ClientFilePath);
            rentManger = new RentManager(Const.RentFilePath);
            reportManager = new ReportManager(rentManger, skiManager);
        } catch (ReadingException e)
        {
            System.out.println("Failed to create Manager");
            return false;
        }

        return true;
    }


    private static void populateData() throws SkiAppException {
        skiManager.resetEntityData();
        skiTypeManager.resetEntityData();
        clientManager.resetEntityData();
        rentManger.resetEntityData();

        SkiType skiType1 = new SkiType("hello", "world");
        SkiType skiType2 = new SkiType("kill", "mee");

        for (int i = 0; i < 20; i++) {
            skiTypeManager.addEntity(new SkiType("elo" + i, "xd"));
        }

        skiTypeManager.addEntity(skiType1);
        skiTypeManager.addEntity(skiType2);


        Ski ski1 = new Ski(skiType1, "żabka", "model_a", "ekstra", 10f);
        Ski ski2 = new Ski(skiType2, "żabka", "model_b", "zwykle", 20f);
        Ski ski3 = new Ski(skiType1, "biedronka", "model_c", "zwykle", 5f);
        Ski ski4 = new Ski(skiType2, "zabka", "model_d", "zwykle", 3f);
        Ski ski5 = new Ski(skiType1, "lidl", "model_e", "ekstra", 50f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);
        skiManager.addEntity(ski3);
        skiManager.addEntity(ski4);
        skiManager.addEntity(ski5);


        Client client1 = new Client("a0", "Janusz", "Tracz", "bogacz");
        Client client2 = new Client("b1", "Adrian", "Zandberg", "potężny Duńczyk");
        Client client3 = new Client("c2", "Janusz", "Korwin-Mikke", "komunistyczny mnożnik lodu w szklance");

        clientManager.addEntity(client1);
        clientManager.addEntity(client2);
        clientManager.addEntity(client3);


        Rent rent1 = new Rent(null, LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), ski1.getModel(), client1.getDocId(), "pierwsze wypozyczenia", RentStatus.ACTIVE);
        //Rent rent2 = new Rent(null, LocalDate.now(), LocalDate.now(), LocalDate.now(), ski2.getModel(), client2.getDocId(), "drugie wypozyczenia", RentStatus.ACTIVE);
        //Rent rent3 = new Rent(null, LocalDate.now(), LocalDate.now(), LocalDate.now(), ski3.getModel(), client3.getDocId(), "trzemcie wypozyczenia", RentStatus.ACTIVE);
        rentManger.addEntity(rent1);
        //rentManger.addEntity(rent2);
        //rentManger.addEntity(rent3);
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


        //Error popup
        errorPopup = new ErrorPopup(mainFrame);

        // resize Action
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent){
                errorPopup.hide();
                errorPopup.changedFrame(mainFrame);
                errorPopup.show();
            }
            @Override
            public void componentMoved(ComponentEvent componentEvent){
                errorPopup.hide();
                errorPopup.changedFrame(mainFrame);
                errorPopup.show();
            }
        });
    }

    //endregion Init


    //region Helpers

    public void refresh() {
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }


    /**
     * <a href="https://stackoverflow.com/a/70393691/17491940">Indian Homie</a>
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
        public AutoCompleteComboBox(final Object[] selectableEntities) {
            super(selectableEntities);
            setEditor(new BasicComboBoxEditor());
            setEditable(true);
            addActionListener(this);
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
                        String text = "";
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

        @Override
        public void actionPerformed(ActionEvent e) {
            driver.refresh(); //XDDDDDDDDDDDDDDDDDDDDDDD
            //System.out.println(deleteEntityButton.is);
        }
    }

    static AutoCompleteComboBox createAutoCompleteSearchComboBox(Object[] addAnyToMe) {
        Object[] newMe = new Object[addAnyToMe.length + 1];
        newMe[0] = (Object) "Any";
        for(int i = 0; i < addAnyToMe.length; i++){
            newMe[i + 1] = addAnyToMe[i];
        }
        return new AutoCompleteComboBox(newMe);
    }


    //endregion Helpers

    //region Report Tab

    private static class AvailableSkisButton extends Button implements ActionListener {
        ReportAppTab panel;
        AvailableSkisButton(String buttonText, ReportAppTab panel_) {
            super(buttonText);
            this.panel = panel_;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("button is pressed  " + this.getLabel());
            LinkedHashSet<Ski> reportResult = panel.manager.availableSkis(null); //TODO ask if there needs to be a date selection for this button
            panel.displayReport(reportResult);
        }
    }
    private static class OverdueSkisButton extends Button implements ActionListener {
        ReportAppTab panel;
        OverdueSkisButton(String buttonText, ReportAppTab panel_) {
            super(buttonText);
            this.panel = panel_;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("button is pressed  " + this.getLabel());
            LinkedHashSet<Ski> reportResult = panel.manager.overdueSkis(); //TODO ask if there needs to be a date selection for this button
            panel.displayReport(reportResult);
        }
    }
    private static class RentedSkisButton extends Button implements ActionListener {
        ReportAppTab panel;
        RentedSkisButton(String buttonText, ReportAppTab panel_) {
            super(buttonText);
            this.panel = panel_;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("button is pressed  " + this.getLabel());
            LinkedHashSet<Ski> reportResult = panel.manager.rentedSkis(null); //TODO ask if there needs to be a date selection for this button
            panel.displayReport(reportResult);
        }
    }


    private static class ReportAppTab extends JPanel {
        ReportManager manager;

        JPanel searchResultsPanel;

        ReportAppTab(ReportManager manager_) {
            this.manager = manager_;

            AvailableSkisButton availableSkisButton = new AvailableSkisButton("Available Skis", this);
            OverdueSkisButton overdueSkisButton = new OverdueSkisButton("Overdue Skis", this);
            RentedSkisButton rentedSkisButton = new RentedSkisButton("Rented Skis", this);


            this.searchResultsPanel = new JPanel();

            //LAYOUT
            setLayout(new GridBagLayout());


            int row = 0;
            int column = 0;


            add(availableSkisButton, createGbc(column, row));
            column += 1;
            add(overdueSkisButton, createGbc(column, row));
            column += 1;
            add(rentedSkisButton, createGbc(column, row));

            column = 0; row += 1;
            add(searchResultsPanel, createGbc(column, row));
        }

        public void displayReport(LinkedHashSet<Ski> skisToDisplay) {
            searchResultsPanel.removeAll();
            System.out.println("elo");


            int number_of_results = skisToDisplay.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);
            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (Ski skiItem : skisToDisplay) {
                System.out.println(skiItem.toString());
                JLabel skiResult = new JLabel(skiItem.getBrand() + " " + skiItem.getModel());

                searchResultsPanel.add(skiResult);
            }
            driver.refresh();
            System.out.println("End of Report");
        }

    }


    //endregion Report Tab


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
            addActionListener(this);
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
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.createItem();
        }
    }
    private static class EditEntityButton<E extends Writeable, M extends Manager<E>> extends Button implements ActionListener {
        EntityPanel<E, M> panel;
        EditEntityButton(String buttonText, EntityPanel<E, M> panel_) {
            super(buttonText);
            this.panel = panel_;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.editItem();
        }
    }
    private static class DeleteEntityButton<E extends Writeable, M extends Manager<E>> extends Button implements ActionListener {
        EntityPanel<E, M> panel;
        DeleteEntityButton(String buttonText, EntityPanel<E, M> panel_) {
            super(buttonText);
            this.panel = panel_;
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) {
            //System.out.println("button is pressed  " + this.getLabel());
            panel.deleteItem();
        }
    }

    //endregion Generic Tab elements


    //region Generic Tab Core

    private static abstract class GenericAppTab<E extends Writeable, M extends Manager<E>> extends JPanel implements searchableTab<E> {
        SearchPanel<E, M> searchPanel;

        // Entity panel has to made first
        EntityPanel<E, M> entityPanel;


        public final void repeatSearch() {
            searchPanel.repeatSearch();
        }

        public final void selectedItem(E selectedEntity) {
            entityPanel.entityLoaded(selectedEntity);
        }

        protected final boolean isThereNoSelectedItem() {
            return entityPanel.selectedEntity == null;
        }

        public final void reloadComboBox() {
            System.out.println("reloading search result");
            searchPanel.reloadComboBox();
            entityPanel.reloadComboBox();
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


        abstract void reloadComboBox();
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
            selectedEntity = null;
            E newEntity = loadItemData();
            try {
                if (newEntity == null) {
                    throw new FailedLoadingUserInputException();
                }
                manager.addEntity(newEntity);
                repeatSearch(); // doesn't throw errors
            } catch (FailedLoadingUserInputException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage()  + " Error");
                System.out.println("Failed to load new entity. " + " Error: " + e);
            }
            catch (SkiAppException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage() + " Error");
                System.out.println("Failed to create new entity. " + newEntity.toString() + " Error: " + e);
            }

        }
        final void editItem() {
            try {
                E newEntity = loadItemData();
                if (selectedEntity == null) {
                    throw new FailedSelectingEntityException();
                }
                if (newEntity == null) {
                    throw new FailedLoadingUserInputException();
                }
                manager.editEntity(selectedEntity, newEntity);
                repeatSearch(); // doesn't throw errors
            }catch (FailedSelectingEntityException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage() + " Error");
                System.out.println("Failed to select entity. " + " Error: " + e);
            } catch (FailedLoadingUserInputException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage() + " Error");
                System.out.println("Failed to load new entity. " + " Error: " + e);
            } catch (SkiAppException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage() + " Error");
                System.out.println("Failed to edit: " + selectedEntity.toString() + "  Error: " + e);
            }
        }
        final void deleteItem() {
            try {
                manager.removeEntity(selectedEntity);
                selectedEntity = null;
                repeatSearch(); // doesn't throw errors
            } catch (SkiAppException e) {
                errorPopup.show(e.getMessage() == null ?  ""+e : e.getMessage() + " Error");
                System.out.println("Failed to delete: " + selectedEntity.toString() + "  Error: " + e);
            }
        }
        abstract public void reloadComboBox();
    }

    //endregion Generic Tab Core


    //region SkiType

    private static class SkiTypeAppTab extends GenericAppTab<SkiType, SkiTypeManager> {
        SkiTypeAppTab(SkiTypeManager manager_, SkiAppTab tabToRefresh) {
            entityPanel = new SkiTypeEntityPanel(manager_, this);
            searchPanel = new SkiTypeSearchPanel(manager_, this, tabToRefresh);

            add(searchPanel);
            add(entityPanel);
        }
    }


    private static class SkiTypeSearchPanel extends SearchPanel<SkiType, SkiTypeManager> {
        // as search() can have any combination of types of arguments, it cannot be generalised //TODO verify if its true
        SkiTypeManager manager;

        JTextField searchNameTextField;
        JTextField searchDescriptionTextField;

        SkiAppTab skiAppTab;


        SkiTypeSearchPanel(SkiTypeManager manager_, SkiTypeAppTab parent_, SkiAppTab tabToRefresh) {
            this.manager = manager_;
            this.parent = parent_;
            this.skiAppTab = tabToRefresh;


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
            add(this.searchResultsPanel, createGbc(0, 3, 2));

            // Show all items
            ArrayList<SkiType> results = this.manager.search(null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<SkiType> performSearch() {
            return manager.search(searchNameTextField.getText(), searchDescriptionTextField.getText());
        }


        public void loadSearchResults(ArrayList<SkiType> searchResults) {
            //HACK fix for new types not showing up in search list of sky types of other managers
            skiAppTab.reloadComboBox();


            searchResultsPanel.removeAll();
            if (!searchResults.isEmpty()) {
                if (parent.isThereNoSelectedItem()) {
                    parent.selectedItem(searchResults.get(0));
                }
            }

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

                searchResultsPanel.add(skiTypeResult);
            }
            driver.refresh();
            System.out.println("End of Search");

        }

        @Override
        void reloadComboBox() {
            // empty
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

            EditEntityButton<SkiType, SkiTypeManager> editEntityButton = new EditEntityButton<>("Edit", this);

            DeleteEntityButton<SkiType, SkiTypeManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);


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
        public void reloadComboBox() {

        }

        @Override
        SkiType loadItemData() {
            try {
                return new SkiType(selectedItemName.getText(), selectedItemDescription.getText());
            } catch (Exception e) {
                System.out.println("failed loading data: " + e);
            }
            return null;
        }
    }

    //endregion SkiType


    //region Ski

    private static class SkiAppTab extends GenericAppTab<Ski, SkiManager> {
        SkiAppTab(SkiManager manager_, SkiTypeManager skiTypeManager, SignalSender signalSender) {
            entityPanel = new SkiEntityPanel(manager_, this, skiTypeManager);
            searchPanel = new SkiSearchPanel(manager_, this, signalSender);

            add(searchPanel);
            add(entityPanel);
        }
    }

    private static class SkiSearchPanel extends SearchPanel<Ski, SkiManager> {
        // as search() can have any combination of types of arguments, it cannot be generalised //TODO verify if its true
        SkiManager manager;

        JTextField searchItemBrand;
        JTextField searchItemModel;
        JTextField searchItemBonds;
        JTextField searchMinItemLength;
        JTextField searchMaxItemLength;

        AutoCompleteComboBox searchItemSkiTypeComboBox;
        List<SkiType> skiTypes;

        GridBagConstraints comboBoxGBC;

        SignalSender signalSender;

        SkiSearchPanel(SkiManager manager_, SkiAppTab parent_, SignalSender signalSender_) {
            this.manager = manager_;
            this.parent = parent_;
            this.signalSender = signalSender_;

            JLabel itemBrandLabel = new JLabel("Brand:");
            JLabel itemModelLabel = new JLabel("Model:");
            JLabel itemBondsLabel = new JLabel("Bonds:");
            JLabel itemMinLengthLabel = new JLabel("Mininmum length:");
            JLabel itemMaxLengthLabel = new JLabel("Maximum length:");
            JLabel skiTypeLabel = new JLabel("Ski type:  ");


            this.searchItemBrand = new JTextField();
            this.searchItemModel = new JTextField();
            this.searchItemBonds = new JTextField();
            this.searchMinItemLength = new JTextField();
            this.searchMaxItemLength = new JTextField();

            this.skiTypes = skiTypeManager.getEntitiesList();
            String[] skiTypeNames = new String[this.skiTypes.size()];
            for (int i = 0; i < this.skiTypes.size(); i++) {
                skiTypeNames[i] = this.skiTypes.get(i).getName();
            }

            searchItemSkiTypeComboBox = createAutoCompleteSearchComboBox(skiTypeNames);


            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);

            this.searchResultsPanel = new JPanel();


            //LAYOUT
            setLayout(new GridBagLayout());


            Dimension defaultFieldDimension = new Dimension(200, 24);
            searchItemBrand.setPreferredSize(defaultFieldDimension);
            searchItemModel.setPreferredSize(defaultFieldDimension);
            searchItemBonds.setPreferredSize(defaultFieldDimension);
            searchMinItemLength.setPreferredSize(defaultFieldDimension);
            searchMaxItemLength.setPreferredSize(defaultFieldDimension);


            int row = 0;
            int column = 0;

            add(itemBrandLabel, createGbc(column, row));
            row += 1;
            add(itemModelLabel, createGbc(column, row));
            row += 1;
            add(itemBondsLabel, createGbc(column, row));
            row += 1;
            add(itemMinLengthLabel, createGbc(column, row));
            row += 1;
            add(itemMaxLengthLabel, createGbc(column, row));
            row += 1;
            add(skiTypeLabel, createGbc(column, row));

            column +=1; row = 0;

            add(this.searchItemBrand, createGbc(column, row));
            row += 1;
            add(this.searchItemModel,  createGbc(column, row));
            row += 1;
            add(this.searchItemBonds,  createGbc(column, row));
            row += 1;
            add(this.searchMinItemLength,  createGbc(column, row));
            row += 1;
            add(this.searchMaxItemLength,  createGbc(column, row));
            row += 1;
            comboBoxGBC = createGbc(column, row);
            add(this.searchItemSkiTypeComboBox, comboBoxGBC);

            column = 0; row +=1;

            add(searchButton, createGbc(column, row, 2));

            row += 1;
            add(searchResultsPanel, createGbc(column, row));


            // Show all items
            ArrayList<Ski> results = this.manager.search(null, null, null, null, null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<Ski> performSearch() {
            Float minLength = null;
            try{
                minLength = Float.parseFloat(searchMinItemLength.getText());
            } catch (NullPointerException | NumberFormatException ignored){}
            Float maxLength = null;
            try{
                maxLength = Float.parseFloat(searchMaxItemLength.getText());
            } catch (NullPointerException | NumberFormatException ignored){}

            String searchedSkiTypeName = ((String)searchItemSkiTypeComboBox.getSelectedItem()).equals("Any")  ? null : (String)searchItemSkiTypeComboBox.getSelectedItem();
            SkiType searchedSkiType = null;
            if (searchedSkiTypeName != null) {
                for (SkiType skiType : skiTypes) {
                    if (skiType.getName().equals(searchedSkiTypeName)) {
                        searchedSkiType = skiType;
                        break;
                    }
                }
            }
            //SkiType type, String brand, String model, String bonds, Float minLength, Float maxLength
            return manager.search(searchedSkiType, searchItemBrand.getText(), searchItemModel.getText(), searchItemBonds.getText(), minLength, maxLength);
        }


        public void loadSearchResults(ArrayList<Ski> searchResults) {
            searchResultsPanel.removeAll();
            signalSender.sendReload();

            if (!searchResults.isEmpty()) {
                if (parent.isThereNoSelectedItem()) {
                    parent.selectedItem(searchResults.get(0));
                }
            }

            int number_of_results = searchResults.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);

            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (Ski skiItem : searchResults) {
                System.out.println(skiItem.toString());
                SearchedPositionButton<Ski> skiResult = new SearchedPositionButton<>(skiItem.getBrand() + " " + skiItem.getModel(), skiItem, parent);


                searchResultsPanel.add(skiResult);
            }
            driver.refresh();
            System.out.println("End of Search");
        }

        @Override
        void reloadComboBox() {
            remove(searchItemSkiTypeComboBox);

            skiTypes = skiTypeManager.getEntitiesList();
            String[] skiTypeNames = new String[skiTypes.size()];
            for (int i = 0; i < skiTypes.size(); i++) {
                skiTypeNames[i] = skiTypes.get(i).getName();
            }

            searchItemSkiTypeComboBox = createAutoCompleteSearchComboBox(skiTypeNames);

            add(searchItemSkiTypeComboBox, comboBoxGBC);

            //IMPORTANT
            revalidate();
            repaint();
        }
    }


    private static class SkiEntityPanel extends EntityPanel<Ski, SkiManager>  {
        SkiTypeManager skiTypeManager;
        JTextField selectedItemBrand;
        JTextField selectedItemModel;
        JTextField selectedItemBonds;
        JTextField selectedItemLength;

        AutoCompleteComboBox selectedItemSkiTypeComboBox;
        GridBagConstraints comboBoxGBC;


        List<SkiType> skiTypes;

        SkiEntityPanel(SkiManager manager_, SkiAppTab parent_, SkiTypeManager skiTypeManager_){
            this.manager = manager_;
            this.parent = parent_;
            this.skiTypeManager = skiTypeManager_;



            // Elements
            selectedItemBrand = new JTextField("");
            selectedItemModel = new JTextField("");
            selectedItemBonds = new JTextField("");
            selectedItemLength = new JTextField("");


            JLabel brandLabel = new JLabel("Brand:  ");
            JLabel modelLabel = new JLabel("Model:  ");
            JLabel bondsLabel = new JLabel("Bonds:  ");
            JLabel lengthLabel = new JLabel("Length:  ");
            JLabel skiTypeLabel = new JLabel("Ski type:  ");


            CreateNewEntityButton<Ski, SkiManager> createNewEntityButton = new CreateNewEntityButton<>("New", this);

            EditEntityButton<Ski, SkiManager> editEntityButton = new EditEntityButton<>("Edit", this);

            DeleteEntityButton<Ski, SkiManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);


            this.skiTypes = this.skiTypeManager.getEntitiesList();
            String[] skiTypeNames = new String[this.skiTypes.size()];
            for (int i = 0; i < skiTypes.size(); i++) {
                skiTypeNames[i] = skiTypes.get(i).getName();
            }


            selectedItemSkiTypeComboBox = new AutoCompleteComboBox(skiTypeNames);



            //Layout
            //
            Dimension defaultButtonDimension = new Dimension(200, 24);
            selectedItemBrand.setPreferredSize(defaultButtonDimension);
            selectedItemModel.setPreferredSize(defaultButtonDimension);
            selectedItemBonds.setPreferredSize(defaultButtonDimension);
            selectedItemLength.setPreferredSize(defaultButtonDimension);



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
            add(brandLabel, gbc);

            gbc.gridx += 1; // One to the right

            gbc.gridwidth = 2; // wider element
            add(selectedItemBrand, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(modelLabel, gbc);

            gbc.gridx += 1; // One to the right

            gbc.gridwidth = 2; // wider element
            add(selectedItemModel, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(bondsLabel, gbc);

            gbc.gridx += 1; // One to the right

            gbc.gridwidth = 2; // wider element
            add(selectedItemBonds, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(lengthLabel, gbc);

            gbc.gridx += 1; // One to the right
            gbc.gridwidth = 2; // wider element
            add(selectedItemLength, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(skiTypeLabel, gbc);
            gbc.gridwidth = 1;


            //XD
            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element

            comboBoxGBC = new GridBagConstraints();
            comboBoxGBC.fill = GridBagConstraints.HORIZONTAL;
            comboBoxGBC.insets = new Insets(gap, gap, gap, gap);
            comboBoxGBC.weightx = 0.5;
            comboBoxGBC.gridx = gbc.gridx;
            comboBoxGBC.gridy = gbc.gridy;
            comboBoxGBC.gridwidth = gbc.gridwidth;

            add(selectedItemSkiTypeComboBox, comboBoxGBC);

            gbc.gridwidth = 1;
            //XD


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            add(createNewEntityButton, gbc);
            gbc.gridx += 1;
            add(editEntityButton, gbc);
            gbc.gridx += 1;
            add(deleteEntityButton, gbc);
        }


        @Override
        public void onEntityLoaded(Ski selectedEntity) {
            selectedItemBrand.setText(selectedEntity.getBrand());
            selectedItemModel.setText(selectedEntity.getModel());
            selectedItemBonds.setText(selectedEntity.getBonds());
            selectedItemLength.setText(String.valueOf(selectedEntity.getLength()));

            String typeName = selectedEntity.getType().getName();

            // TODO: optimise this  | save this value and use it here String[] skiTypeNames = new String[skiTypes.size()];
            int itemIndex = 0;
            for (int i = 0; i < selectedItemSkiTypeComboBox.getSize().getWidth(); i++){
                if(selectedItemSkiTypeComboBox.getItemAt(i).equals(typeName)){
                    itemIndex = i;
                    break;
                }
            }
            selectedItemSkiTypeComboBox.setSelectedIndex(itemIndex);
        }

        @Override
        Ski loadItemData() {
            float length = 0;
            try{
                length = Float.parseFloat(selectedItemLength.getText());
            } catch (NullPointerException e){
                System.out.println("loadItemData " + e.getMessage() == null ?  ""+e : e.getMessage());
                return null;
            }
            catch (NumberFormatException e) {
                System.out.println("loadItemData " + e.getMessage() == null ?  ""+e : e.getMessage());
                return null;
            }

            SkiType skiType = skiTypes.get(selectedItemSkiTypeComboBox.getSelectedIndex());
            try {
            return new Ski(
                    skiType,
                    selectedItemBrand.getText(),
                    selectedItemModel.getText(),
                    selectedItemBonds.getText(),
                    length
            );
            } catch (Exception e) {
                System.out.println("failed loading data: " + e);
            }
                return null;
            }

        @Override
        public void reloadComboBox() {
            remove(selectedItemSkiTypeComboBox);

            skiTypes = skiTypeManager.getEntitiesList();
            String[] skiTypeNames = new String[skiTypes.size()];
            for (int i = 0; i < skiTypes.size(); i++) {
                skiTypeNames[i] = skiTypes.get(i).getName();
            }

            selectedItemSkiTypeComboBox = new AutoCompleteComboBox(skiTypeNames);

            add(selectedItemSkiTypeComboBox, comboBoxGBC);

            //IMPORTANT
            revalidate();
            repaint();
        }
    }

    //endregion Ski


    //region Client

    private static class ClientAppTab extends GenericAppTab<Client, ClientManager> {
        ClientAppTab(ClientManager manager_, SignalSender signalSender) {
            entityPanel = new ClientEntityPanel(manager_, this);
            searchPanel = new ClientSearchPanel(manager_, this, signalSender);

            add(searchPanel);
            add(entityPanel);
        }
    }


    private static class ClientSearchPanel extends SearchPanel<Client, ClientManager> {
        // as search() can have any combination of types of arguments, it cannot be generalised //TODO verify if its true
        ClientManager manager;

        JTextField searchFirstNameTextField;
        JTextField searchLastNameTextField;
        JTextField searchDocIDTextField;
        JTextField searchDescriptionTextField;

        SignalSender signalSender;


        ClientSearchPanel(ClientManager manager_, ClientAppTab parent_, SignalSender signalSender_) {
            this.manager = manager_;
            this.parent = parent_;
            this.signalSender = signalSender_;


            JLabel firstNameLabel = new JLabel("First name:");
            JLabel lastNameLabel = new JLabel("Last name:");
            JLabel docIdLabel = new JLabel("Document ID:");
            JLabel descriptionLabel = new JLabel("Description:");

            this.searchFirstNameTextField = new JTextField();
            this.searchLastNameTextField = new JTextField();
            this.searchDocIDTextField = new JTextField();
            this.searchDescriptionTextField = new JTextField();

            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);

            this.searchResultsPanel = new JPanel();


            setLayout(new GridBagLayout());

            Dimension defaultFieldDimension = new Dimension(200, 24);
            searchFirstNameTextField.setPreferredSize(defaultFieldDimension);
            searchLastNameTextField.setPreferredSize(defaultFieldDimension);
            searchDocIDTextField.setPreferredSize(defaultFieldDimension);
            searchDescriptionTextField.setPreferredSize(defaultFieldDimension);

            int row = 0;
            int column = 0;

            add(firstNameLabel, createGbc(column, row));
            row += 1;
            add(lastNameLabel, createGbc(column, row));
            row += 1;
            add(docIdLabel, createGbc(column, row));
            row += 1;
            add(descriptionLabel, createGbc(column, row));

            column +=1; row = 0;

            add(this.searchFirstNameTextField, createGbc(column, row));
            row += 1;
            add(this.searchLastNameTextField,  createGbc(column, row));
            row += 1;
            add(this.searchDocIDTextField,  createGbc(column, row));
            row += 1;
            add(this.searchDescriptionTextField,  createGbc(column, row));

            column = 0; row +=1;

            add(searchButton, createGbc(column, row, 2));

            row += 1;
            add(searchResultsPanel, createGbc(column, row));


            // Show all items
            ArrayList<Client> results = this.manager.search(null, null, null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<Client> performSearch() {

            String docID = searchDocIDTextField.getText();
            if (docID.isEmpty()) {
                docID = null;
            }
            String firstName = searchFirstNameTextField.getText();
            if (firstName.isEmpty()) {
                firstName = null;
            }
            String lastName = searchLastNameTextField.getText();
            if (lastName.isEmpty()) {
                lastName = null;
            }
            String description = searchDescriptionTextField.getText();
            if (description.isEmpty()) {
                description = null;
            }

            return manager.search(docID, firstName, lastName, description);
        }


        public void loadSearchResults(ArrayList<Client> searchResults) {
            signalSender.sendReload();
            searchResultsPanel.removeAll();
            if (!searchResults.isEmpty()) {
                if (parent.isThereNoSelectedItem()) {
                    parent.selectedItem(searchResults.get(0));
                }
            }

            int number_of_results = searchResults.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);

            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (Client clientItem : searchResults) {
                System.out.println(clientItem.toString());
                SearchedPositionButton<Client> skiTypeResult =
                        new SearchedPositionButton<>(
                                clientItem.getFirstName() + " " + clientItem.getLastName(),
                                clientItem, parent
                        );

                searchResultsPanel.add(skiTypeResult);
            }
            driver.refresh();
            System.out.println("End of Search");
        }

        @Override
        void reloadComboBox() {
            // empty
        }
    }


    private static class ClientEntityPanel extends EntityPanel<Client, ClientManager>  {

        JTextField selectedItemFirstNameTextField;
        JTextField selectedItemLastNameTextField;
        JTextField selectedItemDocIDTextField;
        JTextField selectedItemDescriptionTextField;

        ClientEntityPanel(ClientManager manager_, ClientAppTab parent_){
            this.manager = manager_;
            this.parent = parent_;


            // Elements
            selectedItemFirstNameTextField = new JTextField();
            selectedItemLastNameTextField = new JTextField();
            selectedItemDocIDTextField = new JTextField();
            selectedItemDescriptionTextField = new JTextField();

            Dimension defaultButtonDimension = new Dimension(200, 24);
            selectedItemFirstNameTextField.setPreferredSize(defaultButtonDimension);
            selectedItemLastNameTextField.setPreferredSize(defaultButtonDimension);
            selectedItemDocIDTextField.setPreferredSize(defaultButtonDimension);
            selectedItemDescriptionTextField.setPreferredSize(defaultButtonDimension);

            JLabel firstNameLabel = new JLabel("Firstname:  ");
            JLabel lastNameLabel = new JLabel("Lastname:  ");
            JLabel docIdLabel = new JLabel("Document id:  ");
            JLabel descriptionLabel = new JLabel("Description:  ");


            CreateNewEntityButton<Client, ClientManager> createNewEntityButton = new CreateNewEntityButton<>("New", this);

            EditEntityButton<Client, ClientManager> editEntityButton = new EditEntityButton<>("Edit", this);

            DeleteEntityButton<Client, ClientManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);


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
            add(firstNameLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemFirstNameTextField, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(lastNameLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemLastNameTextField, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(docIdLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemDocIDTextField, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Postion
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(descriptionLabel, gbc);

            // One to the right
            gbc.gridx += 1;

            gbc.gridwidth = 2; // wider element
            add(selectedItemDescriptionTextField, gbc);
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
        public void onEntityLoaded(Client selectedEntity) {
            selectedItemFirstNameTextField.setText(selectedEntity.getFirstName());
            selectedItemLastNameTextField.setText(selectedEntity.getLastName());
            selectedItemDescriptionTextField.setText(selectedEntity.getDescription());
            selectedItemDocIDTextField.setText(String.valueOf(selectedEntity.getDocId()));
        }

        @Override
        public void reloadComboBox() {
            // empty
        }

        @Override
        Client loadItemData() {
            try {
                return new Client(
                    selectedItemDocIDTextField.getText(),
                    selectedItemFirstNameTextField.getText(),
                    selectedItemLastNameTextField.getText(),
                    selectedItemDescriptionTextField.getText());
            } catch (Exception e) {
                System.out.println("failed loading data: " + e);
            }
                return null;
            }
    }

    //endregion Client


    //region Rent

    private static class RentAppTab extends GenericAppTab<Rent, RentManager> {
        RentAppTab(RentManager manager_, ClientManager clientManager_, SkiManager skiManager_) {
            entityPanel = new RentEntityPanel(manager_, this, clientManager_, skiManager_);
            searchPanel = new RentSearchPanel(manager_, this, clientManager_, skiManager_);

            add(searchPanel);
            add(entityPanel);
        }
    }


    private static class RentSearchPanel extends SearchPanel<Rent, RentManager> {
        RentManager manager;
        ClientManager clientManager;
        SkiManager skiManager;

        JTextField searchStartDate;
        JTextField searchEndDate;
        JTextField searchUpdatedEndDate;
        JTextField searchComment;

        AutoCompleteComboBox searchItemRentStatusComboBox;
        AutoCompleteComboBox searchItemClientComboBox;
        AutoCompleteComboBox searchItemSkiComboBox;

        GridBagConstraints clientItemComboBoxGBC;
        GridBagConstraints skiItemComboBoxGBC;

        List<Client> clients;
        List<Ski> skis;

        RentSearchPanel(RentManager manager_, RentAppTab parent_, ClientManager clientManager_, SkiManager skiManager_) {
            this.manager = manager_;
            this.parent = parent_;
            this.clientManager = clientManager_;
            this.skiManager = skiManager_;

            JLabel startDateLabel = new JLabel("Start date:  ");
            JLabel endDateLabel = new JLabel("End date:  ");
            JLabel updatedEndDateLabel = new JLabel("Updated end date:  ");
            JLabel commentLabel = new JLabel("Updated end date:  ");
            JLabel clientComboBoxLabel = new JLabel("Client:  ");
            JLabel skiComboBoxLabel = new JLabel("Ski:  ");
            JLabel rentStatusComboBoxLabel = new JLabel("Rent status:  ");


            this.searchStartDate = new JTextField();
            this.searchEndDate = new JTextField();
            this.searchUpdatedEndDate = new JTextField();
            this.searchComment = new JTextField();


            // ComboBoxes:

            this.clients = this.clientManager.getEntitiesList();
            String[] clientsNames = new String[this.clients.size()];
            for (int i = 0; i < this.clients.size(); i++) {
                clientsNames[i] = this.clients.get(i).getFirstName();
            }

            this.skis = this.skiManager.getEntitiesList();
            String[] skiNames = new String[this.skis.size()];
            for (int i = 0; i < this.skis.size(); i++) {
                skiNames[i] = this.skis.get(i).getModel();
            }


            searchItemClientComboBox = createAutoCompleteSearchComboBox(clientsNames);
            searchItemSkiComboBox = createAutoCompleteSearchComboBox(skiNames);
            searchItemRentStatusComboBox = createAutoCompleteSearchComboBox(RentStatus.values());




            // button
            Button searchButton = new Button("search");
            // add action listener
            searchButton.addActionListener(this);

            this.searchResultsPanel = new JPanel();


            //LAYOUT
            setLayout(new GridBagLayout());


            Dimension defaultFieldDimension = new Dimension(200, 24);
            searchStartDate.setPreferredSize(defaultFieldDimension);
            searchEndDate.setPreferredSize(defaultFieldDimension);
            searchUpdatedEndDate.setPreferredSize(defaultFieldDimension);
            searchComment.setPreferredSize(defaultFieldDimension);


            int row = 0;
            int column = 0;

            add(startDateLabel, createGbc(column, row));
            row += 1;
            add(endDateLabel, createGbc(column, row));
            row += 1;
            add(commentLabel, createGbc(column, row));
            row += 1;
            add(updatedEndDateLabel, createGbc(column, row));
            row += 1;
            add(skiComboBoxLabel, createGbc(column, row));
            row += 1;
            add(clientComboBoxLabel, createGbc(column, row));
            row += 1;
            add(rentStatusComboBoxLabel, createGbc(column, row));

            column +=1; row = 0;

            add(this.searchStartDate, createGbc(column, row));
            row += 1;
            add(this.searchEndDate,  createGbc(column, row));
            row += 1;
            add(this.searchUpdatedEndDate,  createGbc(column, row));
            row += 1;
            add(this.searchComment,  createGbc(column, row));


            row += 1;
            skiItemComboBoxGBC = createGbc(column, row);

            add(this.searchItemSkiComboBox, skiItemComboBoxGBC);

            row += 1;
            clientItemComboBoxGBC = createGbc(column, row);

            add(this.searchItemClientComboBox,  clientItemComboBoxGBC);



            row += 1;
            add(this.searchItemRentStatusComboBox,  createGbc(column, row));

            column = 0; row +=1;

            add(searchButton, createGbc(column, row, 2));

            row += 1;
            add(searchResultsPanel, createGbc(column, row));


            // Show all items
            ArrayList<Rent> results = this.manager.search(null, null, null, null, null, null, null);
            loadSearchResults(results);
        }

        @Override
        protected ArrayList<Rent> performSearch() {
            LocalDate startDate = null;
            LocalDate endDate = null;
            LocalDate updatedEndDate = null;
            String comment = searchComment.getText();
            try{
                startDate = Util.stringToDate(searchStartDate.getText());
            }
            catch (ParseException ignore){}
            try{
                endDate = Util.stringToDate(searchEndDate.getText());
            }
            catch (ParseException ignore){}
            try{
                updatedEndDate = Util.stringToDate(searchUpdatedEndDate.getText());
            }
            catch (ParseException ignore){}


            RentStatus status;
            if (searchItemRentStatusComboBox.getSelectedItem().toString().equals("Any")) {
                status = null;
            } else {
                status = RentStatus.valueOf(searchItemRentStatusComboBox.getSelectedItem().toString());
            }


            return manager.search(
                    ((String)searchItemSkiComboBox.getSelectedItem()).equals("Any")  ?
                            null : (String)searchItemSkiComboBox.getSelectedItem(),
                    ((String)searchItemClientComboBox.getSelectedItem()) == "Any" ?
                            null : (String)searchItemClientComboBox.getSelectedItem(),
                    startDate,
                    endDate,
                    updatedEndDate,
                    comment,
                    status
            );
        }


        public void loadSearchResults(ArrayList<Rent> searchResults) {
            searchResultsPanel.removeAll();
            if (!searchResults.isEmpty()) {
                if (parent.isThereNoSelectedItem()) {
                    parent.selectedItem(searchResults.get(0));
                }
            }

            int number_of_results = searchResults.size();
            if (number_of_results == 0) {
                number_of_results = 1;  // Grid layout cannot be set to 0
                JLabel noResultsLabel = new JLabel("No results");
                searchResultsPanel.add(noResultsLabel);

            }

            searchResultsPanel.setLayout(new GridLayout(number_of_results, 1));
            for (Rent rentItem : searchResults) {
                System.out.println(rentItem.toString());
                SearchedPositionButton<Rent> skiResult = new SearchedPositionButton<>(
                        rentItem.getClientID() + " | " +
                                rentItem.getSkiModel() + " | " +
                                rentItem.getStartDate() + " - " +
                                rentItem.getEndDate() + " | " +
                                rentItem.getStatus(),
                        rentItem, parent
                );

                searchResultsPanel.add(skiResult);
            }
            driver.refresh();
            System.out.println("End of Search");
        }

        @Override
        void reloadComboBox() {
            // teraz todo xd
            remove(searchItemSkiComboBox);
            remove(searchItemClientComboBox);

            clients = clientManager.getEntitiesList();
            String[] clientsNames = new String[clients.size()];
            for (int i = 0; i < clients.size(); i++) {
                clientsNames[i] = clients.get(i).getFirstName();
            }

            skis = skiManager.getEntitiesList();
            String[] skiNames = new String[skis.size()];
            for (int i = 0; i < skis.size(); i++) {
                skiNames[i] = skis.get(i).getModel();
            }


            searchItemClientComboBox = createAutoCompleteSearchComboBox(clientsNames);
            searchItemSkiComboBox = createAutoCompleteSearchComboBox(skiNames);

            add(searchItemClientComboBox, clientItemComboBoxGBC);
            add(searchItemSkiComboBox, skiItemComboBoxGBC);

            //IMPORTANT
            revalidate();
            repaint();
        }
    }


    private static class RentEntityPanel extends EntityPanel<Rent, RentManager>  {
        ClientManager clientManager;
        SkiManager skiManager;

        JTextField selectedStartDate;
        JTextField selectedEndDate;
        JLabel selectedUpdatedEndDate;
        JTextField selectedComment;

        //AutoCompleteComboBox selectedItemRentStatusComboBox;
        AutoCompleteComboBox selectedItemClientComboBox;
        AutoCompleteComboBox selectedItemSkiComboBox;

        GridBagConstraints clientItemComboBoxGBC;
        GridBagConstraints skiItemComboBoxGBC;


        List<Client> clients;
        List<Ski> skis;


        RentEntityPanel(RentManager manager_, RentAppTab parent_, ClientManager clientManager_, SkiManager skiManager_){
            this.manager = manager_;
            this.parent = parent_;
            this.clientManager = clientManager_;
            this.skiManager = skiManager_;


            // Elements

            this.selectedStartDate = new JTextField("");
            this.selectedEndDate = new JTextField("");
            this.selectedUpdatedEndDate = new JLabel("");
            this.selectedComment = new JTextField("");


            JLabel startDateLabel = new JLabel("Start date:  ");
            JLabel endDateLabel = new JLabel("End date:  ");
            JLabel updatedEndDateLabel = new JLabel("Updated end date:  ");
            JLabel commentLabel = new JLabel("Description:  ");
            JLabel clientComboBoxLabel = new JLabel("Client:  ");
            JLabel skiComboBoxLabel = new JLabel("Ski:  ");
            JLabel rentStatusComboBoxLabel = new JLabel("Rent status:  ");


            // ComboBoxes:

            this.clients = this.clientManager.getEntitiesList();
            String[] clientsNames = new String[this.clients.size()];
            for (int i = 0; i < this.clients.size(); i++) {
                clientsNames[i] = this.clients.get(i).getFirstName();
            }

            this.skis = this.skiManager.getEntitiesList();
            String[] skiNames = new String[this.skis.size()];
            for (int i = 0; i < this.skis.size(); i++) {
                skiNames[i] = this.skis.get(i).getModel();
            }


            selectedItemClientComboBox = new AutoCompleteComboBox(clientsNames);
            selectedItemSkiComboBox = new AutoCompleteComboBox(skiNames);
            //selectedItemRentStatusComboBox = new AutoCompleteComboBox(RentStatus.values());


            EditEntityButton<Rent, RentManager> changeRentToReturnedButton = new EditEntityButton<>("Returned", this);
            changeRentToReturnedButton.addActionListener((e)->{


                Rent returnedRent = new Rent(
                        selectedEntity.getRentID(),
                        selectedEntity.getStartDate(),
                        selectedEntity.getEndDate(),
                        selectedEntity.getUpdatedEndDate(),
                        selectedEntity.getSkiModel(),
                        selectedEntity.getClientID(),
                        selectedEntity.getComment(),
                        RentStatus.RETURNED
                );

                try {
                    System.out.println("attempt to edit entity using return button");
                    manager.editEntity(selectedEntity, returnedRent);
                    repeatSearch(); // doesn't throw errors
                } catch (SkiAppException returnItemFailed) {
                    System.out.println("Failed returning an item " + selectedEntity.toString() +  " Error: " + returnItemFailed);
                }

            });


            CreateNewEntityButton<Rent, RentManager> createNewEntityButton = new CreateNewEntityButton<>("New", this);
            EditEntityButton<Rent, RentManager> editEntityButton = new EditEntityButton<>("Edit", this);
            DeleteEntityButton<Rent, RentManager> deleteEntityButton = new DeleteEntityButton<>("Delete", this);



            //Layout
            Dimension defaultFieldDimension = new Dimension(200, 24);
            startDateLabel.setPreferredSize(defaultFieldDimension);
            endDateLabel.setPreferredSize(defaultFieldDimension);
            updatedEndDateLabel.setPreferredSize(defaultFieldDimension);
            commentLabel.setPreferredSize(defaultFieldDimension);
            clientComboBoxLabel.setPreferredSize(defaultFieldDimension);
            skiComboBoxLabel.setPreferredSize(defaultFieldDimension);
            rentStatusComboBoxLabel.setPreferredSize(defaultFieldDimension);


            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();


            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            int gap = 3;
            gbc.insets = new Insets(gap, gap, gap, gap);
            gbc = createGbc(0, 0);
            gbc.weightx = 0.5;


            add(startDateLabel, gbc);

            gbc.gridx += 1; // One to the right

            gbc.gridwidth = 2; // wider element
            add(selectedStartDate, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(endDateLabel, gbc);

            gbc.gridx += 1; // One to the right

            gbc.gridwidth = 2; // wider element
            add(selectedEndDate, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(updatedEndDateLabel, gbc);

            gbc.gridx += 1; // One to the right
            gbc.gridwidth = 2; // wider element
            add(selectedUpdatedEndDate, gbc);
            gbc.gridwidth = 1;

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;

            add(commentLabel, gbc);

            gbc.gridx += 1; // One to the right
            gbc.gridwidth = 2; // wider element
            add(selectedComment, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(clientComboBoxLabel, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element

            clientItemComboBoxGBC = (GridBagConstraints) gbc.clone();

            add(selectedItemClientComboBox, clientItemComboBoxGBC);

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(skiComboBoxLabel, gbc);


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element

            skiItemComboBoxGBC = (GridBagConstraints) gbc.clone();

            add(selectedItemSkiComboBox, skiItemComboBoxGBC);

            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(rentStatusComboBoxLabel, gbc);


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            gbc.gridwidth = 3; // wider element
            add(changeRentToReturnedButton, gbc);
            //add(selectedItemRentStatusComboBox, gbc);
            gbc.gridwidth = 1;


            // Next Row -> Reset Position
            gbc.gridy += 1;
            gbc.gridx = 0;
            add(createNewEntityButton, gbc);
            gbc.gridx += 1;
            add(editEntityButton, gbc);
            gbc.gridx += 1;
            add(deleteEntityButton, gbc);
        }


        @Override
        public void onEntityLoaded(Rent selectedEntity) {

            selectedStartDate.setText(Util.dateToString(selectedEntity.getStartDate()));
            selectedEndDate.setText(Util.dateToString(selectedEntity.getEndDate()));
            selectedUpdatedEndDate.setText(Util.dateToString(selectedEntity.getUpdatedEndDate()));


            selectedComment.setText(selectedEntity.getComment());


            String clientID = selectedEntity.getClientID();
            String skiModel = selectedEntity.getSkiModel();
            String rentStatus = selectedEntity.getStatus().name();

            // TODO: optimise this  | save this value and use it here String[] skiTypeNames = new String[skiTypes.size()];

            int itemClientIndex = 0;
            for (int i = 0; i < selectedItemClientComboBox.getItemCount(); i++){
                if(clients.get(i).getDocId().equals(clientID)){
                    itemClientIndex = i;
                    break;
                }
            }
            selectedItemClientComboBox.setSelectedIndex(itemClientIndex);

            int itemSkiIndex = 0;
            for (int j = 0; j < selectedItemSkiComboBox.getItemCount(); j++){
                if(selectedItemSkiComboBox.getItemAt(j).equals(skiModel)){
                    itemSkiIndex = j;
                    break;
                }
            }
            selectedItemSkiComboBox.setSelectedIndex(itemSkiIndex);

//            int itemRentStatusIndex = 0;
//            for (int k = 0; k < selectedItemRentStatusComboBox.getItemCount(); k++){
//                if(selectedItemRentStatusComboBox.getItemAt(k).equals(rentStatus)){
//                    itemRentStatusIndex = k;
//                    break;
//                }
//            }
//            selectedItemRentStatusComboBox.setSelectedIndex(itemRentStatusIndex);
        }

        @Override
        public void reloadComboBox() {
            remove(selectedItemClientComboBox);
            remove(selectedItemSkiComboBox);


            clients = clientManager.getEntitiesList();
            String[] clientsNames = new String[clients.size()];
            for (int i = 0; i < clients.size(); i++) {
                clientsNames[i] = clients.get(i).getFirstName();
            }

            skis = skiManager.getEntitiesList();
            String[] skiNames = new String[skis.size()];
            for (int i = 0; i < skis.size(); i++) {
                skiNames[i] = skis.get(i).getModel();
            }


            selectedItemClientComboBox = new AutoCompleteComboBox(clientsNames);
            selectedItemSkiComboBox = new AutoCompleteComboBox(skiNames);

            add(selectedItemClientComboBox, clientItemComboBoxGBC);
            add(selectedItemSkiComboBox, skiItemComboBoxGBC);

        }

        @Override
        Rent loadItemData() {
            //TODO fix bug with adding new rent

            LocalDate startDate = null;
            try {
                startDate = Util.stringToDate(selectedStartDate.getText());
            } catch (ParseException ignored) {}
            LocalDate endDate = null;
            try {
                endDate = Util.stringToDate(selectedEndDate.getText());
            } catch (ParseException ignored) {}

            String comment = selectedComment.getText();

            String SkiModel = skis.get(selectedItemSkiComboBox.getSelectedIndex()).getModel();
            String clientID = clients.get(selectedItemClientComboBox.getSelectedIndex()).getDocId();
            //RentStatus rentStatus = RentStatus.valueOf(selectedItemRentStatusComboBox.getSelectedItem().toString());


            UUID uuid = null;
            RentStatus status = RentStatus.ACTIVE;
            if (selectedEntity != null) {
                uuid = selectedEntity.getRentID();
                status = selectedEntity.getStatus();
            }
            try {
                return new Rent(
                        uuid, startDate, endDate, null, SkiModel, clientID, comment, status
                );
            } catch (Exception e) {
                System.out.println("failed loading data: " + e);
                return null;
            }

        }
    }

    //endregion Rent





    //region Main Screen

    private static class TabButton extends Button implements ActionListener {
        private final JPanel panel;

        TabButton(String buttonText, JPanel panel_){
            super(buttonText);
            addActionListener(this);
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


    //public static void

    public static void main(String[] args) throws SkiAppException {
        boolean success = managersSetup();
        if (!success){
            return;
        }
        //populateData();

        createMainFrame();

        // reference to the main object
        driver = new SkiDriver();

        SignalSender signalSender = new SignalSender();


        SkiAppTab skiAppTab = new SkiAppTab(skiManager, skiTypeManager, signalSender);

        SkiTypeAppTab skiTypeAppTab = new SkiTypeAppTab(skiTypeManager, skiAppTab);

        ClientAppTab clientAppTab = new ClientAppTab(clientManager, signalSender);

        RentAppTab rentAppTab = new RentAppTab(rentManger, clientManager, skiManager);
        signalSender.rentAppTab = rentAppTab;

        ReportAppTab reportAppTab = new ReportAppTab(reportManager);


        // Main Space
        tabSpace = new JPanel();
        windowSpace = new JPanel();
        tabSpace.setLayout(new GridBagLayout());

        JPanel windowSpace2 = new JPanel(new GridBagLayout());

        tabSpace.add(skiTypeAppTab);


        mainFrame.setLayout(new GridBagLayout());


        // Tab Selection Buttons
        TabButton skiTypesTabButton = new TabButton("skitype", skiTypeAppTab);
        TabButton skiTabButton = new TabButton("ski", skiAppTab);
        TabButton clientTabButton = new TabButton("client", clientAppTab);
        TabButton rentTabButton = new TabButton("rent", rentAppTab);

        TabButton reportTabButton = new TabButton("reports", reportAppTab);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int gap = 3;
        gbc.insets = new Insets(0, 0, gap, 0);
        gbc = createGbc(0, 0);
        gbc.weightx = 0.5;


        windowSpace2.add(skiTypesTabButton, gbc);
        gbc.gridx += 1;
        windowSpace2.add(skiTabButton, gbc);
        gbc.gridx += 1;
        windowSpace2.add(clientTabButton, gbc);
        gbc.gridx += 1;
        windowSpace2.add(rentTabButton, gbc);
        gbc.gridx += 1;
        windowSpace2.add(reportTabButton, gbc);

        gbc.gridwidth = 5;
        gbc.gridx = 0;
        gbc.gridy += 1;
        windowSpace2.add(tabSpace, gbc);



        windowSpace.add(windowSpace2);
        JScrollPane uwuScroll = new JScrollPane(windowSpace);
        uwuScroll.setMinimumSize(new Dimension(mainFrame.getContentPane().getSize()));
        mainFrame.add(uwuScroll, gbc);


        driver.refresh();
    }

    static class ErrorPopup implements ActionListener{
        Popup popup;
        JLabel label;
        JFrame jframe;
        PopupFactory popupFactory;
        JPanel popupPanel;
        JButton okButton;
        ErrorPopup(JFrame frame){
            jframe = frame;
            popupPanel = new JPanel();

            label = new JLabel();
            okButton = new JButton("OK");
            okButton.addActionListener(this);

//            popupPanel.setPreferredSize(new Dimension(400, 300));
            popupPanel.setLayout(new GridBagLayout());
            popupPanel.setBackground(new Color(219,219,219));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(6,6,6,6);
            constraints.gridx = 0;
            constraints.gridy = 0;
            popupPanel.add(label, constraints);
            constraints.gridy = 3;
            popupPanel.add(okButton, constraints);

            popupFactory = new PopupFactory();

            popup = popupFactory.getPopup(
                    jframe,
                    popupPanel,
                    (jframe.getX() + jframe.getWidth() / 2) - popupPanel.getWidth() / 2,
                    (jframe.getY() + jframe.getHeight() / 2) - popupPanel.getHeight() / 2
            );

        }
        public void actionPerformed(ActionEvent event){
            String actionCommand = event.getActionCommand();
            if (actionCommand.equals("OK")) {
                popup.hide();
                popup = popupFactory.getPopup(
                        jframe,
                        popupPanel,
                        (jframe.getX() + jframe.getWidth() / 2) - popupPanel.getWidth() / 2,
                        (jframe.getY() + jframe.getHeight() / 2) - popupPanel.getHeight() / 2
                );
            }
            else{
                popup.show();
            }

        }

        public void show(String errorMsg){
            this.label.setText(errorMsg);
            this.popup.show();
            System.out.println(popupPanel.getWidth());
            System.out.println(popupPanel.getHeight());
        }

        public void show(){
            if(!(this.label.getText().isEmpty())){
                this.popup.show();
            }
        }

        public void hide(){
            this.popup.hide();
        }

        public void changedFrame(JFrame frame){
            this.jframe = frame;
            popup = popupFactory.getPopup(
                    jframe,
                    popupPanel,
                    (jframe.getX() + jframe.getWidth() / 2) - popupPanel.getWidth() / 2,
                    (jframe.getY() + jframe.getHeight() / 2) - popupPanel.getHeight() / 2
            );
        }

    }


    static class SignalSender {
        public RentAppTab rentAppTab;
        public void sendReload() {
            if (rentAppTab != null) {
                rentAppTab.reloadComboBox();
            }
        }
    }


}

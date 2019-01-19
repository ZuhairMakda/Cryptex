package guicontrollers;

import coin.Coin;
import coin.CoinList;
import coin.SortOrder;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import util.APINotRespondingException;
import util.Logger;
import util.search.Search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CoinMainController implements Initializable{
    @FXML private AnchorPane top;
    @FXML private JFXHamburger hamburger;
    @FXML private AnchorPane drawer;
    @FXML private BorderPane menuBorder;
    @FXML private AnchorPane menuOpen;
    @FXML private TableColumn likeCol;
    @FXML private TableColumn numCol;
    @FXML private TableColumn<Coin, String> codeCol;
    @FXML private TableColumn<Coin, String> nameCol;
    @FXML private TableColumn<Coin, String> priceCol;
    @FXML private TableColumn<Coin, String> capCol;
    @FXML private TableColumn changeCol;
    @FXML private TableView<Coin> tableView;
    @FXML private AnchorPane infoPane;
    @FXML private Pagination coinPage;
    @FXML private JFXButton refreshButton;
    @FXML private MenuButton sortButton;
    @FXML private Label coinNameLabel;
    @FXML private Label codeLabel;
    @FXML private Label priceLabel;
    @FXML private Label capLabel;
    @FXML private Label volumeLabel;
    @FXML private Label supplyLabel;
    @FXML private ImageView coinImage;
    @FXML private JFXTextField searchBar;
    @FXML private LineChart<?,?> coinGraph;
    @FXML private Label sortLabel;

    private static TableView<Coin> table2;

    private HamburgerBasicCloseTransition transition;
    private int start = 0;

    private static ObservableList<Coin> coin = FXCollections.observableArrayList();

    @FXML
    public void showHand(){
        top.setCursor(Cursor.HAND);
    }

    @FXML
    public void showDefault(){
        top.setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void hamClick() {
        Timeline openNav = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(drawer.layoutXProperty(), drawer.getLayoutX())),
                new KeyFrame(new Duration(350), new KeyValue(drawer.layoutXProperty(), 0)));
        Timeline closeNav = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(drawer.layoutXProperty(), drawer.getLayoutX())),
                new KeyFrame(new Duration(350), new KeyValue(drawer.layoutXProperty(), -200)));
        Timeline open = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(menuOpen.prefWidthProperty(), menuOpen.getWidth())),
                new KeyFrame(new Duration(350), new KeyValue(menuOpen.prefWidthProperty(), 200)));
        Timeline close = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(menuOpen.prefWidthProperty(), menuOpen.getWidth())),
                new KeyFrame(new Duration(350), new KeyValue(menuOpen.prefWidthProperty(), 0)));
        if(start == 0){
            start = 1;
            openNav.play();
            open.play();
        }
        else if(start == 1){
            start = 0;
            closeNav.play();
            close.play();
        }
        hamburger.setDisable(true);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(350),
                ae -> hamburger.setDisable(false)));
        timeline.play();
        transition.setRate(transition.getRate()*-1);
        transition.play();
    }

    public TableView<Coin> createPage(int pageIndex){
        tableView.scrollTo(0);

        priceCol.prefWidthProperty().bind(tableView.widthProperty().subtract(225).divide(4));
        nameCol.prefWidthProperty().bind(tableView.widthProperty().subtract(225).divide(4));
        capCol.prefWidthProperty().bind(tableView.widthProperty().subtract(225).divide(4));
        changeCol.prefWidthProperty().bind(tableView.widthProperty().subtract(225).divide(4));

        nameCol.setReorderable(false);
        priceCol.prefWidthProperty().bind(tableView.widthProperty().subtract(165).divide(4));
        nameCol.prefWidthProperty().bind(tableView.widthProperty().subtract(165).divide(4));
        capCol.prefWidthProperty().bind(tableView.widthProperty().subtract(165).divide(4));
        changeCol.prefWidthProperty().bind(tableView.widthProperty().subtract(165).divide(4));
        
        /*nameCol.setReorderable(false);
        priceCol.setReorderable(false);
        codeCol.setReorderable(false);
        numCol.setReorderable(false);
        capCol.setReorderable(false);
	*/
        //numCol.setCellValueFactory(new PropertyValueFactory<Coin, Integer>("number"));
        codeCol.setCellValueFactory(new PropertyValueFactory<Coin, String>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Coin, String>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Coin, String>("displayPrice"));
        capCol.setCellValueFactory(new PropertyValueFactory<Coin, String>("displayMarketCap"));
        changeCol.setCellValueFactory(new PropertyValueFactory<Coin, String>("displayDailyChangePercent"));

        //https://stackoverflow.com/questions/6998551/setting-font-color-of-javafx-tableview-cells
        changeCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn param) {
                return new TableCell<Coin, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        double temp;
                        super.updateItem(item, empty);
                        if (item != null) {
                            if(item.contains("-")){
                                this.setTextFill(Color.RED);
                            }
                            else {
                                this.setTextFill(Color.GREEN);
                            }
                            setText(item);
                        }
                    }
                };
            }
        });

        //tableView.scrollTo(100);

        //Add to table
        if((pageIndex+1) != (int)Math.ceil((double)CoinList.getList().length/CoinList.MAX_MARKET_INPUT)){
            tableView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(CoinList.getList()).subList(0+(pageIndex*CoinList.MAX_MARKET_INPUT),(CoinList.MAX_MARKET_INPUT)+(pageIndex*CoinList.MAX_MARKET_INPUT))));
        }

        else{
            tableView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(CoinList.getList()).subList(0+(pageIndex*CoinList.MAX_MARKET_INPUT),CoinList.getList().length)));
        }
        doubleClickedRow();
        numCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Coin, Coin>, ObservableValue<Coin>>() {
            @Override public ObservableValue<Coin> call(TableColumn.CellDataFeatures<Coin, Coin> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numCol.setCellFactory(new Callback<TableColumn<Coin, Coin>, TableCell<Coin, Coin>>() {
            @Override public TableCell<Coin, Coin> call(TableColumn<Coin, Coin> param) {
                return new TableCell<Coin, Coin>() {
                    @Override protected void updateItem(Coin item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+pageIndex*CoinList.MAX_MARKET_INPUT+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        return tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sortButton.setDisable(true);
        // https://stackoverflow.com/questions/16384879/auto-numbered-table-rows-javafx
        numCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Coin, Coin>, ObservableValue<Coin>>() {
            @Override public ObservableValue<Coin> call(TableColumn.CellDataFeatures<Coin, Coin> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numCol.setCellFactory(new Callback<TableColumn<Coin, Coin>, TableCell<Coin, Coin>>() {
            @Override public TableCell<Coin, Coin> call(TableColumn<Coin, Coin> param) {
                return new TableCell<Coin, Coin>() {
                    @Override protected void updateItem(Coin item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });

        likeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Coin, Coin>, ObservableValue<Coin>>() {
            @Override public ObservableValue<Coin> call(TableColumn.CellDataFeatures<Coin, Coin> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        likeCol.setCellFactory(new Callback<TableColumn<Coin, Coin>, TableCell<Coin, Coin>>() {
            @Override public TableCell<Coin, Coin> call(TableColumn<Coin, Coin> param) {
                return new TableCell<Coin, Coin>() {
                    @Override protected void updateItem(Coin item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            BorderPane border = new BorderPane();
                            MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.STAR_OUTLINE);
                            icon.setCursor(Cursor.HAND);
                            icon.setGlyphSize(30);
                            border.setCenter(icon);
                            setGraphic(border);
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });

        searchBar.textProperty().addListener((obs, oldText, newText ) -> {
            if(newText.isEmpty()) {
                coinPage.setPageCount((int) Math.ceil((double) CoinList.getList().length / CoinList.MAX_MARKET_INPUT));
                createPage(0);
            }
            else {
                coinPage.setPageCount(1);
            	search();
            }
        });
 
        MenuItem alphabetical = new MenuItem("Alphabetical");
        alphabetical.setOnAction((event) -> {
            sortList(SortOrder.ALPHABETICAL);
            coinPage.setCurrentPageIndex(0);
        });
        
        MenuItem price = new MenuItem("Price");
        price.setOnAction((event) -> {
            sortList(SortOrder.PRICE);
            coinPage.setCurrentPageIndex(0);
        });
        
        MenuItem mktcap = new MenuItem("Market");
        mktcap.setOnAction((event) -> {
            sortList(SortOrder.MKTCAP);
            coinPage.setCurrentPageIndex(0);
        });
        
        MenuItem change = new MenuItem("24h Change");
        change.setOnAction((event) -> {
            sortList(SortOrder.CHANGE);
            coinPage.setCurrentPageIndex(0);
        });
        
        sortButton.getItems().clear();
        sortButton.getItems().add(alphabetical);
        sortButton.getItems().add(price);
        sortButton.getItems().add(mktcap);
        sortButton.getItems().add(change);
        
        coinPage.setPageCount((int)Math.ceil((double)CoinList.getList().length/CoinList.MAX_MARKET_INPUT));
        coinPage.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > (int)Math.ceil((double)CoinList.getList().length/CoinList.MAX_MARKET_INPUT)) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });
        coin = getCoin();
        transition = new HamburgerBasicCloseTransition(hamburger);
        transition.setRate(-1);
        try {
            menuBorder.setCenter(FXMLLoader.load(getClass().getResource("/resources/fxml/SideMenu.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startLoad();
    }
    
    private void sortList(SortOrder s) {
    	CoinList.sort(s);
    	Logger.info("Searching by " + s.toString());
    	sortLabel.setText("Sorted by " + s.toString());
    	createPage(0);
    }

    public static void setCoinArray(ObservableList<Coin> coin) {
        CoinMainController.coin = coin;
    }

    private ObservableList<Coin> getCoin() {
        return coin;
    }

    public static TableView<Coin> getTable2() {
        return table2;
    }

    private void doubleClickedRow(){
        tableView.setRowFactory( tv -> {
            TableRow<Coin> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                coinGraph.getData().clear();
                if (e.getClickCount() == 2 && (!row.isEmpty()) ) {
                    infoPane.toFront();
                    coinNameLabel.setText(row.getItem().getName());
                    codeLabel.setText(row.getItem().getCode());
                    priceLabel.setText("Price: " + row.getItem().getDisplayPrice());
                    capLabel.setText("Market Cap: " + row.getItem().getDisplayMarketCap());
                    try {
                        volumeLabel.setText("Volume 24H: " + row.getItem().getDisplayVolume24H("USD"));
                    } catch (APINotRespondingException e1) {
                        e1.printStackTrace();
                    };
                    supplyLabel.setText("Total Supply: " + row.getItem().getTotalSupply());
                    try {
                        coinImage.setImage(new Image("https://www.cryptocompare.com"+row.getItem().getImagePath()));
                    } catch (APINotRespondingException e1) {
                        e1.printStackTrace();
                    }
                    double[] graphY = new double[0];
                    XYChart.Series series = new XYChart.Series();

                    try {
                        graphY = row.getItem().getDailyHistorical("USD", "01/01/2017", "01/01/2018");
                    } catch (APINotRespondingException e1) {
                        e1.printStackTrace();
                    }

                    for(int i = 1; i <= 30; i++){
                        series.getData().add(new XYChart.Data(Integer.toString(i), graphY[i-1]));
                    }
                    coinGraph.getData().addAll(series);
                }
            });
            return row;
        });
    }

    @FXML public void closeInfo(){
        infoPane.toBack();
    }
    
    private void search() {
    	Coin[] list = (Coin[]) Search.search(CoinList.getAlphabeticalList(), searchBar.getText());
        tableView.setItems(FXCollections.observableArrayList(list));
    	tableView.refresh();
    }
    
    private void startLoad() {
    	/*Runnable runnable = new Runnable() {
			@Override
			public void run() {
				runLoad();
			}
		};
		
		Thread loadThread = new Thread(runnable);
		loadThread.setDaemon(true);
		loadThread.start();*/
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                runLoad();
                return null ;
            }
        };
        task.setOnSucceeded(e -> {
            sortButton.setDisable(false);
        });
        new Thread(task).start();
    }

    private void runLoad() {
    	try {
    		while(!CoinList.marketDataFullyLoaded())
    			CoinList.loadNextMarketData(CoinList.MAX_MARKET_INPUT, "USD");
    		
    			Logger.info("Finished loading market data");
		} catch (APINotRespondingException e) {
			e.printStackTrace();
		}
    }
    
    @FXML public void refreshClicked(){
    	CoinList.resetMarketData();
		try {
			CoinList.loadNextMarketData(CoinList.MAX_MARKET_INPUT, "USD");
		} catch (APINotRespondingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	createPage(0);
    	startLoad();
    }
    
    @FXML public void sortClicked(){
    	  
    }
}

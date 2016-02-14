package huju;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import huju.mcu.comm.SerialCommunicator;
import huju.mcu.comm.SerialCommunicatorProvider;
import huju.mcu.ServiceProvider;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MotionDetect;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import huju.mcu.comm.CommunicationDataListener;

public class HouseAutomation extends Application implements CommunicationDataListener
{
	private static boolean DEBUG = true;
	private static final int MAX_DATA_POINTS_HUMIDITY = 50;
	private static final int MAX_DATA_POINTS_MD1 = 50;
    private int xSeriesData = 0;
    private int xSeriesDataMD1 = 0;
    private int xSeriesDataMD1_comp = 0;
    private int xSeriesDataMD2 = 0;
    private int waitTime = 3;
    private XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> mdSeries1 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> mdSeries1_comp = new XYChart.Series<>();
    private XYChart.Series<Number, Number> mdSeries2 = new XYChart.Series<>();

    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> dataMD1_Q1 = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> dataMD1_Comp = new ConcurrentLinkedQueue<>();
    
    private ConcurrentLinkedQueue<Number> dataMD2 = new ConcurrentLinkedQueue<>();
    
    private int motionDet1_State = 0; 
    private int motionDet1_CompState = 0; 
    private int md1SecondsTillLast = 0;
    private int motionDet2_State = 0;
    
    private static final String DEFAULT_PLATFORM = SerialCommunicatorProvider.RASBPERRY_PI;
    private SerialCommunicator communicator = 
    		SerialCommunicatorProvider.getSerialCommunicator(DEFAULT_PLATFORM);
    
    private Text lblHumidity = new Text("RH=");
    private Text lblTemperature = new Text("T=");
    private Text valueHumidity = new Text("   "); 
    private Text valueTemperature = new Text("   ");
    private NumberAxis xAxis;
    
    private NumberAxis xAxisMD1;
    private NumberAxis xAxisMD1_comp;
    private NumberAxis xAxisMD2;
    
    private ExecutorService executor;
    
    private LineChart initHumTemLineChart()
    {
    	xAxis = new NumberAxis(0, MAX_DATA_POINTS_HUMIDITY, MAX_DATA_POINTS_HUMIDITY / 10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);

        // Create a LineChart
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        //lineChart.setTitle("Animated Line Chart");
        lineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        series1.setName("Humidity");
        series2.setName("Temperature");

        // Add Chart Series
        lineChart.getData().addAll(series1, series2);
        return lineChart;
    }
    
    private LineChart initMotioDetectorLineChart1()
    {
    	xAxisMD1 = new NumberAxis(0, MAX_DATA_POINTS_MD1, MAX_DATA_POINTS_MD1 / 10);
    	xAxisMD1.setForceZeroInRange(false);
    	xAxisMD1.setAutoRanging(false);
    	xAxisMD1.setTickLabelsVisible(true);
    	xAxisMD1.setTickMarkVisible(false);
    	xAxisMD1.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);

        // Create a LineChart
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxisMD1, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        //lineChart.setTitle("Animated Line Chart");
        lineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        mdSeries1.setName("DIY Motion");

        // Add Chart Series
        lineChart.getData().addAll(mdSeries1);
        return lineChart;
    }
    
    private LineChart initCompensatedMotioDetectorLineChart1()
    {
    	xAxisMD1_comp = new NumberAxis(0, MAX_DATA_POINTS_MD1, MAX_DATA_POINTS_MD1 / 10);
    	xAxisMD1_comp.setForceZeroInRange(false);
    	xAxisMD1_comp.setAutoRanging(false);
    	xAxisMD1_comp.setTickLabelsVisible(true);
    	xAxisMD1_comp.setTickMarkVisible(true);
    	xAxisMD1_comp.setMinorTickVisible(true);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);

        // Create a LineChart
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxisMD1_comp, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        lineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        mdSeries1_comp.setName("DIY Motion Compensated");

        // Add Chart Series
        lineChart.getData().addAll(mdSeries1_comp);
        return lineChart;
    }
    
    
    private LineChart initMotioDetectorLineChart2()
    {
    	xAxisMD2 = new NumberAxis(0, MAX_DATA_POINTS_MD1, MAX_DATA_POINTS_MD1 / 10);
    	xAxisMD2.setForceZeroInRange(false);
    	xAxisMD2.setAutoRanging(false);
    	xAxisMD2.setTickLabelsVisible(true);
    	xAxisMD2.setTickMarkVisible(false);
    	xAxisMD2.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);

        // Create a LineChart
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxisMD2, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        //lineChart.setTitle("Animated Line Chart");
        lineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        mdSeries2.setName("HC-SR501");

        // Add Chart Series
        lineChart.getData().addAll(mdSeries2);
        return lineChart;
    }
    

    private void init(Stage primaryStage) {
    	LineChart lineChartHumTem = initHumTemLineChart();
    	LineChart motionLine1 = initMotioDetectorLineChart1();
    	LineChart motionLine1_comp = initCompensatedMotioDetectorLineChart1();
    	LineChart motionLine2 = initMotioDetectorLineChart2();
    	
    	VBox root = new VBox(10);
    	HBox labels = new HBox(4);
    	HBox md1Layout = new HBox(2);
    	
    	final BorderPane humTemLayout = new BorderPane();
    	humTemLayout.setPrefSize(600,400);
        humTemLayout.setStyle("-fx-border-color: black;");
    	
        Font f1 = Font.font("Verdana", FontWeight.BOLD, 20);
        Font f2 = Font.font("Verdana", FontWeight.NORMAL, 20);
        lblHumidity.setFont(f2);
        lblTemperature.setFont(f2);
        
        valueHumidity.setFont(f1);
        valueHumidity.setWrappingWidth(100);
        valueHumidity.setTextAlignment(TextAlignment.JUSTIFY);
        
        valueTemperature.setFont(f1);
        valueTemperature.setWrappingWidth(100);
        valueTemperature.setTextAlignment(TextAlignment.JUSTIFY);
        
        labels.getChildren().add(lblHumidity);
        labels.getChildren().add(valueHumidity);
        labels.getChildren().add(lblTemperature);
        labels.getChildren().add(valueTemperature);
    	
    	humTemLayout.setTop(labels);
    	humTemLayout.setCenter(lineChartHumTem);
    	
    	final BorderPane md1BorderLayout = new BorderPane();
    	//md1BorderLayout.setPrefSize(1024,300);
    	md1BorderLayout.setStyle("-fx-border-color: black;");
    	//md1BorderLayout.setLeft(motionLine1);
    	//md1BorderLayout.setRight(motionLine1_comp);
    	md1Layout.setPrefSize(1024,300);
    	md1Layout.getChildren().addAll(motionLine1, motionLine1_comp);
    	md1Layout.autosize();
    	
    	md1BorderLayout.setCenter(md1Layout);
    	
        
        root.getChildren().add(humTemLayout);
        root.getChildren().add(md1BorderLayout);
        root.getChildren().add(motionLine2);
        
        //primaryStage.setScene(new Scene(lineChart));
        
        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);

    }


    @Override
    public void start(Stage stage) {
        stage.setTitle("Hoiuse Automation Demo");
        init(stage);
        stage.show();


        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        
        InternalTimer timer = new InternalTimer();
        executor.execute(timer);

        //AddToQueue addToQueue = new AddToQueue();
        //executor.execute(addToQueue);
        
        communicator.setCommunicationDataListener(this);
        String port = SerialCommunicatorProvider.getDefaultPort(DEFAULT_PLATFORM);
        communicator.openPort(port, 115200);
        //-- Prepare Timeline
        prepareTimeline();
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ1.isEmpty()) break;
            series1.getData().add(new XYChart.Data<>(xSeriesData++, dataQ1.remove()));
            series2.getData().add(new XYChart.Data<>(xSeriesData++, dataQ2.remove()));
            
        }
        
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataMD1_Q1.isEmpty()) break;
            mdSeries1.getData().add(new XYChart.Data<>(xSeriesDataMD1++, dataMD1_Q1.remove()));
        }
        
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataMD1_Comp.isEmpty()) break;
            mdSeries1_comp.getData().add(new XYChart.Data<>(xSeriesDataMD1_comp++, dataMD1_Comp.remove()));
        }
        
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataMD2.isEmpty()) break;
            mdSeries2.getData().add(new XYChart.Data<>(xSeriesDataMD2++, dataMD2.remove()));
        }
        
        
        
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series1.getData().size() > MAX_DATA_POINTS_HUMIDITY) {
            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS_HUMIDITY);
        }
        if (series2.getData().size() > MAX_DATA_POINTS_HUMIDITY) {
            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS_HUMIDITY);
        }
        if (mdSeries1.getData().size() > MAX_DATA_POINTS_MD1) {
        	mdSeries1.getData().remove(0, mdSeries1.getData().size() - MAX_DATA_POINTS_MD1);
        }
        if (mdSeries2.getData().size() > MAX_DATA_POINTS_MD1) {
        	mdSeries2.getData().remove(0, mdSeries2.getData().size() - MAX_DATA_POINTS_MD1);
        }
        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS_HUMIDITY);
        xAxis.setUpperBound(xSeriesData - 1);
        
        xAxisMD1.setLowerBound(xSeriesDataMD1 - MAX_DATA_POINTS_MD1);
        xAxisMD1.setUpperBound(xSeriesDataMD1 - 1);
        
        
        xAxisMD1_comp.setLowerBound(xSeriesDataMD1_comp - MAX_DATA_POINTS_MD1);
        xAxisMD1_comp.setUpperBound(xSeriesDataMD1_comp - 1);
        
        
        xAxisMD2.setLowerBound(xSeriesDataMD2 - MAX_DATA_POINTS_MD1);
        xAxisMD2.setUpperBound(xSeriesDataMD2 - 1);
    }
    
    protected void timeExceed()
    {
    	//dataMD1_Q1.add(motionDet1_State);
    	md1SecondsTillLast++;
		if (md1SecondsTillLast >= waitTime) {
			if (motionDet1_CompState==1 ){
				motionDet1_CompState = 0;
			}
			dataMD1_Q1.add(motionDet1_State);
			
		} 
		dataMD1_Comp.add(motionDet1_CompState);
		dataMD2.add(motionDet2_State);
    	
    }
    
    
    private class InternalTimer implements Runnable 
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HouseAutomation.this.timeExceed();
			try {
				Thread.sleep(1000);
				executor.execute(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
    	
	}


	@Override
	public void dataReceived(MCUData data) {
		if (DEBUG) {
			System.out.println(data.toString());
		}
		if (data.getDeviceType()==DeviceType.TEMPERATURE_HUMIDITY_SENSOR) {
			HumidityTemperature ht = (HumidityTemperature)data;
			dataQ1.add(ht.getHumidity());
			dataQ2.add(ht.getTemperature());
			valueHumidity.setText(ht.getHumidity()+" %");
			String temp = ht.getTemperature()+" "+((char) 8451);
			valueTemperature.setText(temp);
			
		} else if (data.getDeviceType() == DeviceType.MOTION_DETECTOR) {
			MotionDetect md = (MotionDetect) data;
			if (md.getSourceBus() == SourceBus.GPIO_EXT2_5) {
				int level = md.getPinLevel();
				
				dataMD1_Q1.add(level);
				md1SecondsTillLast = 0;					
				motionDet1_State = level;
				if (level==1 && motionDet1_CompState==0) {
					motionDet1_CompState = 1;
					dataMD1_Comp.add(level);
				}
			} else if (md.getSourceBus() == SourceBus.GPIO_EXT1_5) {
				int level = md.getPinLevel();
				dataMD2.add(level);
				motionDet2_State = level;
			} else {
				System.out.println(data.toString());
			}
		} 
	}
		

	
    public static void main(String[] args) {
    	for (String a : args) {
    		if (a.toLowerCase().equals("nodebug")) {
    			DEBUG = false;
    		}
    		
    	}
        launch(args);
    }
    
}

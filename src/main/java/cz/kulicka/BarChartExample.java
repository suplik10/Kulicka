package cz.kulicka;

import cz.kulicka.entity.ChartKline;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RectangularShape;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BarChartExample extends JFrame {

    ArrayList<ChartKline> chartKlines = new ArrayList<>();

    public BarChartExample(final String title) {
        super(title);
        IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1800, 500));
        chartPanel.setMouseWheelEnabled(true);
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private IntervalXYDataset createDataset() {

        Macd macdDataset = new Macd();
        chartKlines = macdDataset.getDataSet();

        //List<ChartKline> chartKlinesc = chartKlines.subList(250,270);

        final XYSeries series = new XYSeries("Hna hna data");

        for (ChartKline chartKline : chartKlines) {
            series.add(chartKline.getClosedDate().getTime(), chartKline.getValue());
        }

        final XYSeriesCollection dataset = new XYSeriesCollection(series);
        return dataset;
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset the dataset.
     * @return A sample chart.
     */
    private JFreeChart createChart(IntervalXYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYBarChart(
                "money money money",
                "X",
                true,
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );


        XYToolTipGenerator xyToolTipGenerator = (dataset1, series, item) -> {
            Number x1 = chartKlines.get(item).getClosedPrice();
            Date closedDate = chartKlines.get(item).getClosedDate();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("<html><p style='color:#0000ff;'>Serie: '%s'</p>", dataset1.getSeriesKey(series)));
            stringBuilder.append(String.format("X:'%d'<br/>", x1.intValue()));
            stringBuilder.append(String.format("closed date: " + closedDate.toString()));
            stringBuilder.append("</html>");
            return stringBuilder.toString();
        };


        XYPlot plot = (XYPlot) chart.getPlot();
//        XYItemRenderer renderer2 = plot.getRenderer();
//        renderer.setDefaultToolTipGenerator(xyToolTipGenerator);
//        renderer.setSeriesPaint(0, Color.black.darker());


        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);

        final DateAxis dateaxis = (DateAxis) plot.getDomainAxis();
        DateTickUnit unit = new DateTickUnit(DateTickUnitType.MONTH, 12);
        dateaxis.setTickUnit(unit);
        dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateaxis.setTickMarksVisible(Boolean.FALSE);
        dateaxis.setDateFormatOverride(new SimpleDateFormat("MMM d HH:mm:ss"));
        dateaxis.setAutoTickUnitSelection(Boolean.TRUE);

        ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer(1, true);
        renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {

            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                Date d = new Date((long) dataset.getXValue(series, item));
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d HH:mm:ss");
                String s = sdf.format(d);
                s += String.format(" MACD: %.0f", dataset.getY(series, item));
                s += ("<br/> Closed price: " + chartKlines.get(item).getClosedPrice());
                return s;
            }
        });

        XYBarPainter painter = new XYBarPainter() {

            @Override
            public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
                // TODO Auto-generated method stub

            }

            @Override
            public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) {
                // TODO Auto-generated method stub
                bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() + 8, bar.getHeight());
                // g2.setBackground(Color.GREEN);
                g2.setColor(Color.green.darker());
                g2.fill(bar);
                g2.draw(bar);

            }
        };
        renderer.setBarPainter(painter);
        plot.setRenderer(renderer);
        return chart;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {

        final BarChartExample demo = new BarChartExample("XY Series Demo 3");
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
}
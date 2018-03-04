package cz.kulicka;

import cz.kulicka.entity.ChartKline;
import cz.kulicka.entity.MacdIndicator;
import cz.kulicka.service.MacdIndicatorService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
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

public class BarChartJframe extends JFrame {

    ArrayList<ChartKline> chartKlines = new ArrayList<>();

    MacdIndicatorService macdIndicatorService;

    public BarChartJframe(final String title, MacdIndicatorService macdIndicatorService) {
        super(title);
        this.macdIndicatorService = macdIndicatorService;
        IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1800, 500));
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

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(1l);

        for (float macdIndicator1 : macdIndicator.getMacdList()) {
            series.add(3d, macdIndicator1);
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


}
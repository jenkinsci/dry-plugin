package hudson.plugins.dry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;

import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.graph.CategoryBuildResultGraph;
import hudson.plugins.analysis.graph.ColorPalette;
import hudson.plugins.analysis.graph.GraphConfiguration;
import hudson.plugins.analysis.util.ToolTipProvider;

/**
 * Builds a graph showing the total number of duplicated lines in a line graph.
 *
 * @author Ulli Hafner
 */
public class DuplicatedLinesGraph extends CategoryBuildResultGraph {
    @Override
    public String getId() {
        return "DUPLINES";
    }

    @Override
    public String getLabel() {
        return Messages.Trend_Type_duplicated_lines();
    }

    // FIXME: analysis-core 2.0: use real type
    @Override
    protected List<Integer> computeSeries(final BuildResult current) {
        List<Integer> series = new ArrayList<Integer>();
        if (current instanceof DryResult) {
            series.add(((DryResult) current).getTotalNumberOfDuplicatedLines());
        }
        return series;
    }

    @Override
    protected JFreeChart createChart(final CategoryDataset dataSet) {
        return  createLineGraph(dataSet, false, Messages.Trend_duplicated_lines_yAxisLabel());
    }

    @Override
    protected Color[] getColors() {
        return new Color[] {ColorPalette.BLUE};
    }

    @Override
    protected CategoryItemRenderer createRenderer(final GraphConfiguration configuration, final String pluginName, final ToolTipProvider toolTipProvider) {
        return createLineRenderer();
    }
}


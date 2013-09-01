package hudson.plugins.dry;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

import hudson.plugins.analysis.views.WarningsCountColumn;

import hudson.views.ListViewColumnDescriptor;

/**
 * A column that shows the total number of code duplications in a job.
 *
 * @author Ulli Hafner
 */
public class DryColumn extends WarningsCountColumn<DryProjectAction> {
    /**
     * Creates a new instance of {@link DryColumn}.
     */
    @DataBoundConstructor
    public DryColumn() { // NOPMD: data binding
        super();
    }

    @Override
    protected Class<DryProjectAction> getProjectAction() {
        return DryProjectAction.class;
    }

    @Override
    public String getColumnCaption() {
        return Messages.DRY_Warnings_ColumnHeader();
    }

    /**
     * Descriptor for the column.
     */
    @Extension
    public static class ColumnDescriptor extends ListViewColumnDescriptor {
        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.DRY_Warnings_Column();
        }
    }
}

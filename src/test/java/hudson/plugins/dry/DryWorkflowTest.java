package hudson.plugins.dry;

import hudson.FilePath;
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class DryWorkflowTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    /**
     * Run a workflow job using {@link DryPublisher} and check for success.
     */
    @Test
    public void dryPublisherWorkflowStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("cpd.xml");
        report.copyFrom(getClass().getResourceAsStream("./parser/cpd/cpd.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'DryPublisher', highThreshold: 50, normalThreshold: 25])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(0, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link DryPublisher} with a failing threshold of 0, so the given example file
     * "/hudson/plugins/dry/parser/cpd/cpd.xml" will make the build to fail.
     */
    @Test
    public void dryPublisherWorkflowStepSetLimits() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStepSetLimits");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("cpd.xml");
        report.copyFrom(getClass().getResourceAsStream("./parser/cpd/one-cpd.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'DryPublisher', pattern: '**/cpd.xml', highThreshold: 50, normalThreshold:" +
                        " 25, failedTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(0, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link DryPublisher} with a unstable threshold of 0, so the given example file
     * "/hudson/plugins/dry/parser/cpd/cpd.xml" will make the build to fail.
     */
    @Test
    public void dryPublisherWorkflowStepFailure() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStepFailure");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("cpd.xml");
        report.copyFrom(getClass().getResourceAsStream("./parser/cpd/cpd.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'DryPublisher', pattern: '**/lint-results.xml', highThreshold: 50, " +
                        "normalThreshold: 25, unstableTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n")
        );
        jenkinsRule.assertBuildStatus(Result.UNSTABLE, job.scheduleBuild2(0).get());
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(0, result.getResult().getAnnotations().size());
    }

}

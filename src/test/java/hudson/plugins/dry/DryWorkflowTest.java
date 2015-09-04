package hudson.plugins.dry;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

import hudson.FilePath;
import hudson.model.Result;
import hudson.tasks.Maven;

/**
 * In this test suite we initialize the Job workspaces with a resource (maven-project1.zip) that contains a Maven
 * project with pmd-cpd configured.
 */
public class DryWorkflowTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    private static Maven.MavenInstallation mavenInstallation;

    @BeforeClass
    public static void init() throws Exception {
        mavenInstallation = jenkinsRule.configureMaven3();
    }

    /**
     * Run a workflow job using {@link DryPublisher} and check for success.
     */
    @Test @Ignore("not compatible with workflow 1.5")
    public void dryPublisherWorkflowStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        workspace.unzipFrom(DryWorkflowTest.class.getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                        + "  sh \"${mvnHome}/bin/mvn clean install\"\n"
                        + "  step([$class: 'DryPublisher'])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(2, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link DryPublisher} with a failing threshold of 0.
     */
    @Test @Ignore("not compatible with workflow 1.5")
    public void dryPublisherWorkflowStepSetLimits() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStepSetLimits");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        workspace.unzipFrom(DryWorkflowTest.class.getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                        + "  sh \"${mvnHome}/bin/mvn clean install\"\n"
                        + "  step([$class: 'DryPublisher', pattern: '**/cpd.xml', highThreshold: 50, normalThreshold:" +
                        " 25, failedTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(2, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link DryPublisher} with a unstable threshold of 0.
     */
    @Test @Ignore("not compatible with workflow 1.5")
    public void dryPublisherWorkflowStepFailure() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "dryPublisherWorkflowStepFailure");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        workspace.unzipFrom(DryWorkflowTest.class.getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                        + "  sh \"${mvnHome}/bin/mvn clean install\"\n"
                        + "  step([$class: 'DryPublisher', pattern: '**/cpd.xml', highThreshold: 50, " +
                        "normalThreshold: 25, unstableTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatus(Result.UNSTABLE, job.scheduleBuild2(0).get());
        DryResultAction result = job.getLastBuild().getAction(DryResultAction.class);
        assertEquals(2, result.getResult().getAnnotations().size());
    }
}

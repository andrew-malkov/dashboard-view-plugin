package hudson.plugins.view.dashboard.test;

import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.TestResultProjectAction;

import java.util.Collection;

public class TestUtil {
	
	/**
	 * Summarize the last test results from the passed set of jobs.  If a job
	 * doesn't include any tests, add a 0 summary.
	 * 
	 * @param jobs
	 * @return
	 */
	public static TestResultSummary getTestResultSummary(Collection<Job> jobs) {
		TestResultSummary summary = new TestResultSummary();
		
		for (Job job : jobs) {
			boolean addBlank = true;
			TestResultProjectAction testResults = job.getAction(TestResultProjectAction.class);
			String description = job.getLastBuild().getDescription();
			
			if (testResults != null) {
				AbstractTestResultAction tra = testResults.getLastTestResultAction();
				
				if (tra != null) {
					AbstractTestResultAction traPrevious = tra.getPreviousResult();
					int total = tra.getTotalCount();
					int diff = total;
					if (traPrevious != null) {
						diff = total - traPrevious.getTotalCount();
					}
					
					addBlank = false;
					summary.addTestResult(
						new TestResult(job, total, tra.getFailCount(), tra.getSkipCount(), description, diff)
					);
				}
			}
			
			if (addBlank) {
				summary.addTestResult(new TestResult(job, 0, 0, 0, "", 0));
			}
		}
		
		return summary;
	}
	
	public static TestResult getTestResult(Run run) {
		TestResultAction tra = run.getAction(TestResultAction.class);
		
		if (tra != null) {
			return new TestResult(run.getParent(), tra.getTotalCount(), tra.getFailCount(), tra.getSkipCount(), "", 0);
		} else {
			return new TestResult(run.getParent(), 0, 0, 0, "", 0);
		}
	}
}

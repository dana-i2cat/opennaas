package cat.i2cat.manticore.test.demo.jobs;

import org.apache.axis.message.addressing.EndpointReferenceType;
import cat.i2cat.manticore.stubs.ipnetwork.CommitQueueReq;
import cat.i2cat.manticore.stubs.ipnetwork.CommitQueueResp;
import cat.i2cat.manticore.test.wrappers.ipnetwork.IPNetworkWrapper;

/**
 * This Jobs send the execute command to the IPnetwork-WS resource to execute all 
 * the programmed actions in queue.
 * 
 * @author Xavi Barrera
 *
 */
public class ExecuteQueueJob {
	private EndpointReferenceType ipnetworkEPR;
	
	public ExecuteQueueJob(EndpointReferenceType ipnetworkEPR) {
		this.ipnetworkEPR = ipnetworkEPR;
	}
	
	public int run() {
		CommitQueueReq request = new CommitQueueReq();
		try {
			CommitQueueResp response = IPNetworkWrapper.commitQueue(request , ipnetworkEPR);
			int errorCode = response.getErrorCode();
			return errorCode;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}

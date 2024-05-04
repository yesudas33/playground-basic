package interceptor;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * The TimerInterceptor class implements the IClientInterceptor interface to measure response times
 * for FHIR server requests and calculate average response times over multiple requests.
 */
public class TimerInterceptor implements IClientInterceptor {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TimerInterceptor.class);
    private long totalResponseTime = 0;
    private int counter=-1; //starting count from -1 to account for 1st handshake call for the FHIR Rest client
    private int LOOP_COUNT;
    private int NAME_COUNT;

    /**
     * Constructs a new TimerInterceptor with the specified loop count and name count.
     *
     * @param LOOP_COUNT The number of times the search operation is repeated.
     * @param NAME_COUNT The number of patient names used for each search operation.
     */
    public TimerInterceptor(int LOOP_COUNT, int NAME_COUNT) {
        this.LOOP_COUNT = LOOP_COUNT;
        this.NAME_COUNT = NAME_COUNT;
    }

    /**
     * This method is called before a request is sent to the server. It does nothing in this implementation.
     *
     * @param iHttpRequest The HTTP request being intercepted.
     */
    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {
        // This method is intentionally left blank as it is not used in this implementation.
    }

    /**
     * This method is called after a response is received from the server.
     * It measures the response time and logs the average time after every NAME_COUNT requests.
     *
     * @param iHttpResponse The HTTP response received from the server.
     * @throws IOException If an I/O error occurs while processing the response.
     */
    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {

        StopWatch stopWatch=iHttpResponse.getRequestStopWatch();
        long responseTimeMillis = (stopWatch.getMillis()); // Convert to milliseconds
        log.info("TIMER INTERCEPTOR:Response time for query : " + responseTimeMillis + " ms");
        if (counter!=-1)    totalResponseTime += responseTimeMillis;  //this condition is added to ignore the first handshake call of Fhir REST Client
        counter++;
        if (counter%NAME_COUNT==0 && counter>0) {
            log.info("-------------------------");
            log.info("TIMER INTERCEPTOR: SET "+counter/NAME_COUNT+": average time :"+totalResponseTime + " ms");
            log.info("-------------------------");
            totalResponseTime=0;
        }

    }
}

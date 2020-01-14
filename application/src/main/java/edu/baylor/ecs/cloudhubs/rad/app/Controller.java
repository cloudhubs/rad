package edu.baylor.ecs.cloudhubs.rad.app;

import edu.baylor.ecs.cloudhubs.rad.context.RequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.ResponseContext;
import edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * This class defines an endpoint to accept requests to the RAD Application.
 * It takes {@link RequestContext} as request.
 * And generates {@link ResponseContext} as response.
 * It delegates the request to {@link edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService} for analysis/processing.
 *
 * @author Dipta Das
 */

@RestController
@AllArgsConstructor
public class Controller {
    private final RestDiscoveryService restDiscoveryService;

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    @ResponseBody
    public ResponseContext getRadResponseContext(@RequestBody RequestContext request) {
        return restDiscoveryService.generateResponseContext(request);
    }
}

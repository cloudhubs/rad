package edu.baylor.ecs.cloudhubs.rad.app;

import edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext;
import edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * This class defines an endpoint to accept requests to the RAD Application.
 * It takes {@link edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext} as request.
 * And generates {@link edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext} as response.
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
    public RadResponseContext getRadResponseContext(@RequestBody RadRequestContext request) {
        return restDiscoveryService.generateRadResponseContext(request);
    }
}

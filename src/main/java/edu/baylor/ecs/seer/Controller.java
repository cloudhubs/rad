package edu.baylor.ecs.seer;

import edu.baylor.ecs.seer.context.RadRequestContext;
import edu.baylor.ecs.seer.context.RadResponseContext;
import edu.baylor.ecs.seer.service.RestDiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

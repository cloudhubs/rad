package edu.baylor.ecs.cloudhubs.rad.sample;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SampleService {
    private final RestTemplate restTemplate;
    private final String url = "http://localhost:8080/rad/sample";

    public SampleService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public SampleModel doGetForObject() {
        return restTemplate.getForObject(url + "/get-mapping", SampleModel.class);
    }

    public SampleModel doGetForEntity() {
        return restTemplate.getForEntity(url + "/request-mapping-get", SampleModel.class).getBody();
    }

    public SampleModel doExchangeGet() {
        return restTemplate.exchange(url + "/get-mapping", HttpMethod.GET, null, SampleModel.class).getBody();
    }

    public SampleModel doPostForObject(SampleModel sampleModel) {
        return restTemplate.postForObject(url + "/post-mapping", sampleModel, SampleModel.class);
    }

    public SampleModel doPostForEntity(SampleModel sampleModel) {
        return restTemplate.postForEntity(url + "/request-mapping-post", sampleModel, SampleModel.class).getBody();
    }

    public SampleModel doExchangePost(SampleModel sampleModel) {
        return restTemplate.exchange(url + "/post-mapping", HttpMethod.POST, null, SampleModel.class, sampleModel).getBody();
    }

}

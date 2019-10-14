package edu.baylor.ecs.seer.dataflow;

import org.springframework.web.client.RestTemplate;

public class SampleRestClient {

    public void restCall01() {
        RestTemplate restTemplate = new RestTemplate();
        SampleModel sampleModel = restTemplate.getForObject("localhost:8080/user", SampleModel.class);
    }

    public void restCall02() {
        RestTemplate restTemplate = new RestTemplate();
        SampleModel[] sampleModel = restTemplate.getForObject("localhost" + ":8080" + "/user", SampleModel[].class);
    }

    public void restCall03() {
        String s1 = "localhost";
        String s2 = s1 + ":8080";

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(s2 + "/user", SampleModel[].class);
    }

    public void restCall04(String s) {
        RestTemplate restTemplate = new RestTemplate();
        SampleModel sampleModel = restTemplate.getForObject(s + "/user", SampleModel.class);
    }

    public void restCall05() {
        RestTemplate restTemplate = new RestTemplate();
        String s1 = "localhost";
        String s2 = ":8080";
        SampleModel sampleModel = restTemplate.getForObject(s1 + s2 + "/user", SampleModel.class);
    }
}

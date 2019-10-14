package edu.baylor.ecs.seer.dataflow;

import org.springframework.web.client.RestTemplate;

public class SampleRestClient {

    public void restCall01() {
        String str = "abc";
        String xyz = str + "pqr";

        RestTemplate restTemplate = new RestTemplate();
        SampleModel sampleModel = restTemplate.getForObject(xyz + "/def", SampleModel.class);
    }

    public void restCall02() {
        String str = "abc";
        String xyz = str + "pqr";

        RestTemplate restTemplate = new RestTemplate();
        SampleModel[] sampleModel = restTemplate.getForObject(xyz + "/def", SampleModel[].class);
    }

    public void restCall03() {
        String str = "abc";
        String xyz = str + "pqr";

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(xyz + "/def", SampleModel[].class);
    }
}

package com.one.learn.resttemplate;

import com.one.learn.resttemplate.bean.Product;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.Assert.isTrue;

/**
 * @author One
 * @Description RestTemplate 请求测试类
 * @date 2019/05/09
 */
public class RestTemplateTests {
    RestTemplate restTemplate = new RestTemplate();
    RestTemplate customRestTemplate = null;

    @Before
    public void setup() {
        customRestTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        // 连接超时设置 10s
        clientHttpRequestFactory.setConnectTimeout(10_000);

        // 读取超时设置 10s
        clientHttpRequestFactory.setReadTimeout(10_000);
        return clientHttpRequestFactory;
    }

    @Test
    public void testGet_product1() {
        String url = "http://localhost:8080/product/get_product1";
        String result = restTemplate.getForObject(url, String.class);
        System.out.println("get_product1返回结果：" + result);
        Assert.hasText(result, "get_product1返回结果为空");

        Product product = restTemplate.getForObject(url, Product.class);
        System.out.println("get_product1返回结果：" + product);
        Assert.notNull(product, "get_product1返回结果为空");

        ResponseEntity<Product> responseEntity = restTemplate.getForEntity(url, Product.class);
        System.out.println("get_product1返回结果：" + responseEntity);
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "get_product1响应不成功");

        MultiValueMap header = new LinkedMultiValueMap();
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Object> requestEntity = new HttpEntity<>(header);
        ResponseEntity<Product> exchangeResult = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Product.class);
        System.out.println("get_product1返回结果：" + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "get_product1响应不成功");

        String executeResult = restTemplate.execute(url, HttpMethod.GET, request -> {
            request.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }, (clientHttpResponse) -> {
            InputStream body = clientHttpResponse.getBody();
            byte[] bytes = new byte[body.available()];
            body.read(bytes);
            return new String(bytes);
        });
        System.out.println("get_product1返回结果：" + executeResult);
        Assert.hasText(executeResult, "get_product1返回结果为空");
    }

    @Test
    public void testGet_product2() {
        String url = "http://localhost:8080/product/get_product2?id={id}";
        ResponseEntity<Product> responseEntity = restTemplate.getForEntity(url, Product.class, 101);
        System.out.println(responseEntity);
        isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "get_product2 请求不成功");
        Assert.notNull(responseEntity.getBody().getId(), "get_product2  传递参数不成功");

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id", 101);
        Product result = restTemplate.getForObject(url, Product.class, uriVariables);
        System.out.println(result);
        Assert.notNull(result.getId(), "get_product2  传递参数不成功");
    }

    @Test
    public void testPost_product1() {
        String url = "http://localhost:8080/product/post_product1";
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.add(HttpHeaders.CONTENT_TYPE, (MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        Product product = new Product(201, "Macbook", BigDecimal.valueOf(10000));
        String productStr = "id=" + product.getId() + "&name=" + product.getName() + "&price=" + product.getPrice();
        HttpEntity<String> request = new HttpEntity<>(productStr, header);
        ResponseEntity<String> exchangeResult = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        System.out.println("post_product1: " + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "post_product1 请求不成功");

        MultiValueMap<String, Object> map = new LinkedMultiValueMap();
        map.add("id", (product.getId()));
        map.add("name", (product.getName()));
        map.add("price", (product.getPrice()));
        HttpEntity<MultiValueMap> request2 = new HttpEntity<>(map, header);
        ResponseEntity<String> exchangeResult2 = restTemplate.exchange(url, HttpMethod.POST, request2, String.class);
        System.out.println("post_product1： " + exchangeResult2);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "post_product1 请求不成功");
    }

    @Test
    public void testPost_product2() {
        String url = "http://localhost:8080/product/post_product2";
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        header.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<Product> request = new HttpEntity<>(new Product(2, "Macbook", BigDecimal.valueOf(10000)), header);
        ResponseEntity<String> exchangeResult = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        System.out.println("post_product2: " + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "post_product2 请求不成功");
    }

    @Test
    public void testDelete() {
        String url = "http://localhost:8080/product/delete/{id}";
        restTemplate.delete(url, 101);
    }

    @Test
    public void testPut() {
        String url = "http://localhost:8080/product/update";
        Map<String, ?> variables = new HashMap<>();
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        Product product = new Product(101, "iWatch", BigDecimal.valueOf(2333));
        String productStr = "id=" + product.getId() + "&name=" + product.getName() + "&price=" + product.getPrice();
        HttpEntity<String> request = new HttpEntity<>(productStr, header);
        restTemplate.put(url, request);
    }

    @Test
    public void testUploadFile() {
        String url = "http://localhost:8080/product/upload";
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Object file = new FileSystemResource(new File("/Users/One/Desktop/b.txt"));
        body.add("file", file);

        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.MULTIPART_FORM_DATA_VALUE));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, header);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("upload: " + responseEntity);
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "upload 请求不成功");
    }

}
